package de.immerarchiv.job.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileStates {

	private List<FileState> states = new ArrayList<>();
	
	private boolean warning = false;
	private boolean synched = true;

	private Set<String> warnings = new HashSet<>();

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
			warnings .add(state.getState());
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

	public Set<String> getWarnings() {
		return warnings;
	}
	
	

}
