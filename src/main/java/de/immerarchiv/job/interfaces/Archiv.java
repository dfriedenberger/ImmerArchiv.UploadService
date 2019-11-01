package de.immerarchiv.job.interfaces;

import java.util.List;

import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.DifferentBagItsException;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.WrongCheckSumException;
import de.immerarchiv.job.model.WrongFilenameException;

public interface Archiv {


	//create empty bagit's if not existing 
	
	void addBagIt(BagIt bagIt);

	void addFile(BagIt bagit, List<FolderFile> files);

	
	List<BagIt> selectBagItsForRepository(String id);

	List<BagIt> findBagits(Folder folder,List<FolderFile> files) throws DifferentBagItsException;

	boolean fileExists(BagIt bagIt, FolderFile file) throws WrongCheckSumException, WrongFilenameException;




}
