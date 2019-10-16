package de.immerarchiv.job;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.immerarchiv.app.Config;
import de.immerarchiv.app.RepositoryConfig;
import de.immerarchiv.job.impl.RepositoryScanJob;
import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagItList;
import de.immerarchiv.repository.impl.RepositoryService;

public class RepositoryScanJobTest {

	@Test
	public void test() throws Exception {
		
		
		Config config = new ObjectMapper(new YAMLFactory()).readValue(new File("config.yml"),Config.class);
		RepositoryConfig repo = config.repositories.get(0);
		RepositoryService service = new RepositoryService(repo.url,repo.name, repo.token);
		
		Job job = new RepositoryScanJob("1",service);
				
		job.init();
		while(job.next())
			System.out.println(job);
		job.finish();


		assertEquals(532,job.getResult(BagItList.class).size());
						
	}

}
