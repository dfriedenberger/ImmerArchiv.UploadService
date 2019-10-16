package de.immerarchiv.util.interfaces;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface MD5Service {

	String calc(File file) throws IOException, NoSuchAlgorithmException;

}
