package de.immerarchiv.util.impl;

import java.util.Date;

import de.immerarchiv.util.interfaces.TimestampService;

public class TimestampServiceImpl implements TimestampService {

	@Override
	public long getTime() {
		return new Date().getTime();
	}

}
