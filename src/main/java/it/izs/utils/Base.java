package it.izs.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import it.izs.utils.JSONArray;
import org.json.JSONException;
import it.izs.utils.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;


public class Base {


	/**
	 * readFile
	 * 
	 * Return the full content of a file as a String
	 * 
	 * - Find the fullpath of the resource file name
	 * - Read all file content
	 * - Convert bytes to String
	 *  
	 * @param resourceFileName name of the file in resource folder
	 * @return content of a file as a String
	 * @throws IOException
	 */
	public static String readFile(String resourceFileName, Boolean fileEsterno) throws IOException {
		String path = "";
		if(fileEsterno) {
			path = resourceFileName;
		} else {
			//E' un file interno al progetto contenuto nella cartella "resources"
			path = getFullPathFromResourceName(resourceFileName);
		}
		if(path != null) {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded);
		} else {
			return null;
		}
	}

	
	/**
	 * jsonObjectFromFile
	 * 
	 * Read a file and returns a JSONObject
	 * @param resourceFileName
	 * @return
	 * @throws Exception
	 */
	public static JSONObject jsonObjectFromFile(String resourceFileName, Boolean fileEsterno) throws Exception {	
		JSONObject json = null;
		String content = readFile(resourceFileName, fileEsterno);
		if(content != null) {
			json = new JSONObject(content);		
		} else {
			json = readFileInJar(resourceFileName);
		}
		return json;
	}


	private static JSONObject readFileInJar(String resourceFileName) throws IOException {
		JSONObject json;
		InputStream is = Base.class.getResourceAsStream("/resources/"+resourceFileName);
		json = new JSONObject(IOUtils.toString(is, "utf-8"));
		return json;
	}
	
	/**
	 * jsonArrayFromFile
	 * 
	 * Read a file and returns a JSONArray
	 * 
	 * @param resourceFileName
	 * @return
	 * @throws Exception
	 */
	public static JSONArray jsonArrayFromFile(String resourceFileName) throws Exception {		
		return new JSONArray(readFile(resourceFileName, false));		
	}

	/**
	 * stringFromHttpGet
	 */
	public static String stringFromHttpGet(String urlPath) throws Exception {		
		HttpResponse<String> response = Unirest.get(urlPath)
				  .header("Cache-Control", "no-cache")
				  .asString();
		
		return response.getBody();
	}

	/**
	 * jsonArrayFromHttpGet
	 */
	public static JSONArray jsonArrayFromHttpGet(String urlPath) throws Exception {		
		return new JSONArray(stringFromHttpGet(urlPath));
	}

	/**
	 * encodeURL
	 */
	public static String encodeURL(String queryString) throws Exception {		
		return URLEncoder.encode(queryString);
	}

			
	
	/**
	 * returns the full path of a resource file. This is needed to open and read a file.
	 * @param resourceFileName  name of the file in resource folder
	 * @return full path in the filesystem
	 * @throws IOException
	 */
	public static String getFullPathFromResourceName(String resourceFileName) throws IOException {
		URL url = Base.class.getClassLoader().getResource(resourceFileName);
		
		if(url == null) 
			return null;
		else
			return url.getPath();
	}
	
	
	public static Map<String, String> toMap(JSONObject object) throws JSONException {
	    Map<String, String> map = new HashMap<String, String>();

	    Iterator<String> keysItr = object.keys();
	    while(keysItr.hasNext()) {
	        String key = keysItr.next();
	        Object value = object.get(key);

	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        map.put(key, value.toString());
	    }
	    return map;
	}
	
	private static List<Object> toList(JSONArray array) throws JSONException {
	    List<Object> list = new ArrayList<Object>();
	    for(int i = 0; i < array.length(); i++) {
	        Object value = array.get(i);
	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        list.add(value);
	    }
	    return list;
	}

	public static void copyToFile(InputStream inputStream, File file) throws IOException {
	    try(OutputStream outputStream = new FileOutputStream(file)) {
	        IOUtils.copy(inputStream, outputStream);
	    }
	}
}
