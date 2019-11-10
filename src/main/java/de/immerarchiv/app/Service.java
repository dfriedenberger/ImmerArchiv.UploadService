package de.immerarchiv.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.frittenburger.web.impl.WebServerImpl;
import de.frittenburger.web.interfaces.WebServer;
import de.immerarchiv.job.impl.ApplicationState;
import de.immerarchiv.job.impl.ArchivImpl;
import de.immerarchiv.job.impl.BestBagitStrategy;
import de.immerarchiv.job.impl.FileIgnoreFilterImpl;
import de.immerarchiv.job.impl.FolderFileComparerServiceImpl;
import de.immerarchiv.job.impl.FolderScanJob;
import de.immerarchiv.job.impl.FolderSystemImpl;
import de.immerarchiv.job.impl.RepositoryScanJob;
import de.immerarchiv.job.impl.SynchronizeJob;
import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.FileIgnoreFilter;
import de.immerarchiv.job.interfaces.FolderFileComparerService;
import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FileSystemState;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.util.impl.BagItCacheImpl;
import de.immerarchiv.util.impl.MD5CacheImpl;
import de.immerarchiv.util.impl.MD5ServiceImpl;
import de.immerarchiv.util.impl.NameServiceImpl;
import de.immerarchiv.util.interfaces.BagItCache;
import de.immerarchiv.util.interfaces.MD5Cache;
import de.immerarchiv.util.interfaces.MD5Service;
import de.immerarchiv.util.interfaces.NameService;

public class Service {

	private final static Logger logger = LogManager.getLogger(Service.class);

	//infrastructure
	private final static MD5Service md5service = new MD5ServiceImpl();
	private final static FolderFileComparerService comparerService = new FolderFileComparerServiceImpl();
	private final static NameService nameService = new NameServiceImpl();
	private final static BestBagitStrategy bestBagitStrategy = new BestBagitStrategy();
	private static MD5Cache md5cache = null;
	private static BagItCache bagItCache = null;

	public static void main(String[] args) throws Exception {
	
		
		File working = new File("working");
		working.mkdir();
		File bagitCache = new File(working,"bagitCache");
		bagitCache.mkdir();
		new File(working,"log").mkdir();

		//caches
		md5cache = new MD5CacheImpl(new File(working,"md5cache.txt"));
		bagItCache = new BagItCacheImpl(bagitCache);
		
		
		File configFile = new File("config.yml");
		YAMLFactory yf = new YAMLFactory();

		if(!configFile.exists())
		{
			new ObjectMapper(yf).writerWithDefaultPrettyPrinter().writeValue(configFile,Config.defaultConfig());
		}
		
		
		
		
		
		WebServer webserver = null;
		Config config = null;
		long nextscann = 0;
		long lastRead = 0;

		ApplicationState.set("jobs-errors",0);

		Job currentJob = null;
        PriorityQueue<Job> jobs = new PriorityQueue<>((j1, j2) -> {
            return j1.priority().getValue() - j2.priority().getValue();
        });
 
		
		while(true)
		{
			ApplicationState.set("heartbeat",new Date());

			if(configFile.lastModified() > lastRead)
			{
				config = new ObjectMapper(yf).readValue(configFile,Config.class);
				lastRead = new Date().getTime();
				ApplicationState.set("config-read",new Date());
				ApplicationState.set("config-content",config);				
			}
			
			if(webserver == null)
			{
				webserver = new WebServerImpl(config.server.port);
				webserver.start();
			}
			
			if(currentJob != null)
			{
				logger.trace("next job {}",currentJob);
				ApplicationState.incr("jobs-current-step");

				try
				{
					if(currentJob.next())
						continue;
				} 
				catch(Exception e)
				{
					logger.error("job failed {}",currentJob);

					logger.error(e);
					ApplicationState.incr("jobs-errors");
				}
				
				logger.info("finish job {}",currentJob);
				List<Job> nextJobs = currentJob.getNext();
				
				if(nextJobs != null)
			    {
					logger.info("add next jobs {}",nextJobs.size());
			    	jobs.addAll(nextJobs);
			    }
			   
				currentJob = null;
				
			}
			
			//currentJob == null
			ApplicationState.set("jobs-cnt",jobs.size());
			if(!jobs.isEmpty())
			{
				logger.info("queued jobs {}",jobs.size());

				currentJob = jobs.remove();
				logger.info("init job {}",currentJob);
				currentJob.init();
				ApplicationState.set("jobs-current-start",new Date());
				ApplicationState.set("jobs-current-step",0);
				ApplicationState.set("jobs-current-name",currentJob.getClass().getSimpleName());

				continue;
			}
			
			
			//currentJob == null && jobs.size() == 0 //jede Stunde
			if(nextscann  < new Date().getTime())
			{

				int repoId = 0;
				List<RepositoryService> repositoryServices = new ArrayList<>(); 
				for(RepositoryConfig repoConfig : config.repositories)
				{
					repoId++;
					repositoryServices.add(new RepositoryService(""+repoId,repoConfig.url,repoConfig.name, repoConfig.token));
				}
				
				String[] repositories = repositoryServices.stream().map(rs -> rs.getId()).collect(Collectors.toList()).toArray(new String[0]);
				
				Archiv archiv = new ArchivImpl(repositories, comparerService,nameService,bestBagitStrategy);
				FolderSystem folderSystem = new FolderSystemImpl();
				FileSystemState fileSystemState = new FileSystemState();
				FileIgnoreFilter fileIgnoreFilter = new FileIgnoreFilterImpl();
				
				for(String pattern : config.ignore)
				{
					fileIgnoreFilter.addPattern(pattern);
				}
				
				for(PathConfig pathConfig : config.pathes)
				{
					Folder folder = new Folder();
					folder.setPath(pathConfig.path);
					folderSystem.addFolder(folder);
				}
				jobs.add(new FolderScanJob(md5service, nameService, md5cache, folderSystem,fileIgnoreFilter,fileSystemState));

				for(RepositoryService respositoryService :repositoryServices)
				{
					jobs.add(new RepositoryScanJob(archiv,bagItCache, respositoryService));
				}
				
				jobs.add(new SynchronizeJob(repositoryServices,archiv, folderSystem,fileSystemState));
				
				
				nextscann = new Date().getTime() + 1000 * 60 * 60;
				ApplicationState.set("jobs-nextscann",new Date(nextscann));
				ApplicationState.set("config-content",config);
				ApplicationState.setFileSystemState(fileSystemState);

			}
			
			
			ApplicationState.set("jobs-current-start","-");
			ApplicationState.set("jobs-current-step",0);
			ApplicationState.set("jobs-current-name","-");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e);
			}

			
		}
		
		
		
		
		
		
		
	}

	

}
