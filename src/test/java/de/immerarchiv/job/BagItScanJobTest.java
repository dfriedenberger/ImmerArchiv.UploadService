package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
	
	@Mock
	RepositoryService repositoryService;
	
    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }
	  
	@Test
	public void testNewBagit() throws Exception {
		
				
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
		
		List<FileInfo> fileInfoList1 = new ArrayList<>();
		FileInfo fileinfo1 = new FileInfo();
		fileinfo1.CheckSumKey = "md5";
		fileinfo1.name = "filename1.txt";

		fileInfoList1.add(fileinfo1);
		FileInfo fileinfo2 = new FileInfo();
		fileinfo2.CheckSumKey = "md5";
		fileinfo2.name = "filename1.txt";

		fileInfoList1.add(fileinfo2);
		when(repositoryService.resolveBagit("29816067-f0f7-4f5c-9cfb-589e40311d23")).thenReturn(fileInfoList1);
		
		List<FileInfo> fileInfoList2 = new ArrayList<>();
		when(repositoryService.resolveBagit("500f44ad-3da4-468a-b14b-7f68daea9cfd")).thenReturn(fileInfoList2);
		
		Job job = new BagItScanJob(repositoryService,bagItCache,archiv,bagits);
		
		   
		when(bagItCache.get(bagIt1)).thenReturn(null);
		when(bagItCache.get(bagIt2)).thenReturn(null);

		job.init();

		while(job.next())
			System.out.println(job);
		
		
		

		verify(bagItCache).put(eq(bagIt1), anyObject());
		verify(bagItCache).put(eq(bagIt2), anyObject());
		verify(bagItCache).load();
		
		verify(archiv).addFile(eq(bagIt1), captor.capture());
		assertEquals(2,captor.getValue().size());
		
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
		
		
		verify(archiv).addFile(eq(bagIt1), captor.capture());
		
		assertEquals(1,captor.getValue().size());

		verify(bagItCache,times(0)).put(eq(bagIt1), anyObject());
		verify(bagItCache).load();
		
		
		
	}
	
	
}
