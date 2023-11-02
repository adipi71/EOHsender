package it.izs.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class Str {

	String value = "";
	Matcher matcher;
	static public boolean debugActive=false;
	static public boolean logActive=true;
	
	public Str() {
		this.value = "";
	}

	public Str(String original) {
		this.value = original;
	}

	public Str(Object o) {
		set(o);
	}

	
	public static Str s (Object o) {
		return new Str(o);
	}


	public int length() {
		return value.length();
	}

	public boolean isEmpty() {
		if (value == null) {
			return true;
		}
		return value.isEmpty();
	}
	
	public static boolean isEmpty(String input) {
		return new Str(input).isEmpty();
	}
	
	public static boolean isInteger(String input) {
		try {
			int a = Integer.parseInt(input);
			return true;			
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public boolean isNumber(String pid) {
		try {
			int a = Integer.parseInt(pid);	
			int b=a+1;

			Date d = new Date(pid);// =new
			Date.parse(pid);
			
			return b>a;
			
			//DateFormat d;
			//d = new java.util.Date()
		} catch (Exception e) {
			return false;
		}
	}

	public String get() {
		return value;
	}

	public void set(Object o) {
		if (o != null) {
			value = o.toString();
		} else {
			value = "";
		}
	}
	//---------------------

	/**
	 * link to String.format
	 *  
	 * @param format
	 * @param args
	 */
	public static String sf(String format, Object... args) {
		return String.format(format, args);
	}
	
	
	/**
	 * regexp con groups
	 * esempio: 
	 * bash/perl: if( $stringa =~ /reg(ex)p/){         $val=$1; }
	 * java     : if( Str.matches("reg(ex)p")){ String val=Str.$i(1)}
	 * @param regex
	 * @return
	 */
	public boolean matches(String regex) {
		
		//System.out.println( value + "-----regex_originale" + regex );
		Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        matcher = p.matcher(value);
		//System.out.println( "group:" + matcher.groupCount() );
        return matcher.matches();
	}
	
	public String $i(int i) {
		String s=matcher.group(i);
		//System.out.println( "group:" + i +":"+ s );
		return matcher.group(i);
	}
	
	//---------------------
	

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
	
	/**
	 * 
	 * @param tmpl
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String strFromMustache(String tmpl, JSONObject context)  {	       
    	return strFromMustache( tmpl, context.toMap());		
	}
	
	/**
	 * 
	 * @param tmpl
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String strFromMustacheEscaped(String tmpl, JSONObject context)  {	       
		Map<String, Object> input = context.toMap();
		for (String key: input.keySet()) {
			Object value = input.get(key);
			if (value == null) {
				continue;
			}
			input.put(key, value.toString().replaceAll("'", "''"));
		}
    	return strFromMustache( tmpl, input);		
	}
	
	/**
	 * returns the full path of a resource file. This is needed to open and read a file.
	 * @param resourceFileName  name of the file in resource folder
	 * @return full path in the filesystem
	 * @throws IOException
	 */
	public static String fullPath(String resourceFileName) throws IOException {
		URL url = Base.class.getClassLoader().getResource(resourceFileName);
		
		if(url == null) 
			return null;
		else
			return url.getPath();
	}


	public static boolean fileExists(String path)  {
		File f = new File(path);
		return f.exists();
	}
	public static boolean fileExists(String dir,String filename)  {
		return fileExists(dir +"/"+filename);
	}


	public static String getFileExtension(String path)  {
		Str f=new Str(path);

		if( f.matches("^.*\\.([^\\.]+)$") ){
			return f.$i(0);
		}
		return "";
	}

	public Str readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		value = new String(encoded);
		return this;
	}

	public static String readFileString(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}
	
	public static String[] readFileStringArray(String path) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		//List<String> a = Files.readAllLines(Paths.get(path));
		//String[] aa= (String[]) a.toArray();
		return new String(encoded).split("\r?\n");
	}

	/**
	 * legge il l'input stream e restituisce tutte le righe.
	 * Esempio:  
	 * 
	 * InputStream in = MainImportDB.class.getResourceAsStream("/importdb/cfg.json");
	 * String s = readStreamString(in)
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String readStreamString(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String row, content="";
		while( (row = reader.readLine()) != null ) {
			content += row;
		}
		return content;
	}

	/**
	 * ritorna un BufferedReader reader in maniera da essere usare un loop cosi':
	 * 		while( (row = reader.readLine()) != null ) { content += row;	}
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static BufferedReader readerFromFile(String path) throws IOException {
		InputStream in = new FileInputStream( new File(path) );
		return new BufferedReader(new InputStreamReader(in));
	}



	/**
	 * overwrite an existing file or write to a new file 
	 * @param path
	 * @param out
	 * @throws IOException
	 */
	public static void writeToFile(String path, String out) throws IOException {
		if( fileExists(path) ) {
			writeToFileOption(path, out, StandardOpenOption.TRUNCATE_EXISTING);
		}else {
			writeToFileOption(path, out, StandardOpenOption.CREATE);		
		}
	}
	
	/**
	 * append to an existing file or write to a new file 
	 * @param path
	 * @param out
	 * @throws IOException
	 */
	public static void appendToFile(String path, String out) throws IOException {
		if( fileExists(path) ) {
			writeToFileOption(path, out, StandardOpenOption.APPEND);
		}else {
			writeToFileOption(path, out, StandardOpenOption.CREATE);		
		}
	}

	/**
	 * 
	 * @param path
	 * @param out
	 * @param option 
	 * @throws IOException
	 */
	public static void writeToFileOption(String path, String out, StandardOpenOption option) throws IOException {
		Files.write(Paths.get(path), out.getBytes(), option);
	}

	public void writeToFile(String path) throws IOException {
		Files.write(Paths.get(path), value.getBytes(), StandardOpenOption.WRITE);
	}

	
	public void loadFromHttpGet(String urlPath) throws Exception {		
		HttpResponse<String> response = Unirest.get(urlPath)
				  .header("Cache-Control", "no-cache")
				  .asString();
		
		value =  response.getBody();
	}

	/**
	 * read the file and store the base64 encoded value (Str.get() is the encoded value);
	 * @param path
	 * @throws IOException
	 */
	public void encodeBase64(String path) throws IOException {
		readFile(path);
		value=Str.encodeBase64(value.getBytes());
	}
	
	/**
	 * static method for encodeBase64 of a byte[] or String.getBytes()
	 * returns the base64 encoded string
	 */
	public static String encodeBase64(byte[] content)  {

		//byte[] content = FileUtils.readFileToByteArray(new File("/tmp/file.csv"));
		return Base64.getEncoder().encodeToString(content);		
	}
	
	/**
	 * trasforma s in 's' afcendo escape di apici in s (raddoppiandoli) 
	 * @param s
	 * @return
	 */
	public static String apice(String s) {
		s = "'" + s.replaceAll("'", "''") + "'";
		//ctx.lg.log(s);
		return s;
	}
	
		
	public static String getErrorMessage(Exception e) {
		if( e == null) {return "";}
		return "Exception:" + e.getClass() + "message:"+e.getMessage() + getErrorStackTrace(e);		
	}

	public static String getErrorStackTrace(Exception e) {
		if( e == null) {return "";}
		String out="";
		for(StackTraceElement a : e.getStackTrace()) {
			out += "ERROR stacktrace: " + a.toString() + "\n";
		}
		return out;		
	}

	public static String getFileNameFromPath(String f) {
		return new File(f).getName();
	}
	
	public static String getDirNameFromPath(String f) {
		return new File(f).getParent();
	}


	/**
	 * timestamp in formato YYYYMMdd_HHmmss.
	 * Esempio 20201225_110059
	 * @return
	 */
	public static String timestampStr() {
		long currentDateTime = System.currentTimeMillis();
		Date currentDate = new Date(currentDateTime);
		DateFormat df = new SimpleDateFormat("YYYYMMdd_HHmmss");
		String dataFormattata = df.format(currentDate);
		return dataFormattata;
	}


	

	/**
	 * true se è una directory
	 * @param ln
	 * @return
	 */
	public static boolean isDirectory(String ln) {
		File f = new File (ln);
		return f.isDirectory();
	}
	

	/**
	 * true se esiste, ed è un symlink non corrotto ad un file esistente
	 * @param ln
	 * @return
	 */
	public static boolean isSymLink2File(String ln) {
		if(! isSymLink(ln))  {return false;}
		File f = new File (ln);
		return f.isFile();
	}
	
	/**
	 * true se esiste, ed è un symlink non corrotto ad un file esistente
	 * @param ln
	 * @return
	 */
	public static boolean isSymLink2Dir(String ln) {
		if(! isSymLink(ln))  {return false;}
		File f = new File (ln);		
		return f.isDirectory();
	}

	/**
	 * true se esiste ed è un symlink
	 * @param ln
	 * @return
	 */
	public static boolean isSymLink(String ln) {
		if(! fileExists(ln)) {return false;}
		
		File f = new File (ln);
		return Files.isSymbolicLink( f.toPath());
	}
	

}
