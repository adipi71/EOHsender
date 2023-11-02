package it.izs.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import it.izs.utils.JSONArray;
import it.izs.utils.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;


public class JS  {

	private boolean isArray=false;
	private JSONObject hash=new JSONObject();
	private JSONArray  array=new JSONArray();
	
	
	public JS() {
		hash=new JSONObject();
		isArray=false;
	}

	public JS(JSONObject jsonObject) {
		hash=jsonObject;
		isArray=false;
	}

	public JS(Object objectAsJsonObject) {
		hash=(JSONObject) objectAsJsonObject ;
		isArray=false;
	}

	public JS(String jsonFromString) {
		jsonFromString(jsonFromString);
	}

	public JS(JSONArray jsonArray) {
		array=jsonArray;
		isArray=true;
	}
	
	public static JS empty() {
		return new JS("{}");
	}

	public boolean isArray() {
		return isArray;
	}

	public JSONObject getHash() {
		return hash;
	}

	public JSONArray getArray() {
		return array;
	}

	public JSONObject getJOItem(int i) {
	    if(isArray()) {
	    	return (JSONObject)array.get(i);
	    }else {
	    	return null;
	    }
	}
	
	public String get(int i, String key) {
	    if(isArray()) {
	    	return array.getJSONObject(i).get(key).toString();
	    }else {
	    	return null;
	    }
	}
	
	//(JSONObject)records.getArray().get(0)

	/** 
	 * @return a new Map of the JsonObject content
	 */
	public Map<String, Object> toMap() {
	    Map<String, Object> map = new HashMap<String, Object>();
	    Iterator<String> keys = hash.keys();
	    while (keys.hasNext()) {
	        String key = (String) keys.next();
	        map.put(key, hash.get(key));
	    }
	    return map;
	}
	//------------------------------------------
	

	/**
	 * return as{key} as string
	 * @param key
	 * @return
	 */
	public static String getS(JSONObject as,String key) {
	    if(as.has(key)) {
	    	return as.get(key).toString();
	    }else {
	    	return "";
	    }
	}
	
