package de.immerarchiv.job.interfaces;

import java.util.List;

import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.WrongCheckSumException;

public interface Archiv {


	//create empty bagit's if not existing 
	
	void addBagIt(BagIt bagIt);

	void addFile(BagIt bagit, List<FolderFile> files);

	
	List<BagIt> selectBagItsForRepository(String id);

	List<BagIt> findBagits(List<FolderFile> files);

	boolean fileExists(BagIt bagIt, FolderFile file) throws WrongCheckSumException;




}
