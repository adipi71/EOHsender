package it.izs.utils;

public class Logger {

	public boolean debugActive=false;
	public boolean warningActive=true;
	public boolean logActive=true;
	
	private JSONArray errors=new JSONArray();
	private JSONArray warnings=new JSONArray();
	
	
	
	public String[] getErrors() {
		return errors.toStringArray();
	}

	public boolean hasErrors() {
		return errors.length()>0;
	}


	public String[] getWarnings() {
		return warnings.toStringArray();
	}
	public boolean hasWarnings() {
		return warnings.length()==0;
	}


	public boolean isDebugActive() {
		return debugActive;
	}


	public boolean isWarningActive() {
		return warningActive;
	}


	public boolean isLogActive() {
		return logActive;
	}


	public Logger() {
	}

	
	public void debug(String s) {
		if(debugActive) {
			System.out.println(s);
		}
	}
	
	public void log(String s) {
		if(logActive) {
			System.out.println(s);
		}
	}
	
	public void debug(Exception e) {
		debug( "ERROR MESSAGE" );
		debug( Str.getErrorMessage(e) );
		debug( "STACK TRACE" );
		debug( Str.getErrorStackTrace(e) );
	}
	
	public void error(Exception e) {
		error( Str.getErrorMessage(e) );
		debug(e);
	}
	
	public void error(String s, Exception e) {
		error( s + Str.getErrorMessage(e) );
		debug(e);
	}
	
	public void error(String s) {
		//if( s == null ) {	s="null string@Logger"; errors.put( Str.getErrorStackTrace(new Exception(s)));		}
		log("ERROR:" + s);
		errors.put(s);		
	}
	public void warning(String s) {
		if( s == null ) {			s="null string@Logger";		}
		log("WARNING:" + s);
		warnings.put(s);
	}
	public void info(String s) {
		if( s == null ) {			s="null string@Logger";		}
		log("INFO:" + s);
	}

	public void stopOnError() {
		if( hasErrors() ) {
			log("STOP FOR ERRORS. List of Errors:");
			for (String s : getErrors()) {
				log(s);
			}
			System.exit(1);
		}
	}
	
}