	/**
	 * return hash{key} as string
	 * @param key
	 * @return
	 */
	public String getS(String key) {
	    if(hash.has(key)) {
	    	return hash.get(key).toString();
	    }else {
	    	//System.out.println("Key not found: " + key);
	    	return "";
	    }
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public JS put(String key, Object value) {
		hash.put(key, value);
		return this; 
	}
	
	/**
	 * copy a value of a key from another JS
	 * @param as 
	 * @param key
	 * @return
	 */
	public void copyKey(JS as,String key) {		
		hash.put(key, as.getS(key)); 
	}
	
	
	
	public static JSONObject getJO(JSONObject as,String key) {
	    if(as.has(key)) {
	    	return new JSONObject( as.getJSONObject(key));
	    }else {
	    	return new JSONObject(JSONObject.NULL);
	    }
	}
	
	public static Object getObject(JSONObject as,String key) {
	    if(as.has(key)) {
	    	return as.get(key);
	    }else {
	    	return JSONObject.NULL;
	    }
	}

	public void sum(JSONObject as, String key, int i) {

		int value= (as.has(key))? as.getInt(key) + i : i;
		as.put(key, value);
		
	}
	

	public boolean has(String key) {
	    if(this.isArray()) {return false;}	    
	    return this.hash.has(key);
	}
	
//	public JS get(String key) {
//		if(hash.has(key)) {
//			
//			if( !( hash.get(key) instanceof String) ) {
//				System.out.println("WARNING: called a toString on Object ");
//				System.out.println(
//						String.format("key=%s; value=%s; ", key, hash.get(key).toString()));
//			}
//			return hash.get(key).toString();
//		}else {
//			return "";
//		}
//	}
	

	public static JSONArray jsonArrayFromFile(String file) throws Exception {
		Str str=new Str();
		str.readFile(file);
		return new JSONArray(str.get());		
	}

	public static JSONObject jsonObjectFromFile(String fileJsonFormat) throws Exception {
		Str str=new Str();
		str.readFile(fileJsonFormat);
		return new JSONObject(str.get());		
	}
	/**
	 * see jsonObjectFromFile
	 * return a JS object 
	 * @param fileJsonFormat
	 * @return
	 * @throws Exception
	 */
	public static JS jsObjectFromFile(String fileJsonFormat) throws Exception {
		return new JS(jsonObjectFromFile(fileJsonFormat));
	}
	/**
	 * 
	 * @param fileXmlPropertyFormat XML Property file. Deve contenere 
	 *  <?xml version="1.0" encoding="UTF-8" standalone="no"?>
	 * 	<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
	 * le chiavi si impsotano
	 * <entry key="jdbc.driver">com.mysql.jdbc.Driver </entry>
	 * vedi esmpio nei commenti della funzione
	 * @return
	 * @throws Exception
	 */
	public static JSONObject jsonObjectFromXMLPropertyFile(String fileXmlPropertyFormat) throws Exception {
		FileInputStream fis = new FileInputStream(fileXmlPropertyFormat);
		Properties prop = new Properties();
		prop.loadFromXML(fis);
		return new JSONObject(prop);
		/* 
		 * ESEMPIO DI FILE XML NEL FORMATO CORRETTO 
			<?xml version="1.0" encoding="UTF-8" standalone="no"?>
			<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
			<properties>
			    <comment>Jdbc Connection Properties</comment>
			    <entry key="jdbc.driver">com.mysql.jdbc.Driver</entry>
			    <entry key="jdbc.url">jdbc:mysql://localhost:3306/technicalkeeda</entry>
			    <entry key="jdbc.username">root</entry>
			    <entry key="jdbc.password">password</entry>
			</properties>
		 */
	}
	
	/**
	 * see jsonObjectFromXMLPropertyFile
	 * return a JS object 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static JS jsObjectFromXMLPropertyFile(String file) throws Exception {
		return new JS(jsonObjectFromXMLPropertyFile(file));
	}
	
	/**
	 * 
	 * @param jsonString
	 * @throws Exception
	 */
	public void jsonFromString(String jsonString)  {
		if(jsonString.matches("^\\[.*$")) {
			array=new JSONArray(jsonString);
			isArray=true;					
		}else{
			hash=new JSONObject(jsonString);
			isArray=false;
		}		
	}

	/**
	 * return a string from combination of template in Mustache format and data contained in context 
	 * @param tmpl string containing the path of a mustache template
	 * @param context json object with data
	 * @return
	 * @throws Exception 
	 */  
	public static String strFromMustache(String tmpl, Map context)  {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(tmpl), "");
        
        StringWriter writer = new StringWriter();
    	mustache.execute(writer, context);
       
    	return writer.toString();		
	}
	
