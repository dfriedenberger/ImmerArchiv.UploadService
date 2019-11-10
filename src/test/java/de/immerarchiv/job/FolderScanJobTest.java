package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.immerarchiv.job.impl.FileIgnoreFilterImpl;
import de.immerarchiv.job.impl.FolderScanJob;
import de.immerarchiv.job.interfaces.FolderSystem;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FileSystemState;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.util.interfaces.NameService;

public class FolderScanJobTest {

	@Mock
	FolderSystem folderSystem;
	
	@Mock 
	NameService nameService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Captor
	private ArgumentCaptor<FolderFile> captor;
	
	@Test
	public void test() throws Exception {
		
		
		File dir = new File(this.getClass().getClassLoader().getResource("data").getPath());
		
		List<Folder> folders = new ArrayList<>();
		Folder folder = new Folder();
		folder.setPath(dir.getAbsolutePath());
		folders.add(folder);
		
		when(folderSystem.getFolders()).thenReturn(folders);
		when(nameService.createSafeName(any(File.class))).thenAnswer(new Answer<String>() {
		    @Override
		    public String answer(InvocationOnMock invocation) throws Throwable {
		      Object[] args = invocation.getArguments();
		      return ((File) args[0]).getName();
		    }
		  });
		
		Job job = new FolderScanJob(null, nameService, null, folderSystem,new FileIgnoreFilterImpl(),new FileSystemState());
		
		job.init();
		while(job.next())
		{			
			System.out.println(job);
		} 

		verify(folderSystem,times(4)).addFolder(any(Folder.class));
		verify(folderSystem,times(4)).addFile(any(Folder.class),captor.capture());
		
		
		captor.getAllValues().stream().forEach(folderFile -> {
			assertNotNull(folderFile.getFile());
		});
		
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

		Job job = new FolderScanJob(null, nameService, null, folderSystem,null,new FileSystemState());

		assertEquals(1,job.getNext().size());
	}
}
