package de.immerarchiv.job.interfaces;

import java.io.File;

public interface FileIgnoreFilter {

	boolean ignore(File file);

	void addPattern(String pattern);

}
