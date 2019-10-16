package de.immerarchiv.job;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.immerarchiv.job.impl.FolderScanJob;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.FileList;

public class FolderScanJobTest {

	@Test
	public void test() throws Exception {
		
		
		File folder = new File(this.getClass().getClassLoader().getResource("data").getPath());
		
		List<String> folders = new ArrayList<String>();
		folders.add(folder.getAbsolutePath());
		Job job = new FolderScanJob(folders);
		
		
		while(job.next())
		{			
			System.out.println(job);
		} 
		
		
		assertEquals(4,job.getResult(FileList.class).size());
	
		
	}

	
}
