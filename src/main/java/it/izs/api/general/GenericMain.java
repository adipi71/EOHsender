package it.izs.api.general;

import it.izs.utils.JSONObject;

public class GenericMain {
	/**
	 * oggetto json letto dal file
	 * usato per popolare l'oggetto Context ctx dello script
	 */
	static JSONObject cfg=new JSONObject();
	
	/**
	 * Accetta il file di configurazione json. 
	 * carica il file json. crea la classe dello script    
	 * @param args
	 * @throws Exception 
	 */
	public static void start (Bioinfotool tool, String[] args) throws Exception {
		
		if( args.length < 1 ) {
			System.out.println("Non ci sono parametri sufficienti.\n manca il file di configurazione json");
			return;
		}
		if( tool == null ) {
			System.out.println("Set script using setBioinfotool before start");
			return;
		}
		
		cfg=JSONObject.jsonObjectFromFile(args[0]);		
		tool.init(cfg);
		tool.run();
	}//----------------------------------------------

	
}



