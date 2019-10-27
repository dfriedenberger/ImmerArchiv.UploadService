package de.immerarchiv.job.impl;

import java.util.List;

import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.FolderFile;

public class UploadJob implements Job {

	private final BagIt bagIt;
	private final FolderFile file;

	public UploadJob(BagIt bagIt, FolderFile file) {

		this.bagIt = bagIt;
		this.file = file;
	
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean next() throws Exception {
		System.out.println("upload file "+file+" to "+bagIt);
		return false;
	}

	@Override
	public void finish() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int priority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Job> getNext() {
		// TODO Auto-generated method stub
		return null;
	}

}
