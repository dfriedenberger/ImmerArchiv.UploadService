package de.immerarchiv.job.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.immerarchiv.job.interfaces.FileSystemTree;

public class FileSystemState {


	private Map<Integer,FileStates> files = new HashMap<>();

	private final FileSystemTree tree;
	
	public FileSystemState(FileSystemTree tree)
	{
		this.tree = tree;
	}
	//Wrapper
	public void setState(File file, String message) {
		
		FileState state = new FileState();
		state.setState(message);
		put(file, state);
		
	}

	public void put(File file, FileState state) {
		
		Integer id = tree.get(file);
		
		if(!files.containsKey(id))
			files.put(id,new FileStates());
		
		files.get(id).add(state);
	}

	public FileSystemTree getTree() {
		return tree;
	}

	public FileStates getStates(Integer id) {
		return files.get(id);
	}

	public int size() {
		return files.size();
	}


	

	

}
