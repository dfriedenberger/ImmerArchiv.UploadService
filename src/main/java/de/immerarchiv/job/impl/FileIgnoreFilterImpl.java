package de.immerarchiv.job.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.immerarchiv.job.interfaces.FileIgnoreFilter;

public class FileIgnoreFilterImpl implements FileIgnoreFilter {

	private final List<String> patterns = new ArrayList<>();

	@Override
	public boolean ignore(File file) {

		if(patterns.contains(file.getName()))
			return true;
		
		//Default: do not ignore files
		return false;
	}

	@Override
	public void addPattern(String pattern) {
		patterns.add(pattern);
	}

}
