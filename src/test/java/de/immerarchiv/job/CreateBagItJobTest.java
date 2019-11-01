package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.immerarchiv.job.impl.CreateBagItJob;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.BagItInfo;

public class CreateBagItJobTest {

	@Mock
	RepositoryService repositoryService;
	
	@Captor
	private ArgumentCaptor<BagItInfo> captor;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() throws Exception {
		
		BagIt bagIt = new BagIt();
		bagIt.setRepo("repo");
		bagIt.setId("id");
		bagIt.setDescription("test description");

		
		Job job = new CreateBagItJob(repositoryService,bagIt);
		
		job.init();
		while(job.next())
			System.out.println(job);
		job.finish();
				
		verify(repositoryService).create(eq("id"), captor.capture());
		assertEquals("test description",captor.getValue().getDescription());
		
	}

}
