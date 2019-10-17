package de.immerarchiv.job.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FileList;

public class FolderScanJob implements Job {

	private final static Logger logger = LogManager.getLogger(FolderScanJob.class);

	private final List<String> folderList;
	private final FileList fileList = new FileList();

	@Override
	public void init() {
		
	}
	
	public 	FolderScanJob(List<String> folderList)
	{
		this.folderList = folderList;
	}

	@Override
	public boolean next() throws IOException {

		if(folderList.isEmpty()) 
			throw new IOException("has no folder to scann");
		
		File folder = new File(folderList.remove(0));
		
		if(!folder.isDirectory())
			throw new IOException(folder + " has to be a folder");

		File[] folders = folder.listFiles();
		if(folders == null)
		{
			logger.error("can not list files for {}",folder);
		}
		else
		{
			for(File file : folders)
			{
				if(file.isDirectory())
				{
					folderList.add(file.getAbsolutePath());
					continue;
				}
				
				if(file.isFile())
				{
					fileList.add(file.getAbsolutePath());
					continue;
				}
				
				throw new IOException("Unknonwn type "+file);
			}
		}
		return !folderList.isEmpty();
	}


	@Override
	public void finish() {
		//NOP
	}

	@Override
	public <T> T getResult(Class<T> clazz) {
		return 	clazz.cast(fileList);

	}
	
	@Override
	public String toString() {
		return "FolderScanJob [folderlist=" + folderList.size() + ", filelist="
				+ fileList.size() + "]";	
	}



}
