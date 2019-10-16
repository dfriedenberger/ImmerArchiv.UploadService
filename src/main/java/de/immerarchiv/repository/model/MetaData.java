package de.immerarchiv.repository.model;

public class MetaData {
	public String key;
	public String value;
	
	@Override
	public String toString() {
		return key + " = " + value;
	}
	
	
}
