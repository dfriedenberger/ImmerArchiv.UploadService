package de.immerarchiv.job.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileSystemState extends HashMap<String,List<FileState>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	//Wrapper
	public void setState(File file, String message) {
		List<FileState> states = new ArrayList<>();
		FileState state = new FileState();
		state.setState(message);
		states.add(state);		
		super.put(file.getAbsolutePath(), states);
	}

}
