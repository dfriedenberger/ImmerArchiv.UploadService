package de.immerarchiv.util.interfaces;

import java.io.File;
import java.io.IOException;

public interface MD5Cache {

	String get(File file);

	void put(File file, String md5) throws IOException;

	void load() throws IOException;
	


}
