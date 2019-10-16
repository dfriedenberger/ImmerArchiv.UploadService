package de.immerarchiv.job.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.BagItList;
import de.immerarchiv.repository.impl.MetaDataList;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.MetaDataKeys;

public class RepositoryScanJob implements Job {

	private final static Logger logger = LogManager.getLogger(RepositoryScanJob.class);

	private final BagItList bagitList = new BagItList();
	private long bagitCnt = -1;
	private final RepositoryService repositoryService;
	private final String repositoryId;

	public RepositoryScanJob(String repositoryId,RepositoryService repositoryService) {
		this.repositoryId = repositoryId;
		this.repositoryService = repositoryService;
    }

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}
	
	@Override
	public void init() {
		// NOP
	}

	@Override
	public boolean next() throws IOException, GeneralSecurityException, ParseException {
		
		if(bagitCnt == -1)
		{
			MetaDataList metadatalist = repositoryService.resolveStatus();
			bagitCnt = metadatalist.getLong(MetaDataKeys.mdRepositoryCntBagits);
			return true;
		}
		
		
		
		int skip = bagitList.size();
		Map<String, MetaDataList> metadata = repositoryService.resolveBagits(skip,100);
		
		List<BagIt> bagits = metadata.entrySet().stream().map(this::toBagIt).collect(Collectors.toList());
		
		bagitList.addAll(bagits);	
				
		return bagitList.size() < bagitCnt;
	}

	public BagIt toBagIt(Entry<String,MetaDataList> e)
	{
		BagIt bagIt = new BagIt();
		bagIt.repo = repositoryId;
		bagIt.id = e.getKey();
		bagIt.files = e.getValue().getLong(MetaDataKeys.mdBagitCntFiles);
		try {
			bagIt.lastModified = e.getValue().getDate(MetaDataKeys.mdDateLastModified).getTime();
		} catch (ParseException exc) {
			throw new RuntimeException(exc);
		}
		return bagIt;
	}
	@Override
	public void finish() {
		// NOP

	}

	@Override
	public <T> T getResult(Class<T> clazz) {
		return clazz.cast(bagitList);
	}



}
