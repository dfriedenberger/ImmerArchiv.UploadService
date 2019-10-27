package de.immerarchiv.job.interfaces;

import java.util.List;




public interface Job {

	void init() throws Exception;
	
	boolean next() throws Exception;

	void finish() throws Exception;

	int priority();

	List<Job> getNext();

}
