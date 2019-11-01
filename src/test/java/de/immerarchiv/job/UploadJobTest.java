package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.immerarchiv.job.impl.UploadJob;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.util.interfaces.NameService;

public class UploadJobTest {

	@Mock
	RepositoryService repositoryService;
	
	@Mock
	NameService nameService;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Captor
	private ArgumentCaptor<byte[]> captor;
	
	
	@Test
	public void test() throws Exception {
		
		File file = new File(this.getClass().getClassLoader().getResource("data/videos/file_example_MP4_1920_18MG.mp4").getPath());

		
		BagIt bagIt = new BagIt();
		bagIt.setId("12345");
		
		FolderFile folderFile = new FolderFile();
		folderFile.setMd5("testmd5");
		folderFile.setSafeName("filename");
		folderFile.setFile(file);
		folderFile.setLength(file.length());
		
		when(nameService.generateTempName("filename")).thenReturn("tempname.xxx");
		
		Job job = new UploadJob(repositoryService,nameService,bagIt,folderFile);
		
		job.init();
		while(job.next())
			System.out.println(job);
		job.finish();
		
		verify(repositoryService,times(9)).putFilePart(eq("tempname.xxx"), captor.capture());

		List<byte[]> data = captor.getAllValues();
		assertEquals(9, data.size());
		assertEquals(1024 * 1024 * 2, data.get(0).length);
		assertEquals(1024 * 1024 * 2, data.get(1).length);
		assertEquals(file.length() - 1024 * 1024 * 16, data.get(8).length);

		
		verify(repositoryService).appendFile("12345", "filename", "tempname.xxx", "testmd5");

		
	}

}
