package de.immerarchiv.job.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.immerarchiv.job.model.FileState;
import de.immerarchiv.job.model.FileSystemState;

public class ApplicationState {

	private static Map<String,Object> map = new HashMap<String,Object>();
	private static FileSystemState fileSystemState = new FileSystemState();
	public static void incr(String key) {

		int v = (Integer)map.get(key);
		v++;
		map.put(key,v);
		
	}

	public static void set(String key,Object value) {

		map.put(key,value);
		
	}

	public static void set(String key,Date date) {

		map.put(key,date.getTime());
		
	}
	
	public static void setFileSystemState(FileSystemState state) {
		
		fileSystemState = state;
	}
	
	public static Map<String, Object> get() {
		
		Map<String, Object> all = new HashMap<>(map);
		all.putAll(getFiles());

		return all;
	}
	
	private static Map<String, Object> getFiles() {
		
		Map<String, Object> files = new HashMap<>();
		
		for(Entry<String, List<FileState>> e : fileSystemState.entrySet())
		{
			String state = null;
			for(FileState s : e.getValue())
			{
				switch(s.getState())
				{
				case "exists":
					if(state == null)
						state = "files-ok";
					else
						state = "files-warning";
				}
				
			}	
			if(state == null)
				state = "files-warning";
			
			long cnt = 0;
			if(files.containsKey(state))
				cnt = ((Long)files.get(state)).longValue();
			cnt++;
			files.put(state,cnt);
		}
		
		return files;
	}
	
	

	
}
