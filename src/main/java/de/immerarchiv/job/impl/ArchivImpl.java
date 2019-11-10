package de.immerarchiv.job.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.FolderFileComparerService;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.DifferentBagItsException;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.WrongCheckSumException;
import de.immerarchiv.job.model.WrongFilenameException;
import de.immerarchiv.util.interfaces.NameService;

public class ArchivImpl implements Archiv {

	private final static Logger logger = LogManager.getLogger(ArchivImpl.class);

	private final String[] repositories;
	private final Map<BagIt,List<FolderFile>> bagIts = new HashMap<>();
	private final FolderFileComparerService comparerService;
	private final NameService nameService;

	private final Function<Map<BagIt,Double>,List<BagIt>> bestBagItsStrategy;

	public ArchivImpl(String[] repositories,FolderFileComparerService comparerService, NameService nameService,Function<Map<BagIt,Double>,List<BagIt>> bestBagItsStrategy) {
		this.repositories = repositories;
		this.comparerService = comparerService;
		this.nameService = nameService;
		this.bestBagItsStrategy = bestBagItsStrategy;
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
	public List<BagIt> findBagits(Folder folder,List<FolderFile> files) throws DifferentBagItsException {

		Map<BagIt,Double> candidatesMatching = new HashMap<>();

		for(BagIt bagit : bagIts.keySet())
		{
			List<FolderFile> bagItFiles = bagIts.get(bagit);
			double matching = comparerService.isSameFolder(files,bagItFiles);
			if(matching > 0.0)
			{
				candidatesMatching.put(bagit,matching);
				logger.trace("candidate {} {}",bagit,matching);
			}
		}
		
		
		//Find best candidates
		List<BagIt> candidates = bestBagItsStrategy.apply(candidatesMatching);
		
		logger.trace("best {}",candidates);;

		//Check
		List<String> bagitIds = candidates.stream().map(b -> b.getId()).distinct().collect(Collectors.toList());
	
		
		switch(bagitIds.size())
		{
		case 0:
			//no bagits, create new
			String id = UUID.randomUUID().toString();
			String description = nameService.createDescription(folder);
			for(String repo : repositories)
			{
				candidates.add(getBagit(repo,id,description));
			}
			return candidates;
		case 1:
			//todo bien
			String id1 = bagitIds.get(0);
			String description1 = candidates.get(0).getDescription();
			for(String repo : repositories)
			{
				if(candidates.stream().anyMatch(b -> b.getRepo().equals(repo))) continue;
				candidates.add(getBagit(repo,id1,description1));			
			}			
			return candidates;
		default:
			//different bagits found
			for(Entry<BagIt, Double> e : candidatesMatching.entrySet())
			{
				logger.info("repo:{} bagit:{} files:{} matching:{}", e.getKey().getRepo(),
						e.getKey().getId(),e.getKey().getFiles(),String.format("%.2f", e.getValue()));
			}
			throw new DifferentBagItsException("different bagits not supported "+bagitIds);
		}
		
		
		
		// not reachable code		
	}

	private BagIt getBagit(String repo, String id, String description) {
		
		List<BagIt> bagitList = bagIts.keySet().stream().filter(b -> b.getId().equals(id)).filter(b -> b.getRepo().equals(repo)).collect(Collectors.toList());
		
		switch(bagitList.size())
		{
		case 0:
			//add
			BagIt bagIt = new BagIt();
			bagIt.setRepo(repo);
			bagIt.setId(id);
			bagIt.setDescription(description);
			bagIt.setFiles(0);
			bagIt.setLastModified(0);
			addBagIt(bagIt); //add
			return bagIt;
		case 1:
			return bagitList.get(0);
		default:
			//is a Set should never happens
			throw new RuntimeException("Duplicate Bagit Entries");
		}
		
	}

	@Override
	public boolean fileExists(BagIt bagIt, FolderFile file)
			throws WrongCheckSumException, WrongFilenameException {
		
		if(!bagIts.containsKey(bagIt))
			throw new IllegalArgumentException(bagIt+" not exists");
		
		Map<String,String> md5sums = new HashMap<>();
		for(FolderFile f : bagIts.get(bagIt))
		{
			md5sums.put(f.getMd5(),f.getSafeName());
			if(!f.getSafeName().equals(file.getSafeName())) continue;
			if(f.getMd5().equals(file.getMd5())) return true;
			throw new WrongCheckSumException(file.getSafeName());
		}
		
		if(md5sums.containsKey(file.getMd5()))
			throw new WrongFilenameException(md5sums.get(file.getMd5()));
		
		return false;
	}


	@Override
	public List<BagIt> selectBagItsForRepository(String id) {
		
		return bagIts.keySet().stream().filter(b -> b.getRepo().equals(id)).collect(Collectors.toList());
		
	}

	
	
	

}
