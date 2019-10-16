package de.immerarchiv.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.repository.model.FileInfo;
import de.immerarchiv.util.impl.BagItCacheImpl;
import de.immerarchiv.util.interfaces.BagItCache;

public class BagItCacheImplTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void test() throws IOException {
	
		File cachefolder = folder.newFolder();

		BagIt bagit1 = new BagIt();
		bagit1.repo = "repo1";
		bagit1.id = "bagitid";
		bagit1.files = 10;
		bagit1.lastModified = 1000;
		
		BagIt bagit1m = new BagIt();
		bagit1m.repo = "repo1";
		bagit1m.id = "bagitid";
		bagit1m.files = 11;
		bagit1m.lastModified = 2000;
		
		BagIt bagit2 = new BagIt();
		bagit2.repo = "repo2";
		bagit2.id = "bagitid";
		bagit2.files = 10;
		bagit2.lastModified = 1000;
		
				
		BagItCache cache = new BagItCacheImpl(cachefolder);
		
		cache.load();
		
		
		List<FileInfo> list1 = new ArrayList<FileInfo>();
		List<FileInfo> list2 = new ArrayList<FileInfo>();
		FileInfo fileInfo = new FileInfo();
		fileInfo.name = "file.jpg";
		list2.add(fileInfo);
		
		cache.put(bagit1, list1);
		assertEquals(1,cachefolder.listFiles().length);

		cache.put(bagit1m, list2);
		assertEquals(1,cachefolder.listFiles().length);
		
		cache.put(bagit2, list1);

		assertEquals(list2,cache.get(bagit1m));
		assertEquals(null,cache.get(bagit1));

		cache.save();

		
		assertEquals(2,cachefolder.listFiles().length);

		BagItCache cache2 = new BagItCacheImpl(cachefolder);
		cache2.load();
		assertEquals("file.jpg",cache2.get(bagit1m).get(0).name);	
	}

}
