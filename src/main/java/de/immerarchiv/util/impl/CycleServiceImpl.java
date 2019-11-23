package de.immerarchiv.util.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.util.interfaces.CycleService;
import de.immerarchiv.util.interfaces.TimestampService;

public class CycleServiceImpl implements CycleService {
	
	private final static Logger logger = LogManager.getLogger(CycleServiceImpl.class);

	private final TimestampService timestampService;
	private final long triggerCycleMinutes;
	private final long defaultCycleMinutes;
	
	private long nextCycle = 0;
	private String trigger = null;

	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	public CycleServiceImpl(TimestampService timestampService,long triggerCycleMinutes,long defaultCycleMinutes) {
		this.timestampService = timestampService;
		this.triggerCycleMinutes = triggerCycleMinutes;
		this.defaultCycleMinutes = defaultCycleMinutes;
	}

	@Override
	public long getNextCycle() {
		return nextCycle;
	}
	
	@Override
	public String getTrigger() {
		return trigger == null? "Cycle":trigger;
	}
	
	@Override
	public void incrCycle() {
		nextCycle = timestampService.getTime() + defaultCycleMinutes * 60 * 1000; //defaultCycleMinutes minutes	
		trigger = null;
		logger.trace("incrCycle set next to {} Uhr",sdf.format(new Date(nextCycle)));

	}

	@Override
	public void triggerCycle(String event) {
		
		logger.trace("triggerCycle {}",event);
		if(nextCycle == 0) return; //ignore
		if(this.trigger != null) return; //ignore
		long nextCycleCandidate = timestampService.getTime() + triggerCycleMinutes * 60 * 1000; //triggerCycleMinutes minutes	
		if(nextCycleCandidate > nextCycle) return;//ignore
			
		nextCycle = nextCycleCandidate;
		trigger = event;
		logger.trace("triggerCycle set next to {} Uhr",sdf.format(new Date(nextCycle)));

	}

	@Override
	public boolean IsNextCycle() {
		if(nextCycle  >= timestampService.getTime())
			return false;
		
		logger.trace("nextCycle trigger={}",trigger);
		return true;
	}



}
