package de.immerarchiv.util.interfaces;

import java.io.File;

import de.immerarchiv.job.model.Folder;

public interface NameService {

	/* generate temporary name for upload with same extension */
	String generateTempName(String filename);

	String createDescription(Folder folder);

	String createSafeName(File file);

}
