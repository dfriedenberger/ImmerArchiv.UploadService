package de.immerarchiv.job.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.job.interfaces.FileIgnoreFilter;
import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FileSystemState;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.Priority;
import de.immerarchiv.util.interfaces.MD5Cache;
import de.immerarchiv.util.interfaces.MD5Service;
import de.immerarchiv.util.interfaces.NameService;

public class FolderScanJob implements Job {

	private final static Logger logger = LogManager.getLogger(FolderScanJob.class);

	private static final long SIZE100MB = 1025 * 1024 * 100;
	
	private final MD5Service md5service;
	private final NameService nameService;

	private final MD5Cache md5cache;
	
	private final FolderSystem folderSystem;
	private final List<Folder> folderQueue;
	private final FileIgnoreFilter fileIgnoreFilter;
	private final FileSystemState fileSystemState;



	@Override
	public Priority priority() {
		return Priority.FolderScan;
	}
	
	@Override
	public void init() {
		this.folderQueue.addAll(folderSystem.getFolders());
	}
	
	public FolderScanJob(MD5Service md5service,NameService nameService,MD5Cache md5cache,FolderSystem folderSystem,FileIgnoreFilter fileIgnoreFilter,FileSystemState fileSystemState)
	{
		this.md5service = md5service;
		this.nameService = nameService;
		this.md5cache = md5cache;
		this.folderSystem = folderSystem;
		this.folderQueue = new ArrayList<>();
		this.fileIgnoreFilter = fileIgnoreFilter;
		this.fileSystemState = fileSystemState;
	}

	@Override
	public boolean next() throws IOException {

		if(folderQueue.isEmpty()) 
			throw new IOException("has no folder to scann");
		
		Folder folder = folderQueue.remove(0);
		File dir = new File(folder.getPath());
		
		if(!dir.isDirectory())
			throw new IOException(dir + " has to be a folder");

		File[] folders = dir.listFiles();
		if(folders == null)
		{
			logger.warn("can not list files for {}",folder);
			fileSystemState.setState(dir, "access denied");		
		}
		else if(folders.length == 0)
		{
			logger.warn("empty folder {}",folder);
			fileSystemState.setState(dir, "empty folder");	
		}
		else
		{
			for(File file : folders)
			{
				if(file.isDirectory())
				{					
					Folder f = new Folder();
					f.setPath(file.getAbsolutePath());
					folderQueue.add(f);
					folderSystem.addFolder(f);
					continue;
				}
				
				if(file.isFile())
				{
					if(fileIgnoreFilter.ignore(file))
					{
						logger.info("Ignore File {}",file);
						fileSystemState.setState(file, "ignore");
						continue;
					}
					
					if(file.length() == 0)
					{
						logger.warn("Empty File {}",file);
						fileSystemState.setState(file, "empty file");
						continue;
					}
					
					
					
					FolderFile folderFile = new FolderFile();
					
					folderFile.setSafeName(nameService.createSafeName(file));
					folderFile.setLength(file.length());
					folderFile.setFile(file);
					folderSystem.addFile(folder,folderFile);
					continue;
				}
				
				throw new IOException("Unknonwn type "+file);
			}
		}
		return !folderQueue.isEmpty();
	}


	@Override
	public String toString() {
		return "FolderScanJob [folderlist=" + folderQueue.size() + "]";	
	}

	@Override
	public List<Job> getNext() {
		
		List<Job> jobs = new ArrayList<>();

		for(Folder folder : folderSystem.getFolders())
		{
			List<FolderFile> jobFiles = new ArrayList<>();
			List<FolderFile> files = folderSystem.selectFiles(folder);
			long size = 0;
	    	for(int i = 0;i < files.size();i++)
	    	{
	    		FolderFile file = files.get(i);
	    		size += file.getLength();
	    		jobFiles.add(file);
	    		
	    		if((size > SIZE100MB) || (jobFiles.size() >= 200) || (i + 1 == files.size()))
	    		{
	    			jobs.add(new FileScanJob(md5service,md5cache,jobFiles));
	    			jobFiles = new ArrayList<>();
	    			size = 0;
	    		}
	    		
	    	}
			
		}    	
		
		return jobs;
	}

	



}
