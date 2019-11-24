package de.immerarchiv.job.model;

import java.util.ArrayList;
import java.util.List;

public class FileStates {

	private List<FileState> states = new ArrayList<>();
	
	private boolean warning = false;
	private boolean synched = true;

	public void add(FileState state) {
		
		switch(state.getState())
		{
		case "exists":
			break;
		case "upload job":
			synched = false;
			break;
		default:
			synched = false;
			warning = true;
			break;
		}
		
		states.add(state);
	}

	public boolean hasWarning() {
		return warning;
	}
	
	public boolean isSynchronized() {
		return synched;
	}
	
	

}
