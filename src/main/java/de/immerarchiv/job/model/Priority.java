package de.immerarchiv.job.model;

public enum Priority {
	
	
	
	FolderScan(100),
	RepositoryScan(100),
	FileScan(200), 
	BagItScan(200),
	Syncronize( 300 ), 
	CreateBagIt(400),
	Upload(500);
	 
	 
	 
	 
	

	final private int value;
	 Priority( int value ) { 
		 this.value = value; 
		}

	public int getValue() {
		return value;
	}
}
