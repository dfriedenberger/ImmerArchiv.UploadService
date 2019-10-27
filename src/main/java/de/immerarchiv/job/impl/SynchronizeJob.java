package de.immerarchiv.job.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.app.Service;
import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.WrongCheckSumException;

public class SynchronizeJob implements Job {

	private final static Logger logger = LogManager.getLogger(Service.class);

	private final List<Folder> queue;
	private final Archiv archiv;
	private final FolderSystem folderSystem;

	private final List<Job> nextJobs = new ArrayList<Job>();
	
	public SynchronizeJob(Archiv archiv,FolderSystem folderSystem)
	{
		this.archiv = archiv;
		this.folderSystem = folderSystem;
		this.queue = new ArrayList<>();
	}
	
	@Override
	public void init() throws Exception {
		this.queue.addAll(folderSystem.getFolders());
	}

	@Override
	public boolean next() throws Exception {

		if(queue.isEmpty()) 
			throw new IOException("has no file to scann");
		
		Folder folder = queue.remove(0);
		List<FolderFile> files = folderSystem.selectFiles(folder);
		
		if(files.size() > 0)
		{
		
			List<BagIt> bagIts = archiv.findBagits(files);
			
			// check for each File, where file exists 
			for(FolderFile file : files)
			{
				for(BagIt bagIt : bagIts)
				{
					try
					{
						if(!archiv.fileExists(bagIt,file))
						{
							//must synchronize
							//upload file to bagit
							nextJobs.add(new UploadJob(bagIt,file));
						}
					}
					catch (WrongCheckSumException e)
					{
						e.printStackTrace();
						//file exists but changed
					}
				}
				
				
				
			}
			
		}
		else
		{
			logger.info("folder {} is empty");
		}
		
		
		
		//check if is in bagit
		
		return !queue.isEmpty();
	}

	@Override
	public void finish() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int priority() {
		return 300;
	}

	@Override
	public List<Job> getNext() {
		return nextJobs;
	}

	@Override
	public String toString() {
		return "SynchronizeJob [queue=" + queue.size() + ", nextJobs=" + nextJobs.size() + "]";
	}

}
