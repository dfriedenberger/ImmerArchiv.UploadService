package de.immerarchiv.util.impl;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.util.interfaces.FileChangesWatcher;

public class FileChangesWatcherImpl implements FileChangesWatcher {

	private final static Logger logger = LogManager.getLogger(FileChangesWatcherImpl.class);

	private final WatchService watchService;
	private final List<WatchKey> registeredKeys = new ArrayList<>();

	public FileChangesWatcherImpl() throws IOException {
		watchService = FileSystems.getDefault().newWatchService();
	}
	
	@Override
	public boolean hasNewFiles() {

		WatchKey wk = watchService.poll();
		if (wk != null)
		{
			for(WatchEvent<?> event : wk.pollEvents()) {
				logger.trace(event.context());
			}
			wk.reset();
			return true;
		}
		
		return false;
	}

	

	@Override
	public void addFolder(String path) {
		
		try
		{
			Path root = Paths.get(path);
			
			// register all subfolders
		    Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
		    	  @Override
			      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					WatchKey watchKey = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);	
					registeredKeys.add(watchKey);
		            return FileVisitResult.CONTINUE;
			      }
		    });
			
		} 
		catch(IOException e)
		{
			logger.error(e);
		}
		
	}

	@Override
	public void deleteFolders() {

		for(WatchKey wk : registeredKeys)
			wk.cancel();
		registeredKeys.clear();
	}

}
