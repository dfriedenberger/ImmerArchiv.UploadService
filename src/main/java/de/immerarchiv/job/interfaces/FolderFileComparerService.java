package de.immerarchiv.job.interfaces;

import java.util.List;

import de.immerarchiv.job.model.FolderFile;

public interface FolderFileComparerService {

	double isSameFolder(List<FolderFile> list, List<FolderFile> files);

}