	public static Str strFromMustache(Str tmpl, JS context) {
       
    	return new Str( strFromMustache( tmpl.get(), context.toMap()) );		
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
	 * 
	 * @param csvContent
	 * @return json string from csv file conversion
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws Exception
	 */
	public static String jsonStringFromCSV(String csvFile) throws JsonProcessingException, IOException  {		
		File input = new File(csvFile);
		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();
		
		ObjectMapper mapper = new ObjectMapper();

		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll);
		//System.out.println(jsonString);
		return jsonString;
	}


	
	/**
	 * Modalità perl: 
	 * divide la stringa csvContent in record tramite newline e separa i campi con Separator. 
	 * Prende dalla prima riga le intestazioni e li usa come chiavi del json. 
	 * Se uppercase==true, imposta i nomi delle chiavi in uppercase
	 * 
	 * Gestione righe CSV con meno valori dei nomi colonna
	 * 
	 * @param csvContent
	 * @param Separator
	 * @param uppercase
	 * @return
	 * @throws Exception
	 */
	public static JSONArray jsonArrayFromCSVString(String csvContent, String Separator, boolean uppercase) throws Exception  {		

		String[] rows=csvContent.split("\r?\n");
		//String[] rows=csvContent.split("\r?\n");  TODO: valutare se tagliare invio a capo con \r?\n
		JSONArray result= new JSONArray();
		if(rows.length == 0) {
			return result;
		}
		
		String header=(uppercase)?
			rows[0].toUpperCase(): 					
			rows[0];
			
		String[] colNames=header.split(Separator);
		String[] cols=null;
		String row="";
		int r=0,i=0;
		try {
		for (r = 1; r < rows.length; r++) {
			row = rows[r];			
			JSONObject jo=new JSONObject();
			cols=row.split(Separator);
			for (i = 0; i < colNames.length; i++) {
				// Gestione righe CSV con meno valori dei nomi colonna
				jo.put( colNames[i], 
						(i< cols.length)? cols[i]: "");
			}
			result.put(jo);
		}
			
		} catch (Exception e) {
			System.out.println(
					Str.sf("ERROR: at r=%s; i=%s; \nrow=%s\n colNames.length=%d; cols.length=%d;",
							r,i,row,colNames.length,cols.length));
			throw e;
		}		
		
		return result;
	}

	
	/**
	 * json string 2 CSV string conversion
	 * @param jsonContent
	 * @return
	 * @throws Exception
	 */
	public static String csvStringFromJsonString(String jsonContent) throws Exception {		

		//JsonNode jsonTree = new ObjectMapper().readTree(new File(jsonFile));
		JsonNode jsonTree = new ObjectMapper().readTree(jsonContent);
		Builder csvSchemaBuilder = CsvSchema.builder();
		
		JsonNode firstObject = jsonTree.elements().next();
		firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);} );
		CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
		
		CsvMapper csvMapper = new CsvMapper();
		
		String csvString = 
					csvMapper.writerFor(JsonNode.class)
					  .with(csvSchema)
					  .writeValueAsString(jsonTree);
		  
		return csvString;
	}

	/**
	 * json string 2 CSV string conversion

	 * @param jsonContent
	 * @param headers headers of csv
	 * @return
	 * @throws Exception
	 */
	public static String csvStringFromJsonString(String jsonContent, JSONArray headers) throws Exception {		

		//JsonNode jsonTree = new ObjectMapper().readTree(new File(jsonFile));
		JsonNode jsonTree = new ObjectMapper().readTree(jsonContent);
		Builder csvSchemaBuilder = CsvSchema.builder();
		
		for (Object col: headers) {
			csvSchemaBuilder.addColumn(col.toString());
		}
		CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
		
		CsvMapper csvMapper = new CsvMapper();
		
		String csvString = 
					csvMapper.writerFor(JsonNode.class)
					  .with(csvSchema)
					  .writeValueAsString(jsonTree);
		  
		return csvString;
	}
	
	/*
	JsonNode jsonTree = new ObjectMapper().readTree(new File("src/main/resources/orderLines.json"));
	Next, let's create a CsvSchema. This determines the column headers, types, and sequence of columns in the CSV file. To do this, we create a CsvSchema Builder and set the column headers to match the JSON field names:

	1
	2
	3
	4
	Builder csvSchemaBuilder = CsvSchema.builder();
	JsonNode firstObject = jsonTree.elements().next();
	firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);} );
	CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
	Then, we create a CsvMapper with our CsvSchema, and finally, we write the jsonTree to our CSV file:

	1
	2
	3
	4
	CsvMapper csvMapper = new CsvMapper();
	csvMapper.writerFor(JsonNode.class)
	  .with(csvSchema)
	  .writeValue(new File("src/main/resources/orderLines.csv"), jsonTree);
	*/
	
	
	/**
	 * 
	 * @param csvFile csv Content
	 * @return JS object
	 * @throws Exception
	 */
	public static JSONArray jsFromCSV(String csvFile)  {
		
		try {
			//return new JSONArray(JS.jsonStringFromCSV  (csvFile));
			String csvContent=Str.readFileString(csvFile);
			return jsonArrayFromCSVString( csvContent, ",", false );

		} catch (Exception e) {
			System.out.println("ERROR: file " + csvFile);
			e.printStackTrace();
			return new JSONArray();
		}
	}

	

	public static JSONObject jsonArray2Object(JSONArray list, String id) {
		
		JSONObject out=new JSONObject();
		
		for (Object o : list) {
			JSONObject jo= (JSONObject)o;
			String key=jo.get(id).toString();
			out.put(key, jo);
		}
		return out;    	
	}

	
	public static ArrayList<Object> jsonArrayToArrayList(JSONArray myJSONArray) {
		ArrayList<Object> arrayList = new ArrayList<Object>(myJSONArray.length());
		for(int i=0;i < myJSONArray.length();i++){
		    arrayList.add(myJSONArray.get(i));
		}
		return arrayList;
	}

	
	public static JS jsFromQuery(ResultSet resultSet) throws Exception {
		return new JS(jsonArrayFromJDBC(resultSet));
	}
	

	/**
	 * execute the query and  Convert a result set into a JSON Array
	 * 
	 * @param jdbcUtils. Object with Oralce JDBC connection
	 * @param query string to be executed
	 * @return
	 * @throws Exception
	 */
	public static JSONArray jsonArrayFromJDBC(JdbcUtils jdbcUtils, String query) throws Exception {
		return jsonArrayFromJDBC(jdbcUtils.executeQuery(query));
	}


	/**
     * Convert a result set into a JSON Array
     * @param resultSet
     * @return a JSONArray
     * @throws Exception
     */
	public static JSONArray jsonArrayFromJDBC(ResultSet resultSet) throws Exception {
		JSONArray jsonArray = new JSONArray();
		while (resultSet.next()) {
			JSONObject obj = new JSONObject();
			int total_rows = resultSet.getMetaData().getColumnCount();
			for (int i = 0; i < total_rows; i++) {
				obj.put(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));
			}
			jsonArray.put(obj);
		}
		return jsonArray;
	}

	/**
	 * Convert a result set into a XML List
	 * 
	 * @param resultSet
	 * @return a XML String with list elements
	 * @throws Exception
	 *             if something happens
	 */
	public static String convertResultSetToXML(ResultSet resultSet) throws Exception {
		StringBuffer xmlArray = new StringBuffer("<results>");
		while (resultSet.next()) {
			int total_rows = resultSet.getMetaData().getColumnCount();
			xmlArray.append("<result ");
			for (int i = 0; i < total_rows; i++) {
				xmlArray.append(" " + resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase() + "='"
						+ resultSet.getObject(i + 1) + "'");
			}
			xmlArray.append(" />");
		}
		xmlArray.append("</results>");
		return xmlArray.toString();
	}	
	
	/**
	 * 
	 * @param o the bean to populate with hash (string) values
	 * 
	 * @return false if one field has not been filled. 
	 * @throws Exception
	 */
	public boolean setBeanProperties(Object o) throws Exception {
		Field[] fields = o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {

			Field f= fields[i];
			if( this.has(f.getName())) {
	            f.setAccessible(true);
				f.set(o, this.getS(f.getName()));
			}else {
				System.out.println("WARNING. no fieldName in JSON:" + f.getName());
				//return false;
			}			
		}
		return true;
	}
	
	
    /**
     * serialize 
     * @param indentFactor
     *            The number of spaces to add to each level of indentation. 
     * @return
     * @throws Exception
     */
	public String serialize() throws Exception {
		return serialize(0);
	}
	
    /**
     * 
     * @param indentFactor
     *            The number of spaces to add to each level of indentation. * @return
     * @throws Exception
     */
	public String serialize(int indentFactor) throws Exception {
		if( isArray ) {
			return array.toString(indentFactor);
		}else {
			return hash.toString(indentFactor);
		}
	}
	
	
}
