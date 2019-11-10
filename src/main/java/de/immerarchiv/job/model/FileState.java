package de.immerarchiv.job.model;

public class FileState {
	
	private BagIt bagIt;
	private String state = "unknown";
	
	public BagIt getBagIt() {
		return bagIt;
	}

	public void setBagIt(BagIt bagIt) {
		this.bagIt = bagIt;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "FileState [bagIt=" + bagIt + ", state=" + state + "]";
	}

	
	
}
