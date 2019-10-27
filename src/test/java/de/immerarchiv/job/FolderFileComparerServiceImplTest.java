package de.immerarchiv.job;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.immerarchiv.job.impl.FolderFileComparerServiceImpl;
import de.immerarchiv.job.interfaces.FolderFileComparerService;
import de.immerarchiv.job.model.FolderFile;


public class FolderFileComparerServiceImplTest {

	
	@Test(expected = IllegalArgumentException.class)
	public void testLocalListEmpty() {
		
		FolderFileComparerService service = new FolderFileComparerServiceImpl();
		List<FolderFile> list1 = new ArrayList<>();
		List<FolderFile> list2 = new ArrayList<>();
		service.isSameFolder(list1,list2);
		
	}

	@Test
	public void testSameFolder() {
		
		FolderFileComparerService service = new FolderFileComparerServiceImpl();
		
		FolderFile file1 = new FolderFile();
		file1.setName("test.txt");
		file1.setMd5("xxx");
		
		FolderFile file2 = new FolderFile();
		file2.setName("test1.txt");
		file2.setMd5("xxx1");
		
		FolderFile file3 = new FolderFile();
		file3.setName("test2.txt");
		file3.setMd5("xxx2");
		
		List<FolderFile> list1 = new ArrayList<>();
		list1.add(file1);
		list1.add(file2);
		list1.add(file3);
		
		
		List<FolderFile> list2 = new ArrayList<>();
		list2.add(file1);
		list2.add(file2);
		list2.add(file3);
		
		assertTrue(service.isSameFolder(list1,list2));
		
	}
	
	
	@Test
	public void testDifferentFolder() {
		
		FolderFileComparerService service = new FolderFileComparerServiceImpl();
		
		FolderFile file1 = new FolderFile();
		file1.setName("test.txt");
		file1.setMd5("xxx");
		
		FolderFile file2 = new FolderFile();
		file2.setName("test1.txt");
		file2.setMd5("xxx1");
	
		
		List<FolderFile> list1 = new ArrayList<>();
		list1.add(file1);
		
		
		
		List<FolderFile> list2 = new ArrayList<>();
		list2.add(file2);
		
		assertFalse(service.isSameFolder(list1,list2));
		
	}
	
	@Test
	public void testAdditionalLocalFiles() {
		
		FolderFileComparerService service = new FolderFileComparerServiceImpl();
		
		FolderFile file1 = new FolderFile();
		file1.setName("test.txt");
		file1.setMd5("xxx");
		
		FolderFile file2 = new FolderFile();
		file2.setName("test1.txt");
		file2.setMd5("xxx1");
		
		FolderFile file3 = new FolderFile();
		file3.setName("test2.txt");
		file3.setMd5("xxx2");
		
		List<FolderFile> localFiles = new ArrayList<>();
		localFiles.add(file1);
		localFiles.add(file2);
		localFiles.add(file3);
		
		
		List<FolderFile> bagitFiles = new ArrayList<>();
		bagitFiles.add(file1);
		bagitFiles.add(file2);
		
		assertTrue(service.isSameFolder(localFiles,bagitFiles));
		
	}
}
