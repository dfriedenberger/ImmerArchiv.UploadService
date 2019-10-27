package de.immerarchiv.util.impl;

import de.immerarchiv.job.model.BagIt;

public class BagItKeyImpl extends KeyImpl {

	public BagItKeyImpl(BagIt bagit) {

		super(new String[]{bagit.getRepo(), bagit.getId()},
				new String[]{""+bagit.getFiles(), ""+bagit.getLastModified()});

	}

	private BagItKeyImpl(KeyImpl key) {
		super(key);
	}

	public static BagItKeyImpl parse(String filename) {
		String filenameWithOutExtension = filename.replaceFirst("[.][^.]+$", "");
		return new BagItKeyImpl(KeyImpl.parse(filenameWithOutExtension));
	}

}
