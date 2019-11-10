package de.immerarchiv.job;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import de.immerarchiv.job.impl.FileIgnoreFilterImpl;
import de.immerarchiv.job.interfaces.FileIgnoreFilter;

public class FileIgnoreFilterImplTest {

	@Test
	public void test() {
		FileIgnoreFilter fileIgnoreFilter = new FileIgnoreFilterImpl();
		
		fileIgnoreFilter.addPattern("Thumbs.db");
		assertFalse(fileIgnoreFilter.ignore(new File("path/path2/image.png")));
		assertTrue(fileIgnoreFilter.ignore(new File("path/path2/Thumbs.db")));
	}

}
