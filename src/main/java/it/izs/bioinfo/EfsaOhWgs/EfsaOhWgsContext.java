package it.izs.bioinfo.EfsaOhWgs;

import it.izs.api.Bioinfoquery;
import it.izs.utils.JSONArray;
import it.izs.utils.JSONObject;
import it.izs.utils.Logger;

public class EfsaOhWgsContext {
   
    public JSONObject cfg;
    public Logger lg=new Logger();
    /**
     * JDBC query caller
     */
    public Bioinfoquery bioinfoquery=new Bioinfoquery();
    
    public JSONObject joSubmission=new JSONObject();
    public int libraryLayoutCode=0;
    
    public JSONArray jaListInputs=new JSONArray(); 
    public JSONObject joInput=new JSONObject();
    public String allelicProfileCrc32="";
    public JSONObject joAlCompareReport;
}
