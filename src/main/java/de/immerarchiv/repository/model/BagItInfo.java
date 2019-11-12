package de.immerarchiv.repository.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BagItInfo {
	private String description = null;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Map<String, String>> toArray() {
		
		List<Map<String,String>> info = new ArrayList<>();
		
		if(description != null)
		{
			Map<String,String> val = new HashMap<>();
			val.put("Description",description);
			info.add(val);
		}
		
		return info;
	}
}
