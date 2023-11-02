package it.izs.api.general;

import it.izs.utils.JSONObject;

public interface Bioinfotool {

	public void init( JSONObject cfg ) throws Exception;
	public void run() throws Exception;	

}
