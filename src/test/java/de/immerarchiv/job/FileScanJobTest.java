package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.immerarchiv.job.impl.FileScanJob;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FileStateMap;
import de.immerarchiv.util.interfaces.MD5Cache;
import de.immerarchiv.util.interfaces.MD5Service;

public class FileScanJobTest {

	@Test
	public void testNewFile() throws Exception {
		
		
		File file = new File(this.getClass().getClassLoader().getResource("data/documents/file-example_PDF_1MB.pdf").getPath());
		
		MD5Service md5service = mock(MD5Service.class);
		MD5Cache md5cache = mock(MD5Cache.class);
		
		when(md5service.calc(file)).thenReturn("test-md5-value");
		
		List<String> files = new ArrayList<String>();
		files.add(file.getAbsolutePath());
		Job job = new FileScanJob(md5service,md5cache,files);		
				
		job.init();
		while(job.next())
		{			
			System.out.println(job);
		} 
		job.finish();

		verify(md5cache).put(file, "test-md5-value");
		verify(md5cache).load();
		verify(md5cache).save();
		assertEquals(file.getAbsolutePath(), job.getResult(FileStateMap.class).get("test-md5-value").get(0));
	
		
	}
	
	@Test
	public void testOldFile() throws Exception {
		
		
		File file = new File(this.getClass().getClassLoader().getResource("data/documents/file-example_PDF_1MB.pdf").getPath());
		
		MD5Service md5service = mock(MD5Service.class);
		MD5Cache md5cache = mock(MD5Cache.class);
		when(md5cache.get(file)).thenReturn("test-md5-value");

		
		List<String> files = new ArrayList<String>();
		files.add(file.getAbsolutePath());
		Job job = new FileScanJob(md5service,md5cache,files);
		
		job.init();
		while(job.next())
		{			
			System.out.println(job);
		} 
		job.finish();

		//no calc md5 
		verify(md5service,times(0)).calc(file);
		verify(md5cache).load();
		verify(md5cache).save();

		assertEquals(file.getAbsolutePath(),  job.getResult(FileStateMap.class).get("test-md5-value").get(0));
	
		
	}
	
	
	

}
