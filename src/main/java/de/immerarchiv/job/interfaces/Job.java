package de.immerarchiv.job.interfaces;

import java.util.List;

import de.immerarchiv.job.model.Priority;




public interface Job {

	void init() throws Exception;
	
	boolean next() throws Exception;


	Priority priority();

	List<Job> getNext();

}
