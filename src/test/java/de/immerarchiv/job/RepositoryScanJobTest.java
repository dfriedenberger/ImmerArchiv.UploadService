package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.immerarchiv.job.impl.RepositoryScanJob;
import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.repository.impl.MetaDataList;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.MetaDataKeys;

public class RepositoryScanJobTest {

	@Captor
	private ArgumentCaptor<BagIt> captor;

	@Mock
	Archiv archiv;
	
	@Mock
	RepositoryService repositoryService;
	
    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void test() throws Exception {
		
		
		Job job = new RepositoryScanJob(archiv,null, repositoryService);
			
		
		
		MetaDataList metadatalist = new MetaDataList();
		metadatalist.add(MetaDataKeys.mdRepositoryCntBagits, 2);
		when(repositoryService.resolveStatus()).thenReturn(metadatalist);
		
		
		Map<String, MetaDataList> map = new HashMap<>();
		
		MetaDataList bagIt1 = new MetaDataList();
		bagIt1.add(MetaDataKeys.mdDescription,"bagit1 Name");
		bagIt1.add(MetaDataKeys.mdBagitCntFiles,4711);
		bagIt1.add(MetaDataKeys.mdDateLastModified,MetaDataList.sdf.format(new Date(1234567)));
		map.put("bagit1", bagIt1);
		MetaDataList bagIt2 = new MetaDataList();
		bagIt2.add(MetaDataKeys.mdBagitCntFiles,123);
		bagIt2.add(MetaDataKeys.mdDescription,"bagit2 Name");
		bagIt2.add(MetaDataKeys.mdDateLastModified,MetaDataList.sdf.format(new Date(12345)));
		map.put("bagit2", bagIt2);
		
		when(repositoryService.resolveBagits(0, 100)).thenReturn(map);
		when(repositoryService.getId()).thenReturn("reposid");

		job.init();
		while(job.next())
			System.out.println(job);
		job.finish();

				
		verify(archiv,times(2)).addBagIt(captor.capture());
		
		assertEquals(2, captor.getAllValues().size()); 
		
		assertEquals("reposid",captor.getAllValues().get(0).getRepo());
		assertEquals("bagit1",captor.getAllValues().get(0).getId());
		assertEquals(4711,captor.getAllValues().get(0).getFiles());
		assertEquals("bagit1 Name",captor.getAllValues().get(0).getDescription());

		
	}
	
	@Test
	public void testGetNextJobs() throws Exception {
		
	
		
		Job job = new RepositoryScanJob(archiv,null, repositoryService);
			
		
		
		List<BagIt> bagits = new ArrayList<>();
		bagits.add(new BagIt());

		when(repositoryService.getId()).thenReturn("reposid");
		when(archiv.selectBagItsForRepository("reposid")).thenReturn(bagits);
		

		assertEquals(1,job.getNext().size());
				
		
	}

}
