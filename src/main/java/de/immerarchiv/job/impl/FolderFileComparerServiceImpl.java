package de.immerarchiv.job.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.immerarchiv.job.interfaces.FolderFileComparerService;
import de.immerarchiv.job.model.FolderFile;

public class FolderFileComparerServiceImpl implements FolderFileComparerService {

	@Override
	public double isSameFolder(List<FolderFile> localFiles, List<FolderFile> bagItFiles) {

		if(localFiles.isEmpty())
			throw new IllegalArgumentException("localFiles is empty");
		
		 Collection<FolderFile> similar = new HashSet<FolderFile>( localFiles );
         Collection<FolderFile> different = new HashSet<FolderFile>();
         different.addAll( localFiles );
         different.addAll( bagItFiles );

         similar.retainAll( bagItFiles );
         different.removeAll( similar );
		

         //System.out.println(similar);
         //System.out.println(different);
         
         if(similar.size() == 0) return 0.0;
         
         return 1.0 * similar.size() / (similar.size() + different.size());
                
		//return similar.size() > 0; //&& different.size() == 0;
	}

}
