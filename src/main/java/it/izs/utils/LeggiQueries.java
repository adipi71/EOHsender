package it.izs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class LeggiQueries {
	
	private static Map<String, String> queries;
	private static final String filenameQueries = "queries.properties";
	
	public static Map<String, String> leggiQueries(){
		if(queries == null) {
			try {
				String pathQueries = Base.getFullPathFromResourceName(filenameQueries);
				queries = leggiQueriesFromFile(pathQueries); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return queries;
	}

	private static Map<String, String> leggiQueriesFromFile(String pathFile) throws IOException {
		File file = null;
		try {
			file = new File(pathFile);
			InputStreamReader in = new InputStreamReader(new FileInputStream(file)); 
			return leggiQueries(in);
		} catch (Exception e) {
			InputStream is = Base.class.getResourceAsStream("/resources/"+filenameQueries);
			return leggiQueries(is);
		}
		
	}
	
	@SuppressWarnings("serial")
	private static Map<String, String> leggiQueries(InputStreamReader in) throws IOException{
	    Map<String, String> mp = new LinkedHashMap<>();
	    (new Properties(){
	        public synchronized String put(Object key, Object value) {
	            return mp.put(key.toString(), value.toString());
	        }
	    }).load(in);
	    return mp;
	}
	
	@SuppressWarnings("serial")
	private static Map<String, String> leggiQueries(InputStream in) throws IOException{
	    Map<String, String> mp = new LinkedHashMap<>();
	    (new Properties(){
	        public synchronized String put(Object key, Object value) {
	            return mp.put(key.toString(), value.toString());
	        }
	    }).load(in);
	    return mp;
	}

}
