package de.immerarchiv.util.interfaces;


public interface FileChangesWatcher {

	boolean hasNewFiles();

	void addFolder(String path);

	void deleteFolders();

}
