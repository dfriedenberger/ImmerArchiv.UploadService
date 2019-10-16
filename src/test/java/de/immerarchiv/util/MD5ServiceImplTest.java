package de.immerarchiv.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import de.immerarchiv.util.impl.MD5ServiceImpl;
import de.immerarchiv.util.interfaces.MD5Service;

public class MD5ServiceImplTest {

	@Test
	public void test() throws NoSuchAlgorithmException, IOException {
		
		File file = new File(this.getClass().getClassLoader().getResource("data/videos/file_example_MP4_1920_18MG.mp4").getPath());
		MD5Service service = new MD5ServiceImpl();
		
		
		assertEquals("d7a3decdb6280eb0ef3a059ac35ea0ee",service.calc(file));
	}

}
