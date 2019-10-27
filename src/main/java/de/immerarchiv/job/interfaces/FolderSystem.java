package de.immerarchiv.job.interfaces;

import java.util.List;

import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;

public interface FolderSystem {

	void addFile(Folder folder, FolderFile folderFile);

	List<Folder> getFolders();

	List<FolderFile> selectFiles(Folder folder);

	void addFolder(Folder folder);

	

}
