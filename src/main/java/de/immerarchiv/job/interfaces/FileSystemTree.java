package de.immerarchiv.job.interfaces;

import java.io.File;
import java.util.List;

import de.immerarchiv.job.model.TreeEntry;


public interface FileSystemTree {

	Integer get(File file);

	List<Integer> resolveIds(Integer id);

	List<TreeEntry> resolveChilds(Integer id);

}
