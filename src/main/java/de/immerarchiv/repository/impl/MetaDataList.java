package de.immerarchiv.repository.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.immerarchiv.repository.model.MetaData;
import de.immerarchiv.repository.model.MetaDataKeys;


public class MetaDataList implements MetaDataKeys {

	
	final static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
	final static TimeZone utc = TimeZone.getTimeZone("UTC");
	public final static SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);

	static
	{
		sdf.setTimeZone(utc);
	}

	
	private List<MetaData> metadata = new ArrayList<MetaData>();

	public MetaDataList() {	}

	public MetaDataList(MetaData[] metadata) {
		for(MetaData md : metadata)
			this.metadata.add(md);
	}
	

	//overwrite
	public void set(String key, String value) {
		
		for(MetaData md : metadata)
		{
			if(!md.key.equals(key)) continue;
			md.value = value;
			return;
		}
		add(key,value);
	}
	
	public void add(String key, String value) {
		MetaData md = new MetaData();
		md.key = key;
		md.value = value;
		metadata.add(md);
	}
	
	public String get(String key) {
		for(MetaData md : metadata)
		{
			if(!md.key.equals(key)) continue;
			return md.value;
		}
		return null;
	}
	
	private boolean exists(String key, String value) {
		for(MetaData md : metadata)
		{
			if(!md.key.equals(key)) continue;
			if(!md.value.equals(value)) continue;
			return true;
		}
		return false;
	}
	
	public List<MetaData> getList() {
		return metadata;
	}
	
	//helper
	
	public void add(String key, int i) {
		add(key,""+i);
	}
	
	public void set(String key, long l) {
		add(key,""+l);
	}
	
	public void set(String key, boolean b) {
		add(key,""+b);		
	}
	
	public void setMimeType(String mimetype) {
		set(mdFileMIMEType,mimetype);
	}

	public void addGeoLocation(double latitude, double longitude) {
		add(mdImageGeoLocationLatitude,""+latitude);
		add(mdImageGeoLocationLongitude,""+longitude);
	}
	
	public void setOriginalDate(Date date) {
		set(mdImageDateOriginal,sdf.format(date));
	}
	
	public void addCreator(String creator) {
		if(exists(mdDocumentCreator,creator))
			return;
		add(mdDocumentCreator,creator);
	}
	
	public void setFileExtension(String fileExtension) {
		set(mdFileExtension,fileExtension);
	}
	
	public MetaData[] toArray() {
		return getList().toArray(new MetaData[0]);
	}

	public Date getDate(String key) throws ParseException {
		String date = get(key);
		return sdf.parse(date);
	}

	public long getLong(String key) {
		String value = get(key);		
		return Long.parseLong(value);
	}

	public void set(String key, Date date) {
		set(key,sdf.format(date));
	}

	@Override
	public String toString() {
		return metadata.toString();
	}
	
	
	
	
	

}
