package de.immerarchiv.job.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FileStateMap;
import de.immerarchiv.util.interfaces.MD5Cache;
import de.immerarchiv.util.interfaces.MD5Service;

public class FileScanJob implements Job {


	private final MD5Service md5service;
	private final MD5Cache md5cache;
	private final List<String> fileQueue;
	private final FileStateMap fileStates = new FileStateMap();

	public FileScanJob(MD5Service md5service,MD5Cache md5cache,List<String> fileList)
	{
		this.md5service = md5service;
		this.md5cache = md5cache;
		this.fileQueue = fileList;
	}
	
	@Override
	public void init() throws Exception {
		md5cache.load();
	}

	
	@Override
	public boolean next() throws Exception {
		
		if(fileQueue.isEmpty()) 
			throw new IOException("has no file to scann");
		
		String filepath = fileQueue.remove(0);
		File file = new File(filepath);
		if(!file.isFile())
			throw new IOException(file + " has to be a file");
	
		String md5 = md5cache.get(file);
		
		if(md5 == null)
		{
			md5 = md5service.calc(file);
			md5cache.put(file,md5);
		}
		
		if(!fileStates.containsKey(md5)) 
			fileStates.put(md5, new ArrayList<String>());
		
		fileStates.get(md5).add(filepath);
				
		return !fileQueue.isEmpty();
		
		
	}

	@Override
	public String toString() {
		return "FileScanJob [fileQueue=" + fileQueue.size() + ", fileStates="
				+ fileStates.size() + "]";
	}


	@Override
	public void finish() throws Exception {
		md5cache.save();
		
	}

	@Override
	public <T> T getResult(Class<T> clazz) {
		return clazz.cast(fileStates);
	}





}
