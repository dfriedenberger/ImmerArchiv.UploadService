package de.immerarchiv.repository;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.immerarchiv.app.Config;
import de.immerarchiv.app.RepositoryConfig;
import de.immerarchiv.repository.impl.MetaDataList;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.BagItInfo;
import de.immerarchiv.repository.model.FileInfo;

public class TestRepository {

	
	private RepositoryConfig getRepositoryConfig() throws IOException
	{
		Config config = new ObjectMapper(new YAMLFactory()).readValue(new File("config.yml"),Config.class);
		RepositoryConfig repo = config.repositories.get(0);
		
		/*
		 	RepositoryConfig repo = new RepositoryConfig();
			repo.name = "xxx";
			repo.url = "https://xxx.xxx.xx";
			repo.token = "xxx";
		*/
		
		return repo;
	}
	
	
	@Ignore
	@Test
	public void testConnect() throws IOException, GeneralSecurityException, ParseException {

		RepositoryConfig repo = getRepositoryConfig();
		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		
		MetaDataList metadata = service.resolveStatus();
		System.out.println(metadata);
	
	}
	
	@Ignore
	@Test
	public void testCreateBagit() throws IOException, GeneralSecurityException, ParseException {

		RepositoryConfig repo = getRepositoryConfig();

		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		BagItInfo info = new BagItInfo();
		info.setDescription("test Bagit");
		String bagItId = service.create("123456",info);
		System.out.println(bagItId);
	
	}

	@Ignore
	@Test
	public void testCreateBagitWithSpecialChars() throws IOException, GeneralSecurityException, ParseException {

		RepositoryConfig repo = getRepositoryConfig();

		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		BagItInfo info = new BagItInfo();
		info.setDescription("test äüö");
		String bagItId = service.create("4711",info);
		System.out.println(bagItId);
	
	}
	
	@Ignore
	@Test
	public void testUploadFile() throws IOException, GeneralSecurityException
	{

		RepositoryConfig repo = getRepositoryConfig();

		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		
		service.putFilePart("hello.txt","FirstLine\r\n".getBytes());
		service.putFilePart("hello.txt","LastLine\r\n".getBytes());

	   
		
		String md5 = service.appendFile("123456","HelloWorld.txt","hello.txt","c1106705441f832c0705a81f815b2d67");

		assertEquals("c1106705441f832c0705a81f815b2d67",md5);
		
		
	}
	
	
	@Ignore
	@Test
	public void testListAllBagits() throws IOException, GeneralSecurityException, ParseException
	{
		RepositoryConfig repo = getRepositoryConfig();

		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		Map<String, MetaDataList> bagIts = service.resolveBagits(500, 100);
		
		for(Entry<String, MetaDataList> e : bagIts.entrySet())
		{
			System.out.println(e);
		}
		
	}
	
	@Ignore
	@Test
	public void testListBagit() throws IOException, GeneralSecurityException, ParseException
	{
		RepositoryConfig repo = getRepositoryConfig();
		
		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		List<FileInfo> content = service.resolveBagit("7b646eab-d8df-409d-9819-a3b63edcc4e1");
		
		for(FileInfo info : content)
			System.out.println(info.name);

		System.out.println(content.size());

		
	}
	
}
