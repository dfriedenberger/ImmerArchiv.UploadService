package de.immerarchiv.util.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.repository.model.FileInfo;
import de.immerarchiv.util.interfaces.BagItCache;

public class BagItCacheImpl extends BaseCacheImpl<BagItKeyImpl,List<FileInfo>> implements BagItCache {

	private final File folder;

	public BagItCacheImpl(File folder) {
		this.folder = folder;
	}

	@Override
	public List<FileInfo> get(BagIt bagit) {
		return super.get(new BagItKeyImpl(bagit));
	}

	@Override
	public void put(BagIt bagit, List<FileInfo> fileInfoList)
			throws IOException {
		
		BagItKeyImpl key = new BagItKeyImpl(bagit);

		List<BagItKeyImpl> delKeys = super.put(key,fileInfoList);
		
		for(BagItKeyImpl delKey : delKeys)
		{
				//clean up
				new File(folder,delKey + ".json").delete();
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.writerWithDefaultPrettyPrinter().writeValue(new File(folder,key + ".json"), fileInfoList);
		
	}

	@Override
	public void load() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		for(File file : folder.listFiles())
		{
			BagItKeyImpl key = BagItKeyImpl.parse(file.getName());
			List<FileInfo> fileInfo = mapper.readValue(file, new TypeReference<List<FileInfo>>(){});
			super.put(key,fileInfo);
		}
	}

	@Override
	public void save() throws IOException {		
		//NOP, nothing to do
	}

}
