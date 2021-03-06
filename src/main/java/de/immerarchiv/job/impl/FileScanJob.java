package de.immerarchiv.job.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.Priority;
import de.immerarchiv.util.interfaces.MD5Cache;
import de.immerarchiv.util.interfaces.MD5Service;

public class FileScanJob implements Job {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(FileScanJob.class);

	private final MD5Service md5service;
	private final MD5Cache md5cache;
	private final List<FolderFile> fileQueue;

	public FileScanJob(MD5Service md5service,MD5Cache md5cache,List<FolderFile> files)
	{
		this.md5service = md5service;
		this.md5cache = md5cache;
		this.fileQueue = files;
	}
	
	
	@Override
	public Priority priority() {
		return Priority.FileScan;
	}
	
	@Override
	public void init() throws Exception {
		md5cache.load();
	}

	
	@Override
	public boolean next() throws Exception {
		
		if(fileQueue.isEmpty()) 
			throw new IOException("has no file to scann");
		
		FolderFile folderfile = fileQueue.remove(0);
		File file = folderfile.getFile();
		if(!file.isFile())
			throw new IOException(file + " has to be a file");
		
		
		String md5 = md5cache.get(file);
		
		if(md5 == null)
		{
			md5 = md5service.calc(file);
			md5cache.put(file,md5);
		}
		folderfile.setMd5(md5);
		
		
		return !fileQueue.isEmpty();
		
		
	}

	@Override
	public String toString() {
		return "FileScanJob [fileQueue=" + fileQueue.size() + "]";
	}


	@Override
	public List<Job> getNext() {
		// TODO Auto-generated method stub
		return null;
	}





}
