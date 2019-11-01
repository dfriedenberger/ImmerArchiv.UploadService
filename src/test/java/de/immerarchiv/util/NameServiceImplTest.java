package de.immerarchiv.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.Test;

import de.immerarchiv.job.model.Folder;
import de.immerarchiv.util.impl.NameServiceImpl;
import de.immerarchiv.util.interfaces.NameService;

public class NameServiceImplTest {

	@Test
	public void testGenerateTempName() {
		
		NameService service = new NameServiceImpl();
		
		assertTrue(Pattern.matches("[a-z]{10,10}[.]txt", service.generateTempName("hello.txt")));
		
	}

	
	@Test
	public void testCreateDescription() {
		
		NameService service = new NameServiceImpl();
		
		Folder folder = new Folder();
		folder.setPath("Stuff\\Bilder\\012_Hamburg");
		assertEquals("Bilder/012_Hamburg", service.createDescription(folder));
		
	}
	
	@Test
	public void testCreateSafeName() {
		
		NameService service = new NameServiceImpl();
		
		assertEquals("Hello_W_rld.txt", service.createSafeName(new File("abc/Hello Wörld.txt")));
		
	}
	
}
