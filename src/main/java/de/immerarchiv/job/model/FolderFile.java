package de.immerarchiv.job.model;

import java.io.File;
import java.util.regex.Pattern;

public class FolderFile {

	private String md5;
	private long length;
	
	private File file;
	private String safeName;


	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getSafeName() {
		return safeName;
	}
	public void setSafeName(String safeName) {
		
		if(!Pattern.matches("^[a-zA-Z0-9\\-_\\.]+$", safeName))
			throw new IllegalArgumentException(safeName);
		this.safeName = safeName;
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}

	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
		result = prime * result + ((safeName == null) ? 0 : safeName.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FolderFile other = (FolderFile) obj;
		if (md5 == null) {
			if (other.md5 != null)
				return false;
		} else if (!md5.equals(other.md5))
			return false;
		if (safeName == null) {
			if (other.safeName != null)
				return false;
		} else if (!safeName.equals(other.safeName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return safeName + " md5=" + md5;
	}
	
}
