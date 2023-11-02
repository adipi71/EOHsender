package it.izs.api;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Date;

import it.izs.utils.JSONObject;

import it.izs.api.general.Bioinfotool;
import it.izs.utils.JSONArray;
import it.izs.utils.JdbcUtils;
import it.izs.utils.Logger;
import it.izs.utils.Str;

public class Bioinfoquery implements Bioinfotool{
	
	private Context ctx;
	private boolean PRINT_SQL_ONLY=false;
	
	private JdbcUtils DB = null;
	private Str query=new Str();
	private JSONObject params;
	private Logger lg;
	
	/**
	 * connette al db, esegue la query e stampa i risultati in formato JSON  
	 * @param args
	 * @throws Exception 
	 */
	public void run() throws Exception {
		if( ctx == null ) {throw new Exception("context not loaded. call init(JSONArray cfg) first");}
		
		creaQueryDaParam();
		creaConnessioneAlDatabase();		
		printJsonFromQuery();
	}//----------------------------------------------

	/**
	 * execute a new query, combine _query and _params and return the JSON result of the resultset
	 * @param _query new query string (template mustache) to launch
	 * @param _params new parameters
	 * 
	 * @throws Exception
	 */
	public JSONArray run( Str _query, JSONObject _params) throws Exception {
		
		if( DB == null ) {
			if( ! PRINT_SQL_ONLY ) {
				throw new Exception("JDBC connection not initialized");
			}
		}
		query=_query;
		params=_params;
		
		creaQueryDaParam();
		return JsFromQuery();
	}
	
	public JSONArray run( String _query, JSONObject _params) throws Exception {		
		return run (new Str(_query), _params);
	}
	
	public JSONArray run( String _query ) throws Exception {		
		return run (new Str(_query), JSONObject .empty());
	}//----------------------------------------------

	/**
	 * execute an update/insert/delete contained in "_query" string. combine _query and _params with mustache and execute 
	 * @param _query new statement string (template mustache) to launch
	 * @param _params new parameters
	 * 
	 * @throws Exception
	 */
	public int runStatement( Str _query, JSONObject _params)  {
		
		if( DB == null ) {
			if( ! PRINT_SQL_ONLY ) {
				lg.error( new Exception("JDBC connection not initialized") );
			}			
		}
		query=_query;
		params=_params;
		
		creaQueryDaParam();
		return executeUpdate();
	}

	public int runStatement( String _query, JSONObject _params)  {
		return runStatement(new Str(_query), _params);
	}

	public int runStatement( String _query) {
		return runStatement(new Str(_query),JSONObject.empty());
	}
	//----------------------------------------------
	
	public boolean runStored( String statement) {
		return executeStored(statement);
	}
	//----------------------------------------------
	


	private void creaQueryDaParam()  {		
		String q=params.qq(query.get());
		lg.log( q + ";");
	}
	//----------------------------------------------
	
	
	/**
	 * connessione al database usando i dati da jsonCFG
	 */
	private void creaConnessioneAlDatabase() {
		try {
			DB = new JdbcUtils(  
                    ctx.jdbc_driver_class,
                    ctx.jdbc_url,
                    ctx.jdbc_user,
                    ctx.jdbc_password, lg);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}


	
	/**
	 * ritorna i risultati della query in formato JSON
	 * @param query
	 * @throws Exception 
	 */
	private JSONArray JsFromQuery() throws Exception {		
		ResultSet rs=eseguiQuery();
		return JSONArray.jsFromQuery (rs);		
	}

	
	/**
	 * stampa i risultati della query in formato JSON
	 * @param query
	 * @throws Exception 
	 */
	private void printJsonFromQuery() throws Exception {		
		JSONArray out=JsFromQuery();
		System.out.println(out.toString(3));		
	}

	/**
	 * esegue la query passata come parametro
	 * @param query
	 */
	private ResultSet eseguiQuery() {	
		if( PRINT_SQL_ONLY ) { return null;}

		return DB.executeQuery(query.get());
	}//----------------------------------------------
	
	/**
	 * esegue l'istruzione di modifica presente nell'attributo query della classe
	 */
	private int executeUpdate() {
		if( PRINT_SQL_ONLY ) { return 0;}

		long pre=new Date().getTime();

		int numRecordAffected=DB.executeUpdate(query.get());
		
		long durata=new Date().getTime() - pre;
		lg.log( numRecordAffected + " record affected in " + durata + "ms");
		return numRecordAffected;
	}//----------------------------------------------
	
	private  boolean executeStored( String statement) {
		if( PRINT_SQL_ONLY ) { return false;}		
		lg.log( statement );
		if( DB.executeStored(statement) ) {
			return true;
		}else {
			lg.log( "stored return false");
			return false;			
		}
	}//----------------------------------------------
	
	
	/**
	 * verifica presenza e consistenza dei parametri
	 *
	 * - parametri di connessione al DB
	 * - file con stringa della query. un template mustache
	 * - file con json degli argomenti per il template mustache della query
	 * 
	 * @param cfg json contenente i parametri
	 */
	public void init(JSONObject cfg) throws Exception {
		ctx= new Context();
		cfg.setBeanProperties(ctx);

		//query
		query.readFile(ctx.queryPath);// = //Str. new Str(ctx.queryPath);
		

		//params
		try {
			params=JSONObject.jsonObjectFromFile(ctx.paramQueryJsonPath);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("ERRORE: nella lettura del file 'queryPath':" + ctx.queryPath);
			System.out.println("STOP");
			e1.printStackTrace();
			return;
		}
	}

	/**
	 * verifica presenza e consistenza dei parametri
	 *
	 * - parametri di connessione al DB
	 * - file con stringa della query. un template mustache
	 * - file con json degli argomenti per il template mustache della query
	 * 
	 * @param cfg json contenente i parametri
	 */
	public void initJDBC(JSONObject cfg, Logger _lg) throws Exception {
		if( _lg == null) {
			throw new Exception("initJDBC: Logger cannot be null");
		}
		ctx= new Context();
		lg=_lg;
		cfg.setBeanProperties(ctx);
		if( cfg.getS("PRINT_SQL_ONLY").contains("yes") ) {
			PRINT_SQL_ONLY=true;
			return;
		}
		creaConnessioneAlDatabase();
	}
}



class Context {
	String jdbc_driver_class;
    String jdbc_url;
    String jdbc_user;
    String jdbc_password;
    /**
     * path del file contenente la query
     */
    String queryPath;
    
    String paramQueryJsonPath;
    
	JSONObject cfg;

}

