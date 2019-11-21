package de.immerarchiv.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.immerarchiv.util.impl.FileChangesWatcherImpl;
import de.immerarchiv.util.interfaces.FileChangesWatcher;

public class FileChangesWatcherImplTest {

	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void test() throws IOException, InterruptedException {
		
		
		File folder1 = folder.newFolder("folder1");
	

		FileChangesWatcher watcher = new FileChangesWatcherImpl();
		
		
		assertFalse(watcher.hasNewFiles());
		
		watcher.addFolder(folder1.getAbsolutePath());

		assertFalse(watcher.hasNewFiles());

		assertTrue(new File(folder1,"test1.txt").createNewFile());
		Thread.sleep(100);
		
		assertTrue(watcher.hasNewFiles());
		assertFalse(watcher.hasNewFiles());

		watcher.deleteFolders();
		assertFalse(watcher.hasNewFiles());
		
		assertTrue(new File(folder1,"test2.txt").createNewFile());
		Thread.sleep(100);
		assertFalse(watcher.hasNewFiles());
		

	
	}

	
	@Test
	public void testnewFolder() throws IOException, InterruptedException {
		
		
		File folder1 = folder.newFolder("folder1");
	

		FileChangesWatcher watcher = new FileChangesWatcherImpl();
		
		
		assertFalse(watcher.hasNewFiles());
		
		watcher.addFolder(folder1.getAbsolutePath());

		assertFalse(watcher.hasNewFiles());

		assertTrue(new File(folder1,"testfolder").mkdir());
		Thread.sleep(100);
		
		assertTrue(watcher.hasNewFiles());
		
		

	
	}
	
	@Test
	public void testRekursiv() throws IOException, InterruptedException {
		
		
		File folder1 = folder.newFolder("folder1");
		File folder2 = new File(folder1,"folder2");
		assertTrue(folder2.mkdir());

		FileChangesWatcher watcher = new FileChangesWatcherImpl();
		
		
		assertFalse(watcher.hasNewFiles());
		
		watcher.addFolder(folder1.getAbsolutePath());

		assertFalse(watcher.hasNewFiles());

		assertTrue(new File(folder2,"test1.txt").createNewFile());
		Thread.sleep(100);
		
		assertTrue(watcher.hasNewFiles());
		
		

	
	}


	
}
