package de.immerarchiv.util.interfaces;

import java.io.IOException;
import java.util.List;

import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.repository.model.FileInfo;

public interface BagItCache {

	List<FileInfo> get(BagIt bagit);

	void put(BagIt bagit, List<FileInfo> fileInfoList) throws IOException;

	void load() throws IOException;
	
}
