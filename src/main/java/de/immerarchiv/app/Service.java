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

import de.frittenburger.web.impl.ApplicationStateImpl;
import de.frittenburger.web.impl.WebServerImpl;
import de.frittenburger.web.interfaces.ApplicationState;
import de.frittenburger.web.interfaces.WebServer;
import de.immerarchiv.job.impl.ArchivImpl;
import de.immerarchiv.job.impl.BagItScanJob;
import de.immerarchiv.job.impl.BestBagitStrategy;
import de.immerarchiv.job.impl.FileIgnoreFilterImpl;
import de.immerarchiv.job.impl.FolderFileComparerServiceImpl;
import de.immerarchiv.job.impl.FolderScanJob;
import de.immerarchiv.job.impl.FolderSystemImpl;
import de.immerarchiv.job.impl.RepositoryScanJob;
import de.immerarchiv.job.impl.SynchronizeJob;
import de.immerarchiv.job.impl.UploadJob;
import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.FileIgnoreFilter;
import de.immerarchiv.job.interfaces.FolderFileComparerService;
import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FileSystemState;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.util.impl.BagItCacheImpl;
import de.immerarchiv.util.impl.CycleServiceImpl;
import de.immerarchiv.util.impl.FileChangesWatcherImpl;
import de.immerarchiv.util.impl.MD5CacheImpl;
import de.immerarchiv.util.impl.MD5ServiceImpl;
import de.immerarchiv.util.impl.NameServiceImpl;
import de.immerarchiv.util.impl.TimestampServiceImpl;
import de.immerarchiv.util.interfaces.BagItCache;
import de.immerarchiv.util.interfaces.CycleService;
import de.immerarchiv.util.interfaces.FileChangesWatcher;
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
	private final static CycleService cycleService = new CycleServiceImpl(new TimestampServiceImpl(),15,24 * 60);
	private final static ApplicationState applicationState = ApplicationStateImpl.getInstance();
	private static FileChangesWatcher fileChangesWatcher;
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
		
		//FileWatcher
		fileChangesWatcher = new FileChangesWatcherImpl();
		
		File configFile = new File("config.yml");
		YAMLFactory yf = new YAMLFactory();

		if(!configFile.exists())
		{
			new ObjectMapper(yf).writerWithDefaultPrettyPrinter().writeValue(configFile,Config.defaultConfig());
		}
		
		
		
		
		
		WebServer webserver = null;
		Config config = null;
		long lastRead = 0;


		Job currentJob = null;
        PriorityQueue<Job> jobs = new PriorityQueue<>((j1, j2) -> {
            return j1.priority().getValue() - j2.priority().getValue();
        });
 
		
		while(true)
		{
			applicationState.heartbeat();

			if(configFile.lastModified() > lastRead)
			{
				logger.info("config changed");

				config = new ObjectMapper(yf).readValue(configFile,Config.class);
				lastRead = new Date().getTime();
				
				//Trigger next scan
				cycleService.triggerCycle("ConfigChanged");
				applicationState.updateNextScan(cycleService.getNextCycle(),cycleService.getTrigger());

			}
			
			if(fileChangesWatcher.hasNewFiles())
			{
				logger.info("new files");
				
				//Trigger next scan
				cycleService.triggerCycle("NewFiles");
				applicationState.updateNextScan(cycleService.getNextCycle(),cycleService.getTrigger());
			}
			
			if(webserver == null)
			{
				webserver = new WebServerImpl(config.server.port);
				webserver.start();
			}
			
			if(currentJob != null)
			{
				logger.trace("next job {}",currentJob);
				applicationState.getJobState().incrCurrentStep();
				try
				{
					
					if(currentJob.next())
						continue;
					
					logger.info("finish job {}",currentJob);
					List<Job> nextJobs = currentJob.getNext();
					if(nextJobs != null)
				    {
						logger.info("add next jobs {}",nextJobs.size());
				    	jobs.addAll(nextJobs);
				    }
					
					
					if(currentJob instanceof UploadJob)
					{
						//cnt uploads
						UploadJob uploadJob = (UploadJob)currentJob;
						applicationState.addSuccessfulUpload(uploadJob);
					}
					
				} 
				catch(Exception e)
				{
					logger.error("job failed {}",currentJob);
					logger.error(e);
					
					applicationState.addError(e,currentJob);
					
					//if job is relevant for synchronize, cancel queue and restart in 5 Minutes 
					if((currentJob instanceof RepositoryScanJob)
							||(currentJob instanceof BagItScanJob))
					{
						cycleService.triggerCycle("NetworkError");
						applicationState.updateNextScan(cycleService.getNextCycle(),cycleService.getTrigger());
						jobs.clear();
						
					}
				}
				
				
				currentJob = null;
				applicationState.stopJob();			
				
			}
			
			//currentJob == null
			applicationState.getJobState().setJobCount(jobs.size());
			if(!jobs.isEmpty())
			{
				logger.info("queued jobs {}",jobs.size());

				currentJob = jobs.remove();
				logger.info("init job {}",currentJob);
				currentJob.init();
				
				applicationState.startJob(currentJob);
				continue;
			}
			
			
			//next Scan ??
			if(cycleService.IsNextCycle())
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
				
				fileChangesWatcher.deleteFolders();

				for(PathConfig pathConfig : config.pathes)
				{
					Folder folder = new Folder();
					folder.setPath(pathConfig.path);
					folderSystem.addFolder(folder);
					
					fileChangesWatcher.addFolder(pathConfig.path);
				}
				jobs.add(new FolderScanJob(md5service, nameService, md5cache, folderSystem,fileIgnoreFilter,fileSystemState));

				for(RepositoryService respositoryService :repositoryServices)
				{
					jobs.add(new RepositoryScanJob(archiv,bagItCache, respositoryService));
				}
				
				jobs.add(new SynchronizeJob(repositoryServices,archiv, folderSystem,fileSystemState));
				
				
				cycleService.incrCycle();
				applicationState.updateNextScan(cycleService.getNextCycle(),cycleService.getTrigger());
				applicationState.setNextFileSystemState(fileSystemState);

			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e);
			}

			
		}
		
		
		
		
		
		
		
	}

	

}
