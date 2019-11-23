package de.frittenburger.web.model;

public class JobState {
	
	private long heartbeat = 0;
	private int errors = 0;
	private int uploads = 0;

	private String nextScanTrigger = "null";
	private long nextScanTime = 0;

	private int jobCount = 0;
	private int currentStep = 0;
	private long currentStart = 0;
	private String currentName = "";
	
	
	public String getNextScanTrigger() {
		return nextScanTrigger;
	}

	public void setNextScanTrigger(String nextScanTrigger) {
		this.nextScanTrigger = nextScanTrigger;
	}

	public long getNextScanTime() {
		return nextScanTime;
	}

	public void setNextScanTime(long nextScanTime) {
		this.nextScanTime = nextScanTime;
	}

	public long getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(long heartbeat) {
		this.heartbeat = heartbeat;
	}
	
	public int getErrors() {
		return errors;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public int getUploads() {
		return uploads;
	}

	public void setUploads(int uploads) {
		this.uploads = uploads;
	}

	public int getJobCount() {
		return jobCount;
	}

	public void setJobCount(int jobCount) {
		this.jobCount = jobCount;
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}
	
	public void incrCurrentStep() {
		setCurrentStep(getCurrentStep() + 1);
	}

	public long getCurrentStart() {
		return currentStart;
	}

	public void setCurrentStart(long currentStart) {
		this.currentStart = currentStart;
	}

	public String getCurrentName() {
		return currentName;
	}

	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}

	
	
	
}
