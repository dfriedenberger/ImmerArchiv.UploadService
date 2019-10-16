package de.immerarchiv.util.impl;

import java.io.File;

public class FileKeyImpl extends KeyImpl {

	public FileKeyImpl(File file) {		
		super(new String[]{file.getAbsolutePath()},new String[]{""+file.length(),""+file.lastModified()});
	}
	
	public FileKeyImpl(KeyImpl key) {
		super(key);
	}

	public static FileKeyImpl parse(String key) {
		return new FileKeyImpl(KeyImpl.parse(key));
	}

}
