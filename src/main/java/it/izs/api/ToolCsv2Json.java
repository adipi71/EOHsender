package it.izs.api;

import it.izs.api.general.Bioinfotool;
import it.izs.utils.JSONArray;
import it.izs.utils.JSONObject;
import it.izs.utils.Str;
import java.io.IOException;


public class ToolCsv2Json implements Bioinfotool{
	
	class MyContext {
	    String csvFile;
	}

	private MyContext ctx;
	
	/**
	 * Esegue il template unendo params e restituisce la stringa  
	 * @param args
	 * @throws Exception 
	 */
	public void run() throws Exception {
		if( ctx == null ) {throw new Exception("context not loaded. call init(JS cfg) first");}

		//csv content
		JSONArray ar=new JSONArray();
		ar.jsonArrayFromCSVString(Str.readFileString(ctx.csvFile), ",");
		String out = ar.toString();	
	}
	//----------------------------------------------
	
	
	/**
	 * verifica presenza e consistenza dei parametri
	 *
	 * - file del template mustache
	 * - file con json degli argomenti per il template mustache della query
	 * 
	 * @param cfg json contenente i parametri
	 */
	public void init(JSONObject cfg) throws Exception {
		ctx= new MyContext();
		cfg.setBeanProperties(ctx);

	}
}

