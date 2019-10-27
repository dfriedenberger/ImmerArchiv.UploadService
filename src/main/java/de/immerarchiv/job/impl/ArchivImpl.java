package de.immerarchiv.job.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.FolderFileComparerService;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.WrongCheckSumException;

public class ArchivImpl implements Archiv {

	private final String[] repositories;
	private Map<BagIt,List<FolderFile> > bagIts = new HashMap<>();
	private FolderFileComparerService comparerService;

	public ArchivImpl(String[] repositories,FolderFileComparerService comparerService) {
		this.repositories = repositories;
		this.comparerService = comparerService;
	}

	@Override
	public void addBagIt(BagIt bagIt) {
		
		if(bagIts.containsKey(bagIt))
			throw new IllegalArgumentException(bagIt+" allways exists");
		
		bagIts.put(bagIt, new ArrayList<>());
		
	}

	
	@Override
	public void addFile(BagIt bagIt, List<FolderFile> files) {
		
		if(!bagIts.containsKey(bagIt))
			throw new IllegalArgumentException(bagIt+" not exists");
		
		bagIts.get(bagIt).addAll(files);
	}
	
	
	@Override
	public List<BagIt> findBagits(List<FolderFile> files) {

		List<BagIt> candidates = new ArrayList<BagIt>();
		
		for(BagIt bagit : bagIts.keySet())
		{
			List<FolderFile> bagItFiles = bagIts.get(bagit);
			if(comparerService.isSameFolder(files,bagItFiles))
			{
				candidates.add(bagit);
			}
		}
		
		//Check
		List<String> bagitIds = candidates.stream().map(b -> b.getId()).distinct().collect(Collectors.toList());
		
		
		switch(bagitIds.size())
		{
		case 0:
			//no bagits, create new
			String uuid = UUID.randomUUID().toString();
			for(String repo : repositories)
			{
				candidates.add(getBagit(repo,uuid));
			}
			//break;
		case 1:
			//todo bien
			for(String repo : repositories)
			{
				if(candidates.stream().anyMatch(b -> b.getRepo().equals(repo))) continue;
				candidates.add(getBagit(repo,bagitIds.get(0)));			
			}			
			return candidates;
			default:
			//different bagits found
			throw new RuntimeException("different bagits not supportewd "+bagitIds);
		}
		
		
		
		// not reachable code		
	}

	private BagIt getBagit(String repo, String id) {
		
		List<BagIt> bagitList = bagIts.keySet().stream().filter(b -> b.getId().equals(id)).filter(b -> b.getRepo().equals(repo)).collect(Collectors.toList());
		
		switch(bagitList.size())
		{
		case 0:
			//add
			BagIt bagIt = new BagIt();
			bagIt.setRepo(repo);
			bagIt.setId(id);
			bagIt.setFiles(0);
			bagIt.setLastModified(0);
			addBagIt(bagIt); //add
			return bagIt;
		case 1:
			return bagitList.get(0);
		default:
			//is a Set schould never happens
			throw new RuntimeException("Duplicate Bagit Entries");
		}
		
	}

	@Override
	public boolean fileExists(BagIt bagIt, FolderFile file)
			throws WrongCheckSumException {
		
		if(!bagIts.containsKey(bagIt))
			throw new IllegalArgumentException(bagIt+" not exists");
		
		
		for(FolderFile f : bagIts.get(bagIt))
		{
			if(!f.getName().equals(file.getName())) continue;
			if(f.getMd5().equals(file.getMd5())) return true;
			throw new WrongCheckSumException(file.getName());
		}
		return false;
	}


	@Override
	public List<BagIt> selectBagItsForRepository(String id) {
		
		return bagIts.keySet().stream().filter(b -> b.getRepo().equals(id)).collect(Collectors.toList());
		
	}

	
	

}
