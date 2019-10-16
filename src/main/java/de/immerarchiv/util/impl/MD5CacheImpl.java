package de.immerarchiv.util.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map.Entry;

import de.immerarchiv.util.interfaces.MD5Cache;

public class MD5CacheImpl extends BaseCacheImpl<FileKeyImpl, String> implements MD5Cache {

	
	
	private final File cachefile;

	public MD5CacheImpl(File cachefile) {
		this.cachefile = cachefile;
	}

	
	@Override
	public String get(File file) {
		return get(new FileKeyImpl(file));
	}

	

	@Override
	public void put(File file, String md5) throws IOException {
		
		FileKeyImpl key = new FileKeyImpl(file);
		put(key,md5);
		
		try(Writer output = new BufferedWriter(new OutputStreamWriter(
		        new FileOutputStream(cachefile, true), "UTF-8")))
		{
			output.write(md5+" = "+key+"\r\n");
		}
		
	}

	@Override
	public void load() throws IOException {
		
		
		if(cachefile.exists())
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cachefile), "UTF8")))
			{
				while(reader.ready()) {
				     String line = reader.readLine();
				     int i = line.indexOf("=");
				     
				     String md5 = line.substring(0,i).trim();
				     FileKeyImpl key = FileKeyImpl.parse(line.substring(i+1).trim());
				     put(key, md5);
				     
				}
			}
	}

	

	@Override
	public void save() throws IOException {

		try(Writer output = new BufferedWriter(new OutputStreamWriter(
		        new FileOutputStream(cachefile), "UTF-8")))
		{
			
			for(Entry<FileKeyImpl, String> e : super.entrySet())
				output.write(e.getValue()+" = "+e.getKey()+"\r\n");
		}
		
		
		
	}

}
