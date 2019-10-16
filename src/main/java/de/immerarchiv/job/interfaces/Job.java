package de.immerarchiv.job.interfaces;




public interface Job {

	void init() throws Exception;
	
	boolean next() throws Exception;

	void finish() throws Exception;

	<T> T getResult(Class<T> clazz);

}
