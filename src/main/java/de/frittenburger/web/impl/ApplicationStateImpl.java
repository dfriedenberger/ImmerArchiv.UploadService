package de.frittenburger.web.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.frittenburger.web.interfaces.ApplicationState;
import de.frittenburger.web.model.JobState;
import de.immerarchiv.job.impl.UploadJob;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FileState;
import de.immerarchiv.job.model.FileSystemState;

public class ApplicationStateImpl implements ApplicationState {

	private final static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private JobState jobState = new JobState();
	private List<String> errors = new ArrayList<>();
	private List<String> uploads = new ArrayList<>();

	private FileSystemState fileSystemState = new FileSystemState();
	
	
	
	@Override
	public JobState getJobState() {
		return jobState;
	}
	
	@Override
	public void heartbeat() {
		jobState.setHeartbeat(new Date().getTime());
	}
	

	@Override
	public void updateNextScan(long next, String trigger) {
		jobState.setNextScanTime(next);
		jobState.setNextScanTrigger(trigger);
	}

	@Override
	public void startJob(Job currentJob) {
		jobState.setCurrentStart(new Date().getTime());
		jobState.setCurrentStep(0);
		jobState.setCurrentName(currentJob.getClass().getSimpleName());		
	}

	@Override
	public void stopJob() {
		
		jobState.setCurrentStart(0);
		jobState.setCurrentStep(0);
		jobState.setCurrentName("");	
		
	}
	@Override
	public void addError(Exception e, Job currentJob) {
		
		String errorMessage = sdf.format(new Date()) + " " + currentJob.getClass().getSimpleName() + " "+ e.getMessage();
		errors.add(errorMessage);
		jobState.setErrors(errors.size());

	}

	@Override
	public void addSuccessfulUpload(UploadJob uploadJob)
	{
		String path = uploadJob.getFolderFile().getFile().getPath();
		
		if(path.length() > 60)
		{
			path = "..."+path.substring(path.length() - 57);
		}
		
		String uploadMessage = sdf.format(new Date()) + " " +path
				+" " +uploadJob.getRepositoryService().getUrl()
				+" \""+uploadJob.getBagIt().getDescription() + "\"";
		uploads.add(uploadMessage);
		jobState.setUploads(uploads.size());

	}

	@Override
	public List<String> getErrors(int skip, int limit) {
		return errors.stream().skip(skip).limit(limit).collect(Collectors.toList());
	}

	@Override
	public List<String> getUploads(int skip, int limit) {
		return uploads.stream().skip(skip).limit(limit).collect(Collectors.toList());
	}
	
	@Override
	public void setNextFileSystemState(FileSystemState fileSystemState) {
		this.fileSystemState = fileSystemState;	
	}

	@Override
	public Map<String, Object> getFilesState() {
		
		Map<String, Object> files = new HashMap<>();
		files.put("files-warning", 0L);
		files.put("files-ok", 0L);

		
		for(Entry<String, List<FileState>> e : fileSystemState.entrySet())
		{
			String state = null;
			for(FileState s : e.getValue())
			{
				switch(s.getState())
				{
				case "exists":
					if(state == null)
						state = "files-ok";
					else
						state = "files-warning";
				}
				
			}	
			if(state == null)
				state = "files-warning";
			
			long cnt = ((Long)files.get(state)).longValue();
			cnt++;
			files.put(state,cnt);
		}
		
		return files;
	}
	
	private static ApplicationState applicationState = new ApplicationStateImpl();
	public static ApplicationState getInstance()
	{
		return applicationState;
	}

	

	
	
	




	

	

	
}
