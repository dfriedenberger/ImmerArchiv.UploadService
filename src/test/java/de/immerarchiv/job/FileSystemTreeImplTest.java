package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

import de.immerarchiv.job.impl.FileSystemTreeImpl;
import de.immerarchiv.job.interfaces.FileSystemTree;
import de.immerarchiv.job.model.TreeEntry;

public class FileSystemTreeImplTest {

	
	private File mockFile(File file,boolean isDirectory) {

		if(file == null) return null;
		
		File parent = mockFile(file.getParentFile(),true);
		
		File mock = mock(File.class);
		when(mock.getParentFile()).thenReturn(parent);
		when(mock.isDirectory()).thenReturn(isDirectory);
		when(mock.isFile()).thenReturn(!isDirectory);
		when(mock.getAbsolutePath()).thenReturn(file.getAbsolutePath());
		when(mock.getName()).thenReturn(file.getName());

		return mock;
	}
	
	@Test
	public void testEmpty() {
		FileSystemTree tree = new FileSystemTreeImpl();
		assertEquals(0,tree.resolveIds(0).size());
		assertEquals(0,tree.resolveChilds(0).size());
	}
	@Test
	public void testResolveFiles() {
		
		FileSystemTree tree = new FileSystemTreeImpl();
		
		
		assertEquals(4,tree.get(mockFile(new File("D:/pfad1/pfad2/file.txt"),false)).intValue());
		assertEquals(5,tree.get(mockFile(new File("D:/pfad1/pfad2/file2.txt"),false)).intValue());
		assertEquals(7,tree.get(mockFile(new File("D:/pfadX/file2.txt"),false)).intValue());
		assertEquals(8,tree.get(mockFile(new File("D:/pfadY"),true)).intValue());

		
		assertEquals(3,tree.resolveIds(0).size());
		assertTrue(tree.resolveIds(0).contains(4));
		assertTrue(tree.resolveIds(0).contains(5));
		assertTrue(tree.resolveIds(0).contains(7));
		
		assertEquals(2,tree.resolveIds(3).size());
		assertTrue(tree.resolveIds(3).contains(4));
		assertTrue(tree.resolveIds(3).contains(5));

		assertEquals(2,tree.resolveIds(2).size());
		assertTrue(tree.resolveIds(2).contains(4));
		assertTrue(tree.resolveIds(2).contains(5));

		assertEquals(0,tree.resolveIds(8).size());

	}

	@Test
	public void testResolveChilds() {
		
		FileSystemTree tree = new FileSystemTreeImpl();
		
		
		assertEquals("D:\\",new File("D:/Stuff").getParentFile().getAbsolutePath());
		assertEquals("",new File("D:/Stuff").getParentFile().getName());

		assertEquals(4,tree.get(mockFile(new File("D:/pfad1/pfad2/file.txt"),false)).intValue());
		assertEquals(5,tree.get(mockFile(new File("D:/pfad1/pfad2/file2.txt"),false)).intValue());
		assertEquals(7,tree.get(mockFile(new File("D:/pfadX/file2.txt"),false)).intValue());
		assertEquals(8,tree.get(mockFile(new File("D:/pfadY"),true)).intValue());

		
		assertEquals(1,tree.resolveChilds(0).size());
		assertEquals("D:",tree.resolveChilds(0).get(0).getName());
		
		List<TreeEntry> r = tree.resolveChilds(1);
		assertEquals(3,r.size());
		assertEquals("pfad1/pfad2",r.get(0).getName());
		assertEquals("pfadX",r.get(1).getName());
		assertEquals("pfadY",r.get(2).getName());
		assertEquals(3,r.get(0).getId());
		assertEquals(6,r.get(1).getId());
		assertEquals(8,r.get(2).getId());
		
		assertEquals(2,tree.resolveChilds(3).size());
		assertEquals("file.txt",tree.resolveChilds(3).get(0).getName());
		
		
		assertEquals(1,tree.resolveChilds(2).size());
		assertEquals("pfad2",tree.resolveChilds(2).get(0).getName());


	}


}
