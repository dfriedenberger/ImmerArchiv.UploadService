package de.immerarchiv.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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

		long l1 = cachefile.length();
		cache.save();
		long l2 = cachefile.length();

		assertTrue(l2 < l1);
		
		
		MD5Cache cache2 = new MD5CacheImpl(cachefile);
		cache2.load();
		assertEquals("md5-sum-2",cache2.get(filem));

		
		
	}

}
