package de.immerarchiv.job.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;

public class FolderSystemImpl implements FolderSystem {

	private Map<Folder,List<FolderFile>> map = new HashMap<>();

	@Override
	public void addFile(Folder folder, FolderFile folderFile) {
		if(!map.containsKey(folder))
			throw new IllegalArgumentException(folder+" not found");
		
		map.get(folder).add(folderFile);
	}

	@Override
	public List<Folder> getFolders() {
		return map.keySet().stream().collect(Collectors.toList());
	}

	@Override
	public List<FolderFile> selectFiles(Folder folder) {
		if(!map.containsKey(folder))
			throw new IllegalArgumentException(folder+" not found");
		
		return map.get(folder);
	}

	@Override
	public void addFolder(Folder folder) {
		if(map.containsKey(folder))
			throw new IllegalArgumentException(folder+" allready exists");
		map.put(folder, new ArrayList<>());
	}

}
