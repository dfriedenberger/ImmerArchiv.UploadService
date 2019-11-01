package de.immerarchiv.job.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.Priority;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.FileInfo;
import de.immerarchiv.util.interfaces.BagItCache;

public class BagItScanJob implements Job {

	
	
	private final RepositoryService repositoryService;
	private final BagItCache bagItCache;

	private final List<BagIt> bagitQueue;
	private Archiv archiv;



	public BagItScanJob(RepositoryService repositoryService,
			BagItCache bagItCache, Archiv archiv, List<BagIt> bagItList) {
		this.repositoryService = repositoryService;
		this.archiv = archiv;
		this.bagItCache = bagItCache;
		this.bagitQueue = bagItList;
	}
	
	@Override
	public Priority priority() {
		return Priority.BagItScan;
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
			files = repositoryService.resolveBagit(bagit.getId());
			bagItCache.put(bagit,files);
		}
		
		List<FolderFile> folderfiles = files.stream().map(this::fileInfo2FolderFile).collect(Collectors.toList());
		archiv.addFile(bagit, folderfiles);
		
		return !bagitQueue.isEmpty();
	}

	private FolderFile fileInfo2FolderFile(FileInfo fileinfo)
	{
		FolderFile folderFile = new FolderFile();
		
		if(!fileinfo.CheckSumKey.toLowerCase().equals("md5"))
			throw new RuntimeException("unknown checksum type "+fileinfo.CheckSumKey);
		
		folderFile.setSafeName(fileinfo.name);
		folderFile.setMd5(fileinfo.CheckSumValue);

		return folderFile;
	}
	
	@Override
	public void finish() throws IOException {
		//NOP
	}

	@Override
	public String toString() {
		return "BagItScanJob [bagitQueue=" + bagitQueue.size() + "]";
	}

	@Override
	public List<Job> getNext() {
		return null; //no other jobs
	}

	
}
