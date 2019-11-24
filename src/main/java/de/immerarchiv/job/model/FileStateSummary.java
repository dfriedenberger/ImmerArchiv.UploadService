package de.immerarchiv.job.model;


public class FileStateSummary {
	
	private int filesWarning = 0;
	private int filesOk = 0;
		
	public int getFilesWarning() {
		return filesWarning;
	}
	
	public void setFilesWarning(int filesWarning) {
		this.filesWarning = filesWarning;
	}
	
	public int getFilesOk() {
		return filesOk;
	}
	
	public void setFilesOk(int filesOk) {
		this.filesOk = filesOk;
	}

	public void incrFilesWarning() {
		filesWarning++;
	}

	public void incrFilesOk() {
		filesOk++;		
	}

}
