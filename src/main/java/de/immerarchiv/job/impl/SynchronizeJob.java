package de.immerarchiv.job.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.DifferentBagItsException;
import de.immerarchiv.job.model.FileState;
import de.immerarchiv.job.model.FileSystemState;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.Priority;
import de.immerarchiv.job.model.WrongCheckSumException;
import de.immerarchiv.job.model.WrongFilenameException;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.util.impl.NameServiceImpl;

public class SynchronizeJob implements Job  {

	private final static Logger logger = LogManager.getLogger(SynchronizeJob.class);

	private final List<Folder> queue;
	private final Archiv archiv;
	private final FileSystemState fileSystemState;

	private final FolderSystem folderSystem;
	private final List<RepositoryService> repositoryServices;

	private final List<Job> nextJobs = new ArrayList<Job>();

	private Set<BagIt> existingBagIts = new HashSet<>();

	private Map<BagIt,Set<FolderFile>> uploadBagItMap = new HashMap<>();
	
	public SynchronizeJob(List<RepositoryService> repositoryServices,Archiv archiv,FolderSystem folderSystem,FileSystemState fileSystemState)
	{
		this.repositoryServices = repositoryServices;
		this.archiv = archiv;
		this.folderSystem = folderSystem;
		this.queue = new ArrayList<>();
		this.fileSystemState = fileSystemState;
	}
	
	@Override
	public void init() throws Exception {
		
		for(RepositoryService service : repositoryServices)
		{
			String id = service.getId();
			List<BagIt> bagits = archiv.selectBagItsForRepository(id);
			this.existingBagIts.addAll(bagits);
		}
		
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
			try{
				List<BagIt> bagIts = archiv.findBagits(folder,files);
			
			

				// check for each File, where file exists 
				for(FolderFile file : files)
				{
					List<FileState> states = new ArrayList<>();
					fileSystemState.put(file.getFile().getAbsolutePath(),states);
					
					
					for(BagIt bagIt : bagIts)
					{
						FileState state = new FileState();
						states.add(state);
						state.setBagIt(bagIt);
						try
						{
							if(!archiv.fileExists(bagIt,file))
							{
								//must synchronize
								//upload file to bagit
								
								List<RepositoryService> repos = repositoryServices.stream().filter(rs -> rs.getId().equals(bagIt.getRepo())).collect(Collectors.toList());
	
								if(repos.size() != 1)
								{
									throw new RuntimeException("Could not select single service for "+bagIt);
								}
						
								
								if(!uploadBagItMap.containsKey(bagIt)) uploadBagItMap.put(bagIt, new HashSet<>());
									
								if(uploadBagItMap.get(bagIt).contains(file))
								{
									state.setState("duplicate in bagit");
									logger.warn("UploadJob exists file {} to bagIt {}",file,bagIt);
									continue;
								}
								uploadBagItMap.get(bagIt).add(file);
								nextJobs.add(new UploadJob(repos.get(0),new NameServiceImpl(),bagIt,file,existingBagIts));
								state.setState("upload job");
							}
							else
							{
								state.setState("exists");
							}
						}
						catch (WrongCheckSumException e)
						{
							state.setState("other checksum");
							logger.warn("WrongCheckSumException {}, try rename file or create next version",file.getFile());
						}
						catch (WrongFilenameException e)
						{
							state.setState("other filename");
							logger.warn("WrongFilenameException {} , file exists with {}",file.getFile(),e.getMessage());
						}
					}
					
					
					
					
				}
			}
			catch(DifferentBagItsException e)
			{
				logger.error("DifferentBagItsException {}",e.getMessage());
			}
		}
			
		//check if is in bagit
		
		return !queue.isEmpty();
	}

	

	@Override
	public Priority priority() {
		return Priority.Syncronize;
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
