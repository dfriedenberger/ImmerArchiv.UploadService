package de.immerarchiv.repository;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.immerarchiv.app.Config;
import de.immerarchiv.app.RepositoryConfig;
import de.immerarchiv.repository.impl.MetaDataList;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.repository.model.BagItInfo;

public class TestRepository {

	@Test
	public void testConnect() throws IOException, GeneralSecurityException, ParseException {

		Config config = new ObjectMapper(new YAMLFactory()).readValue(new File("config.yml"),Config.class);
		RepositoryConfig repo = config.repositories.get(0);
		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		
		MetaDataList metadata = service.resolveStatus();
		System.out.println(metadata);
	
	}
	
	
	@Test
	public void testCreateBagit() throws IOException, GeneralSecurityException, ParseException {

		Config config = new ObjectMapper(new YAMLFactory()).readValue(new File("config.yml"),Config.class);
		RepositoryConfig repo = config.repositories.get(0);
		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		BagItInfo info = new BagItInfo();
		info.setDescription("test Bagit");
		String bagItId = service.create("123456",info);
		System.out.println(bagItId);
	
	}

	@Test
	public void testUploadFile() throws IOException, GeneralSecurityException
	{

		Config config = new ObjectMapper(new YAMLFactory()).readValue(new File("config.yml"),Config.class);
		RepositoryConfig repo = config.repositories.get(0);
		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		
		service.putFilePart("hello.txt","FirstLine\r\n".getBytes());
		service.putFilePart("hello.txt","LastLine\r\n".getBytes());

	   
		
		String md5 = service.appendFile("123456","HelloWorld.txt","hello.txt","c1106705441f832c0705a81f815b2d67");

		assertEquals("c1106705441f832c0705a81f815b2d67",md5);
		
		
	}
	
	
	@Test
	public void testListAllBagits() throws IOException, GeneralSecurityException, ParseException
	{
		Config config = new ObjectMapper(new YAMLFactory()).readValue(new File("config.yml"),Config.class);
		RepositoryConfig repo = config.repositories.get(0);
		RepositoryService service = new RepositoryService("id",repo.url,repo.name, repo.token);
		
		Map<String, MetaDataList> bagIts = service.resolveBagits(0, 10);
		
		
		System.out.println(bagIts);

		
	}
	
}
