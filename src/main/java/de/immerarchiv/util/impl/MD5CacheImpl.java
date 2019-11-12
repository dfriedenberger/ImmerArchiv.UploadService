package de.immerarchiv.util.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.codec.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.util.interfaces.MD5Cache;

public class MD5CacheImpl extends BaseCacheImpl<FileKeyImpl, String> implements MD5Cache {

	
	private final static Logger logger = LogManager.getLogger(MD5CacheImpl.class);

	private final File cachefile;

	private boolean loaded = false;


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
		super.put(key,md5);
		
		try(Writer output = new BufferedWriter(new OutputStreamWriter(
		        new FileOutputStream(cachefile, true), "UTF-8")))
		{
			output.write(md5+" = "+key+"\r\n");
		}
		
	}

	@Override
	public void load() throws IOException {
		
		if(loaded)
		{
			logger.info("cache allways loaded, ignore loading");
			return;
		}
		
		int lines = 0;
		if(cachefile.exists())
		{
			List<String> liness = Files.readAllLines(cachefile.toPath(), Charsets.UTF_8);
			System.out.println(liness.size());
			for(String line : liness)
			{
				  int i = line.indexOf("=");
				     lines++;
				     String md5 = line.substring(0,i).trim();
				     FileKeyImpl key = FileKeyImpl.parse(line.substring(i+1).trim());
				     super.put(key, md5);
			}		
	     }
		loaded = true;
	
		if(lines > super.size())
		{
			logger.info("compress cache, because has duplicate entries");
			try(Writer output = new BufferedWriter(new OutputStreamWriter(
			        new FileOutputStream(cachefile), "UTF-8")))
			{
				
				for(Entry<FileKeyImpl, String> e : super.entrySet())
					output.write(e.getValue()+" = "+e.getKey()+"\r\n");
			}
		}
		
	}

	

	

}
