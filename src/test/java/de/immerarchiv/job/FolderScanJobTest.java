package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.immerarchiv.job.impl.FolderScanJob;
import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;

public class FolderScanJobTest {

	@Mock
	FolderSystem folderSystem = null;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void test() throws Exception {
		
		
		File dir = new File(this.getClass().getClassLoader().getResource("data").getPath());
		
		List<Folder> folders = new ArrayList<>();
		Folder folder = new Folder();
		folder.setPath(dir.getAbsolutePath());
		folders.add(folder);
		
		when(folderSystem.getFolders()).thenReturn(folders);
		
		Job job = new FolderScanJob(null, null, folderSystem);
		
		job.init();
		while(job.next())
		{			
			System.out.println(job);
		} 
		job.finish();

		verify(folderSystem,times(4)).addFolder(any(Folder.class));
		verify(folderSystem,times(4)).addFile(any(Folder.class),any(FolderFile.class));
		
	}
	@Test
	public void testgetNext() throws Exception {
	
		List<Folder> folders = new ArrayList<>();
		Folder folder = new Folder();
		folders.add(folder);
		
		List<FolderFile> files1 = new ArrayList<>();
		
		FolderFile file1 = new FolderFile();
		file1.setLength(1234);
		files1.add(file1);
		
		when(folderSystem.getFolders()).thenReturn(folders);
		when(folderSystem.selectFiles(folder)).thenReturn(files1);

		Job job = new FolderScanJob(null, null, folderSystem);

		assertEquals(1,job.getNext().size());
	}
}
