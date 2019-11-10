package de.immerarchiv.job.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

		map.put(key,date.toString());
		
	}

	public static Map<String, Object> get() {
		return map;
	}

	public static void setFileSystemState(FileSystemState state) {
		fileSystemState = state;
	}

	public static FileSystemState getFileSystemState() {
		return fileSystemState;
	}
}
