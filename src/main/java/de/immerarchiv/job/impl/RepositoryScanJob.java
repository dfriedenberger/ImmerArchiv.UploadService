package de.immerarchiv.job.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.Priority;
import de.immerarchiv.repository.impl.MetaDataList;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.MetaDataKeys;
import de.immerarchiv.util.interfaces.BagItCache;

public class RepositoryScanJob implements Job {


	private long bagitCnt = -1;
	private int bagitResolvedCnt = 0;
	private final RepositoryService repositoryService;
	private final BagItCache bagItCache;
	private final Archiv archiv;

	public RepositoryScanJob(Archiv archiv, BagItCache bagItCache,RepositoryService repositoryService) {
		this.archiv = archiv;
		this.bagItCache = bagItCache;
		this.repositoryService = repositoryService;
    }

	@Override
	public Priority priority() {
		return Priority.RepositoryScan;
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
		
		
		
		int skip = bagitResolvedCnt;
		Map<String, MetaDataList> metadata = repositoryService.resolveBagits(skip,100);
		
		List<BagIt> bagits = metadata.entrySet().stream().map(this::toBagIt).collect(Collectors.toList());
		
		bagitResolvedCnt += bagits.size();
		
		for(BagIt bagIt : bagits)
			archiv.addBagIt(bagIt);
				
		return bagitResolvedCnt < bagitCnt;
	}

	@Override
	public String toString() {
		return "RepositoryScanJob [bagitResolvedCnt=" + bagitResolvedCnt + ", bagitCnt="
				+ bagitCnt + ", repositoryId=" + repositoryService.getId() + "]";
	}

	private BagIt toBagIt(Entry<String,MetaDataList> e)
	{
		BagIt bagIt = new BagIt();
		bagIt.setRepo(repositoryService.getId());
		bagIt.setId(e.getKey());
		bagIt.setDescription(e.getValue().get(MetaDataKeys.mdDescription));
		bagIt.setFiles(e.getValue().getLong(MetaDataKeys.mdBagitCntFiles));
		try {
			bagIt.setLastModified(e.getValue().getDate(MetaDataKeys.mdDateLastModified).getTime());
		} catch (ParseException exc) {
			throw new RuntimeException(exc);
		}
		return bagIt;
	}

	@Override
	public List<Job> getNext() {
    	
		List<Job> jobs = new ArrayList<>();		
		List<BagIt> bagits = archiv.selectBagItsForRepository(repositoryService.getId());
		
    	for(int i = 0;i < bagits.size();i+= 20)
    	{
    		List<BagIt>  bagItListPart = new ArrayList<>();
    		for(int ix = i;ix < i + 20 && ix < bagits.size();ix++)
    			bagItListPart.add(bagits.get(ix));
			jobs.add(new BagItScanJob(repositoryService,bagItCache,archiv,bagItListPart));
    	}
    	return jobs;
	}



}
