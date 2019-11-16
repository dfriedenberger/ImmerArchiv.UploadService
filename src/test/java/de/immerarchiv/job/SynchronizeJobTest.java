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

import de.immerarchiv.job.impl.SynchronizeJob;
import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.FileSystemState;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.repository.impl.RepositoryService;

public class SynchronizeJobTest {

	@Mock
	FolderSystem folderSystem;

	@Mock
	Archiv archiv;

	@Mock
	RepositoryService repositoryService1;

	@Mock
	RepositoryService repositoryService2;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() throws Exception {
		
		
		
		List<RepositoryService> repositoryServices = new ArrayList<>();
		repositoryServices.add(repositoryService1);
		repositoryServices.add(repositoryService2);

		when(repositoryService1.getId()).thenReturn("1");
		when(repositoryService2.getId()).thenReturn("2");

		List<Folder> folders = new ArrayList<Folder>();
		
		Folder folder1 = new Folder();
		folders.add(folder1);

		List<FolderFile> files1 = new ArrayList<FolderFile>();
		
		FolderFile file1 = new FolderFile();
		file1.setSafeName("name1");
		file1.setMd5("md5-1");
		file1.setFile(new File("pathtofile/name1.txt"));

		FolderFile file2 = new FolderFile();
		file2.setSafeName("name2");
		file2.setMd5("md5-2");
		file2.setFile(new File("pathtofile/name2.txt"));

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

		
		when(archiv.findBagits(folder1,files1)).thenReturn(bagits);
		
		
		when(archiv.fileExists(bagIt1, file1)).thenReturn(true);
		when(archiv.fileExists(bagIt1, file2)).thenReturn(false);
		when(archiv.fileExists(bagIt2, file1)).thenReturn(false);
		when(archiv.fileExists(bagIt2, file2)).thenReturn(false);

		
		
		
		FileSystemState fileSystemState = new FileSystemState();
		Job job = new SynchronizeJob(repositoryServices,archiv,folderSystem,fileSystemState);
		
		
		job.init();
		while(job.next())
			System.out.println(job);
		
		List<Job> nextJobs = job.getNext();
		assertEquals(3,nextJobs.size());
		assertEquals(2,fileSystemState.size());
		
	}

	
	@Test
	public void testDuplicateFolders() throws Exception {
		
		//https://github.com/dfriedenberger/ImmerArchiv.UploadService/issues/5# file exists bug
		
		
		List<RepositoryService> repositoryServices = new ArrayList<>();
		repositoryServices.add(repositoryService1);
		repositoryServices.add(repositoryService2);

		when(repositoryService1.getId()).thenReturn("1");
		when(repositoryService2.getId()).thenReturn("2");

		List<Folder> folders = new ArrayList<Folder>();
		
		Folder folder1 = new Folder();
		folder1.setPath("pathtofile");
		Folder folder2 = new Folder();
		folder2.setPath("pathtofile2");

		folders.add(folder1);
		folders.add(folder2);

		List<FolderFile> files1 = new ArrayList<FolderFile>();
		FolderFile file1 = new FolderFile();
		file1.setSafeName("name1.txt");
		file1.setMd5("md5-1");
		file1.setFile(new File("pathtofile/name1.txt"));
		files1.add(file1);
		
		
		List<FolderFile> files2 = new ArrayList<FolderFile>();
		FolderFile file2 = new FolderFile();
		file2.setSafeName("name1.txt");
		file2.setMd5("md5-1");
		file2.setFile(new File("pathtofile2/name1.txt"));
		files2.add(file2);
		
		when(folderSystem.getFolders()).thenReturn(folders);
		when(folderSystem.selectFiles(folder1)).thenReturn(files1);
		when(folderSystem.selectFiles(folder2)).thenReturn(files2);

		
		List<BagIt> bagits = new ArrayList<BagIt>();
		
		BagIt bagIt1 = new BagIt();
		bagIt1.setRepo("1");
		bagIt1.setId("29816067-f0f7-4f5c-9cfb-589e40311d23");
		bagIt1.setFiles(0);
		bagIt1.setLastModified(0);
		bagits.add(bagIt1);

		BagIt bagIt2 = new BagIt();
		bagIt2.setRepo("2");
		bagIt2.setId("29816067-f0f7-4f5c-9cfb-589e40311d23");
		bagIt2.setFiles(0);
		bagIt2.setLastModified(0);
		bagits.add(bagIt2);
		
		when(archiv.findBagits(folder1,files1)).thenReturn(bagits);
		when(archiv.findBagits(folder2,files2)).thenReturn(bagits);

		
		when(archiv.fileExists(same(bagIt1), same(file1))).thenReturn(false);
		when(archiv.fileExists(same(bagIt1), same(file2))).thenReturn(false);
		when(archiv.fileExists(same(bagIt2), same(file1))).thenReturn(false);
		when(archiv.fileExists(same(bagIt2), same(file2))).thenReturn(false);
		
		
		
		FileSystemState fileSystemState = new FileSystemState();
		Job job = new SynchronizeJob(repositoryServices,archiv,folderSystem,fileSystemState);
		
		
		job.init();
		while(job.next())
			System.out.println(job);
		
		List<Job> nextJobs = job.getNext();
		assertEquals(2,nextJobs.size());
		assertEquals(2,fileSystemState.size());
		
	}

}
