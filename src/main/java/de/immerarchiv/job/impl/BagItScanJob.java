package de.immerarchiv.job.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.BagItList;
import de.immerarchiv.job.model.FileInfoList;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.FileInfo;
import de.immerarchiv.util.interfaces.BagItCache;

public class BagItScanJob implements Job {

	
	
	private final RepositoryService repositoryService;
	private final BagItCache bagItCache;

	private final BagItList bagitQueue;
	private final FileInfoList fileInfoList = new FileInfoList();



	public BagItScanJob(RepositoryService repositoryService,
			BagItCache bagItCache, BagItList bagItList) {
		this.repositoryService = repositoryService;
		this.bagItCache = bagItCache;
		this.bagitQueue = bagItList;
	}

	@Override
	public void init() throws IOException {
		bagItCache.load();
	}

	@Override
	public boolean next() throws IOException, GeneralSecurityException {
		if(bagitQueue.isEmpty()) 
			throw new IOException("has no bagit to scann");
		
		BagIt bagit = bagitQueue.remove(0);
		
		List<FileInfo> files = bagItCache.get(bagit);
		if(files == null)
		{
			files = repositoryService.resolveBagit(bagit.id);
			bagItCache.put(bagit,files);
		}
		fileInfoList.addAll(files);
		
		return !bagitQueue.isEmpty();
	}

	@Override
	public void finish() throws IOException {
		bagItCache.save();
	}

	@Override
	public <T> T getResult(Class<T> clazz) {
		return clazz.cast(fileInfoList);
	}

	@Override
	public String toString() {
		return "BagItScanJob [bagitQueue=" + bagitQueue.size() + ", fileInfoList="
				+ fileInfoList.size() + "]";
	}

	
}
