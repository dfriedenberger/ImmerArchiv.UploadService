package de.immerarchiv.job;

import static org.junit.Assert.*;

import org.junit.Test;

import de.immerarchiv.job.impl.FolderSystemImpl;
import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;

public class FolderSystemImplTest {

	@Test
	public void test() {
		FolderSystem folderSystem = new FolderSystemImpl();
		
		Folder folder1 = new Folder();
		folder1.setPath("path1");
		
		
		folderSystem.addFolder(folder1);
		
		FolderFile file1 = new FolderFile();
		folderSystem.addFile(folder1, file1);
		FolderFile file2 = new FolderFile();
		folderSystem.addFile(folder1, file2);

		
		assertEquals(1,folderSystem.getFolders().size());
		assertSame(folder1, folderSystem.getFolders().get(0));
		

		assertEquals(2,folderSystem.selectFiles(folder1).size());
		assertSame(file1, folderSystem.selectFiles(folder1).get(0));
		assertSame(file2, folderSystem.selectFiles(folder1).get(1));

		
		
	}

}
