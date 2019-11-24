package de.immerarchiv.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.Charsets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.immerarchiv.util.impl.BaseCacheImpl;
import de.immerarchiv.util.impl.FileKeyImpl;
import de.immerarchiv.util.impl.MD5CacheImpl;
import de.immerarchiv.util.interfaces.MD5Cache;

public class MD5CacheImplTest {

	@Rule
	public TemporaryFolder folder= new TemporaryFolder();
	 
	@Test
	public void test() throws IOException {
		
		File cachefile = folder.newFile();
		
		File file = new File(this.getClass().getClassLoader().getResource("data/documents/file-example_PDF_1MB.pdf").getFile());

		File filem = mock(File.class);
		when(filem.length()).thenReturn(1234L);
		when(filem.getAbsolutePath()).thenReturn(file.getAbsolutePath());
		when(filem.lastModified()).thenReturn(1234L);

		
		MD5Cache cache = new MD5CacheImpl(cachefile);
		
		cache.load();
		
		cache.put(file, "md5-sum-1");
		cache.put(filem, "md5-sum-2");

		assertEquals("md5-sum-2",cache.get(filem));
		assertEquals(null,cache.get(file));

		cache.load(); //=> produce log "ignore loading"

		long l1 = cachefile.length();

		MD5Cache cache1compr = new MD5CacheImpl(cachefile);
		cache1compr.load();
		
		long l2 = cachefile.length();

		assertTrue(l2 < l1);
		
		
		MD5Cache cache2 = new MD5CacheImpl(cachefile);
		cache2.load();
		assertEquals("md5-sum-2",cache2.get(filem));

		
		
	}
	
	//private static int CNT = 45590; realistic scenario  1.5 Minuten
	private static int CNT = 1000;
	
	@Test
	public void testBigFile() throws IOException {
		
		System.out.println("Start "+new Date());

		File cachefile = folder.newFile();
		MD5Cache cache = new MD5CacheImpl(cachefile);
		
		
		for(int i = 0;i < CNT;i++)
		{
			File file = mock(File.class);
			when(file.lastModified()).thenReturn(1234567L);
			when(file.length()).thenReturn(815L);
			when(file.getAbsolutePath()).thenReturn("pathtofile"+i);
			
			cache.put(file, "cksum"+i);
		}
		System.out.println("Start load "+new Date());
		cache.load();
		System.out.println("Stop load "+new Date());


		
	}
	
	

	
}
