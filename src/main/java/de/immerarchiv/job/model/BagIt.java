package de.immerarchiv.job.model;

public class BagIt {
	
	
	

	private String repo;
	private String id;
	private long files;
	private long lastModified;
	public String getRepo() {
		return repo;
	}
	public void setRepo(String repo) {
		this.repo = repo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getFiles() {
		return files;
	}
	public void setFiles(long files) {
		this.files = files;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((repo == null) ? 0 : repo.hashCode());
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
		BagIt other = (BagIt) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (repo == null) {
			if (other.repo != null)
				return false;
		} else if (!repo.equals(other.repo))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "BagIt [repo=" + repo + ", id=" + id + ", files=" + files + "]";
	}
	
}
