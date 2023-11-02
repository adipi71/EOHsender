package it.izs.api;

import java.io.IOException;
import it.izs.api.general.Bioinfotool;
import it.izs.utils.JSONObject;
import it.izs.utils.Str;

public class TmplMustache implements Bioinfotool{
	
	private ContextMustache ctx;
	
	private Str template=new Str();
	private JSONObject params;
		
	/**
	 * Esegue il template unendo params e restituisce la stringa  
	 * @param args
	 * @throws Exception 
	 */
	public void run() throws Exception {
		if( ctx == null ) {throw new Exception("context not loaded. call init(JS cfg) first");}
		String out=params.qq(template.get());
		System.out.println(out);		
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
		ctx= new ContextMustache();
		cfg.setBeanProperties(ctx);

		//query
		template.readFile(ctx.templatePath);
		

		//params
		try {
			params=JSONObject.jsonObjectFromFile(ctx.paramJsonPath);

		} catch (IOException e1) {
			System.out.println("ERRORE: nella lettura del file 'params':" + ctx.paramJsonPath);
			e1.printStackTrace();
			return;
		}
	}
}

class ContextMustache {
    /**
     * path del file contenente la query
     */
    String templatePath;    
    String paramJsonPath;
}

