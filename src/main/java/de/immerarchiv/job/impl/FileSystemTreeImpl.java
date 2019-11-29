package de.immerarchiv.job.impl;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.immerarchiv.job.interfaces.FileSystemTree;
import de.immerarchiv.job.model.TreeEntry;

public class FileSystemTreeImpl implements FileSystemTree {

	private Map<String,Integer> files2id = new HashMap<>();

	private Map<Integer,List<Integer>> files = new HashMap<>();
	private Map<Integer,List<Integer>> folders = new HashMap<>();
	private Map<Integer,String> names = new HashMap<>();

	private int globalFileId = 0;
	
	public FileSystemTreeImpl()
	{
		folders.put(0, new ArrayList<>());
		files.put(0, new ArrayList<>());
	}
	
	
	@Override
	public synchronized Integer get(File file) {

		String key = file.getAbsolutePath();
		
		if(files2id.containsKey(key)) return files2id.get(key);

		File parentFile = file.getParentFile();
		Integer parentId = 0;
		if(parentFile != null) parentId = get(parentFile);
		
		
		++globalFileId;

		files2id.put(key, globalFileId);
		
		
		//Windows special File("D:\\").getName() is empty
		Pattern pattern = Pattern.compile("^([A-Z]):[\\\\]$");
        Matcher matcher = pattern.matcher(key);
		if(matcher.matches())
		{
			String disk = matcher.group(1);
			names.put(globalFileId,disk + ":");
		}
		else
		{
			names.put(globalFileId,file.getName());
		}
		if(file.isDirectory())
		{
			folders.put(globalFileId, new ArrayList<>());
			files.put(globalFileId, new ArrayList<>());
			folders.get(parentId).add(globalFileId);
		}
		else if(file.isFile())
		{
			files.get(parentId).add(globalFileId);
		}
		else
		{
			throw new RuntimeException("Unknown type "+file);
		}
		
		return globalFileId;
	}

	@Override
	public List<Integer> resolveIds(Integer id) {

		
		List<Integer> fileEntries = new ArrayList<>();
		
		fileEntries.addAll(files.get(id));
		
		for(Integer folderId : folders.get(id))
		{
			fileEntries.add(folderId);

			fileEntries.addAll(resolveIds(folderId));
		}
		
		return fileEntries;
	}

	@Override
	public List<TreeEntry> resolveChilds(Integer id) {

		List<Integer> filesList = files.get(id);
		List<Integer> folderList = folders.get(id);
		
		List<TreeEntry> entries = new ArrayList<>();
		
		for(Integer fileId : filesList)
		{
			TreeEntry e = new TreeEntry();
			
			e.setDirectory(false);
			e.setId(fileId);
			e.setName(names.get(fileId));
			entries.add(e);
		}
		
		for(Integer folderId : folderList)
		{
			TreeEntry e = new TreeEntry();
			
			List<Integer> expanded = expand(folderId);
			
			e.setDirectory(true);
			e.setId(expanded.get(expanded.size() -1));
			
			String name = expanded.stream().map(i -> names.get(i)).collect( Collectors.joining( "/" ) );
			e.setName(name);
			entries.add(e);
		}
		
		return entries;
	}


	private List<Integer> expand(Integer id) {

		List<Integer> expanded = new ArrayList<>();
		expanded.add(id);
		if(files.get(id).isEmpty()) 
		{
			List<Integer> f = folders.get(id);
			if(f.size() == 1) 
			{
				expanded.addAll(expand(f.get(0)));
			}
		}
		return expanded;
	}

	

}
