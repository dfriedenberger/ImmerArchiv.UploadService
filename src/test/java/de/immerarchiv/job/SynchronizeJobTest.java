package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.immerarchiv.job.impl.SynchronizeJob;
import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;

public class SynchronizeJobTest {

	@Mock
	FolderSystem folderSystem;

	@Mock
	Archiv archiv;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() throws Exception {
		
		
		List<Folder> folders = new ArrayList<Folder>();
		
		Folder folder1 = new Folder();
		folders.add(folder1);

		List<FolderFile> files1 = new ArrayList<FolderFile>();
		
		FolderFile file1 = new FolderFile();
		file1.setName("name1");
		file1.setMd5("md5-1");

		FolderFile file2 = new FolderFile();
		file2.setName("name2");
		file2.setMd5("md5-2");

		files1.add(file1);
		files1.add(file2);
		when(folderSystem.getFolders()).thenReturn(folders);
		when(folderSystem.selectFiles(folder1)).thenReturn(files1);

		
		List<BagIt> bagits = new ArrayList<BagIt>();
		
		BagIt bagIt1 = new BagIt();
		bagIt1.setRepo("1");
		bagIt1.setId("29816067-f0f7-4f5c-9cfb-589e40311d23");
		bagIt1.setFiles(0);
		bagIt1.setLastModified(0);
		
		BagIt bagIt2 = new BagIt();
		bagIt2.setRepo("1");
		bagIt2.setId("500f44ad-3da4-468a-b14b-7f68daea9cfd");
		bagIt2.setFiles(0);
		bagIt2.setLastModified(0);
		
		
		bagits.add(bagIt1);
		bagits.add(bagIt2);

		
		when(archiv.findBagits(files1)).thenReturn(bagits);
		
		
		when(archiv.fileExists(bagIt1, file1)).thenReturn(true);
		when(archiv.fileExists(bagIt1, file2)).thenReturn(false);
		when(archiv.fileExists(bagIt2, file1)).thenReturn(false);
		when(archiv.fileExists(bagIt2, file2)).thenReturn(false);

		Job job = new SynchronizeJob(archiv,folderSystem);
		
		
		job.init();
		while(job.next())
			System.out.println(job);
		job.finish();
		
		List<Job> nextJobs = job.getNext();
		assertEquals(3,nextJobs.size());
	}

}
