package de.immerarchiv.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.frittenburger.web.impl.WebServerImpl;
import de.frittenburger.web.interfaces.WebServer;
import de.immerarchiv.job.impl.ApplicationState;
import de.immerarchiv.job.impl.BagItScanJob;
import de.immerarchiv.job.impl.FileScanJob;
import de.immerarchiv.job.impl.FolderScanJob;
import de.immerarchiv.job.impl.RepositoryScanJob;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagItList;
import de.immerarchiv.job.model.FileList;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.util.impl.BagItCacheImpl;
import de.immerarchiv.util.impl.MD5CacheImpl;
import de.immerarchiv.util.impl.MD5ServiceImpl;
import de.immerarchiv.util.interfaces.BagItCache;
import de.immerarchiv.util.interfaces.MD5Cache;
import de.immerarchiv.util.interfaces.MD5Service;

public class Service {

	private final static Logger logger = LogManager.getLogger(Service.class);

	//infrastructure
	private final static MD5Service md5service = new MD5ServiceImpl();
	private final static MD5Cache md5cache = new MD5CacheImpl(new File("working/md5cache.txt"));
	private final static BagItCache bagItCache = new BagItCacheImpl(new File("working/bagitCache"));

	public static void main(String[] args) throws Exception {
	
		
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
		List<Job> jobs = new ArrayList<Job>();
		
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
					e.printStackTrace();
					ApplicationState.incr("jobs-errors");
				}
				logger.info("finish job {}",currentJob);
				currentJob.finish();

			    if(currentJob instanceof FolderScanJob)
			    {
			    	FileList fileListAll = currentJob.getResult(FileList.class);
			    	for(int i = 0;i < fileListAll.size();i+= 200)
			    	{
			    		List<String> fileList = new ArrayList<String>();
			    		for(int ix = i;ix < i + 200 && ix < fileListAll.size();ix++)
			    			fileList.add(fileListAll.get(ix));
						jobs.add(new FileScanJob(md5service,md5cache,fileList));
			    	}
			    	
			    }
			    if(currentJob instanceof RepositoryScanJob)
			    {
			    	RepositoryScanJob repositoryScanJob = (RepositoryScanJob)currentJob;
			    	BagItList bagItListAll = repositoryScanJob.getResult(BagItList.class);
			    	
			    	for(int i = 0;i < bagItListAll.size();i+= 20)
			    	{
			    		BagItList bagItList = new BagItList();
			    		for(int ix = i;ix < i + 20 && ix < bagItListAll.size();ix++)
			    			bagItList.add(bagItListAll.get(ix));
						jobs.add(new BagItScanJob(repositoryScanJob.getRepositoryService(),bagItCache,bagItList));
			    	}
			    	
			    }
				
				
				currentJob = null;
				
			}
			
			//currentJob == null
			ApplicationState.set("jobs-cnt",jobs.size());
			if(jobs.size() > 0)
			{
				logger.info("queued jobs {}",jobs.size());

				currentJob = jobs.remove(0);
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

				List<String> folders = new ArrayList<String>();
				for(PathConfig pathConfig : config.pathes)
				{
					folders.add(pathConfig.path);
				}
				jobs.add(new FolderScanJob(folders));

				int repoId = 0;
				for(RepositoryConfig repoConfig : config.repositories)
				{
					repoId++;
					RepositoryService service = 
							new RepositoryService(repoConfig.url,repoConfig.name, repoConfig.token);
					jobs.add(new RepositoryScanJob(""+repoId,service));
				}
				
				
				nextscann = new Date().getTime() + 1000 * 60 * 60;
				ApplicationState.set("jobs-nextscann",new Date(nextscann));
				ApplicationState.set("config-content",config);
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
