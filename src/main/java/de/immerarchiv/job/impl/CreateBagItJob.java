package de.immerarchiv.job.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.Priority;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.BagItInfo;

public class CreateBagItJob implements Job {

	private final static Logger logger = LogManager.getLogger(CreateBagItJob.class);

	private final RepositoryService repositoryService;
	private final BagIt bagIt;

	public CreateBagItJob(RepositoryService repositoryService, BagIt bagIt) {
		this.repositoryService = repositoryService;
		this.bagIt = bagIt;
	}

	@Override
	public void init() throws Exception {
		//NOP
	}

	@Override
	public boolean next() throws Exception {
		
		
		BagItInfo info = new BagItInfo();
		info.setDescription(bagIt.getDescription());
		
		String bagitId = repositoryService.create(bagIt.getId(), info);
		
		logger.info("Create bagIt {}",bagitId);
		
		return false;
	}

	@Override
	public void finish() throws Exception {
		//NOP
	}

	@Override
	public Priority priority() {
		return Priority.CreateBagIt;
	}

	@Override
	public List<Job> getNext() {
		return null;
	}

}
