package de.immerarchiv.util.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.immerarchiv.util.interfaces.MD5Service;

public class MD5ServiceImpl implements MD5Service {

	@Override
	public String calc(File file) throws IOException, NoSuchAlgorithmException {

		MessageDigest complete = MessageDigest.getInstance("MD5");
		byte[] buffer = new byte[0xFFFF];

		try (InputStream fis = new FileInputStream(file)) {
			int numRead;

			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
		}
		byte[] digest = complete.digest();
		String result = "";

		for (int i = 0; i < digest.length; i++) {
			result += Integer.toString((digest[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		return result;
	}

}
