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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.immerarchiv.app.Config;
import de.immerarchiv.app.RepositoryConfig;
import de.immerarchiv.job.impl.BagItScanJob;
import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.FileInfo;
import de.immerarchiv.util.interfaces.BagItCache;

public class BagItScanJobTest {

	@Captor
	private ArgumentCaptor<List<FolderFile>> captor;
	
	@Mock
	BagItCache bagItCache;

	@Mock
	Archiv archiv;
	
    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }
	  
	@Test
	public void testNewBagit() throws Exception {
		
		Config config = new ObjectMapper(new YAMLFactory()).readValue(new File("config.yml"),Config.class);
		RepositoryConfig repo = config.repositories.get(0);
		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		List<BagIt> bagits = new ArrayList<>();

		
		
		BagIt bagIt1 = new BagIt();
		bagIt1.setRepo("1");
		bagIt1.setId("29816067-f0f7-4f5c-9cfb-589e40311d23");
		bagIt1.setFiles(0);
		bagIt1.setLastModified(0);
		bagits.add(bagIt1);
		
		BagIt bagIt2 = new BagIt();
		bagIt2.setRepo("1");
		bagIt2.setId("500f44ad-3da4-468a-b14b-7f68daea9cfd");
		bagIt2.setFiles(0);
		bagIt2.setLastModified(0);
		bagits.add(bagIt2);
		
		Job job = new BagItScanJob(service,bagItCache,archiv,bagits);
		
		   
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
		
		verify(archiv).addFile(eq(bagIt1), captor.capture());
		assertEquals(5,captor.getValue().size());
		
		verify(archiv).addFile(eq(bagIt2), captor.capture());
		assertEquals(0,captor.getValue().size());
	}

	@Test
	public void testOldBagit() throws Exception {
		
		RepositoryService service = null; //wird nicht genutzt

		List<BagIt> bagits = new ArrayList<>();
		
		BagIt bagIt1 = new BagIt();
		bagIt1.setRepo("1");
		bagIt1.setId("29816067-f0f7-4f5c-9cfb-589e40311d23");
		bagIt1.setFiles(0);
		bagIt1.setLastModified(0);
		bagits.add(bagIt1);
		
		Job job = new BagItScanJob(service,bagItCache,archiv,bagits);
		
		   
		List<FileInfo> list = new ArrayList<FileInfo>();
		
		FileInfo fileInfo1 = new FileInfo();
		fileInfo1.CheckSumKey = "md5";
		fileInfo1.CheckSumKey = "md5";
		fileInfo1.name = "filenme.ext";
		fileInfo1.length = 17;
		
		list.add(fileInfo1);
		
		
		when(bagItCache.get(bagIt1)).thenReturn(list);

		job.init();

		while(job.next())
			System.out.println(job);
		job.finish();
		
		
		verify(archiv).addFile(eq(bagIt1), captor.capture());
		
		assertEquals(1,captor.getValue().size());

		verify(bagItCache,times(0)).put(eq(bagIt1), anyObject());
		verify(bagItCache).load();
		verify(bagItCache).save();
		
		
		
	}
	
	
}
