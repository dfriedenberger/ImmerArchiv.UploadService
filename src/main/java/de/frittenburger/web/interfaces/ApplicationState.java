package de.frittenburger.web.interfaces;


import java.util.List;
import java.util.Map;

import de.frittenburger.web.model.JobState;
import de.immerarchiv.job.impl.UploadJob;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FileSystemState;

public interface ApplicationState {

	JobState getJobState();

	void heartbeat();

	void updateNextScan(long nextCycle, String trigger);

	void addError(Exception e, Job currentJob);

	void setNextFileSystemState(FileSystemState fileSystemState);

	void startJob(Job currentJob);
	void stopJob();

	Map<String,Object> getFilesState();

	void addSuccessfulUpload(UploadJob uploadJob);

	List<String> getErrors(int skip, int limit);

	List<String> getUploads(int skip, int limit);

}
