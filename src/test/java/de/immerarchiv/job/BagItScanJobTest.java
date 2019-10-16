package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.immerarchiv.app.Config;
import de.immerarchiv.app.RepositoryConfig;
import de.immerarchiv.job.impl.BagItScanJob;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.BagItList;
import de.immerarchiv.job.model.FileInfoList;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.FileInfo;
import de.immerarchiv.util.interfaces.BagItCache;

public class BagItScanJobTest {

	@Test
	public void testNewBagit() throws Exception {
		
		Config config = new ObjectMapper(new YAMLFactory()).readValue(new File("config.yml"),Config.class);
		RepositoryConfig repo = config.repositories.get(0);
		RepositoryService service = new RepositoryService(repo.url,repo.name, repo.token);
		
		BagItCache bagItCache = mock(BagItCache.class);

		BagItList bagits = new BagItList();
		
		BagIt bagIt1 = new BagIt();
		bagIt1.repo = "1";
		bagIt1.id = "00198f2e-2015-48d0-aa39-d6cb55e6a3eb";
		bagIt1.files = 12;
		bagIt1.lastModified = 4711;
		bagits.add(bagIt1);
		
		BagIt bagIt2 = new BagIt();
		bagIt2.repo = "1";
		bagIt2.id = "006fd99c-876e-4bf9-adb9-29e57f46f81e";
		bagIt2.files = 1;
		bagIt2.lastModified = 815;
		bagits.add(bagIt2);
		
		Job job = new BagItScanJob(service,bagItCache,bagits);
		
		   
		when(bagItCache.get(bagIt1)).thenReturn(null);
		when(bagItCache.get(bagIt2)).thenReturn(null);

		job.init();

		while(job.next())
			System.out.println(job);
		job.finish();
		
		
		

		verify(bagItCache).put(eq(bagIt1), anyObject());
		verify(bagItCache).put(eq(bagIt2), anyObject());
		verify(bagItCache).load();
		verify(bagItCache).save();
		
		assertEquals(67,job.getResult(FileInfoList.class).size());
		
	}

	@Test
	public void testOldBagit() throws Exception {
		
		RepositoryService service = null; //wird nicht genutzt
		
		BagItCache bagItCache = mock(BagItCache.class);

		BagItList bagits = new BagItList();
		
		BagIt bagIt1 = new BagIt();
		bagIt1.repo = "1";
		bagIt1.id = "00198f2e-2015-48d0-aa39-d6cb55e6a3eb";
		bagIt1.files = 12;
		bagIt1.lastModified = 4711;
		bagits.add(bagIt1);		
		
		Job job = new BagItScanJob(service,bagItCache,bagits);
		
		   
		List<FileInfo> list = new ArrayList<FileInfo>();
		list.add(new FileInfo());
		when(bagItCache.get(bagIt1)).thenReturn(list);

		job.init();

		while(job.next())
			System.out.println(job);
		job.finish();
		
		
		

		verify(bagItCache,times(0)).put(eq(bagIt1), anyObject());
		verify(bagItCache).load();
		verify(bagItCache).save();
		
		assertEquals(1,job.getResult(FileInfoList.class).size());
		
	}
	
	
}
