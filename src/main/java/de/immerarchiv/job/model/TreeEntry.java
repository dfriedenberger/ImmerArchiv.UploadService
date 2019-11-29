package de.immerarchiv.job.model;

import java.util.HashMap;
import java.util.Map;

public class TreeEntry {
	
	private String name;
	private int id;
	private boolean directory;
	private Map<String,Object> properties = new HashMap<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isDirectory() {
		return directory;
	}
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	public void addAdditionalField(String key, Object value) {
		this.properties.put(key, value);
	}
	
	
}
