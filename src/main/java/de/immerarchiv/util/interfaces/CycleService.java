package de.immerarchiv.util.interfaces;

public interface CycleService {

	void incrCycle();

	void triggerCycle(String string);

	boolean IsNextCycle();

	long getNextCycle();

	String getTrigger();
	
}
