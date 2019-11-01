package de.immerarchiv.util.impl;

import java.io.File;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import de.immerarchiv.job.model.Folder;
import de.immerarchiv.util.interfaces.NameService;

public class NameServiceImpl implements NameService {


	@Override
	public String generateTempName(String fileName) {
		return randomString(10)+"."+extension(fileName);
	}

	
	@Override
	public String createDescription(Folder folder) {
		File file = new File(folder.getPath());
		return file.getParentFile().getName()+"/"+file.getName();
	}
	
	@Override
	public String createSafeName(File file) {
		return file.getName().codePoints().mapToObj(this::filterAllowedCharacter).collect(Collectors.joining());
	}

	
	private String filterAllowedCharacter(int c)
	{
		
		
		switch(c)
		{
		case '.':
		case '_':
		case '-':
			break;
		default:
			if('a' <= c && c <= 'z') break;
			if('A' <= c && c <= 'Z') break;
			if('0' <= c && c <= '9') break;
			
			//TODO: Map with table
			//if(table.contains(c))
			//	return table.get(c);
			
			
			return "_";
		}
		
		
        //default			
		return new String(Character.toChars(c));
	}
	private static String AlphaNumericString = "abcdefghijklmnopqrstuvxyz"; 
	
	private String randomString(int n) {
		
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(n); 
  
        for (int i = 0; i < n; i++) { 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index 
                = (int)(AlphaNumericString.length() 
                        * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(AlphaNumericString 
                          .charAt(index)); 
        } 
  
        return sb.toString(); 
	}

	private String extension(String fileName) {
		
		String extension = "bin";

		int x = fileName.lastIndexOf('.');
		if (x > 0) {
		    extension = fileName.substring(x+1);
		}
		return extension;
	}


	
	

}
