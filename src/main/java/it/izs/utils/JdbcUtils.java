package it.izs.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class JdbcUtils {
	
	private Connection connection = null;
	private Exception lastException = null;
	private Logger lg;
	
	private static final String JDBC_ORACLE = "oracle.jdbc.driver.OracleDriver";

	/**
	 * Crea una connessione JDBC
	 * @param JDBC_DRIVER_CLASS
	 * @param url
	 * @param user
	 * @param password
	 * @throws Exception 
	 */
	public JdbcUtils(String JDBC_DRIVER_CLASS, String url, String user, String password, Logger _lg) throws Exception {
		super(); 
		if( _lg == null) {
			throw new Exception("JdbcUtils: Logger cannot be null");
		}
		lg= _lg;

		cleanError();

		try {
			Class.forName(JDBC_DRIVER_CLASS);
		} catch (ClassNotFoundException e) {
			manageError(e,"Driver Oracle JDBC non trovato");
			return;
		}

		try {
			connection = DriverManager. getConnection(url, user, password);

		} catch (SQLException e) {			
			manageError(e,"Connessione fallita! ");
			return;
		}
	}

	/**
	 * 
	 */
	public void chiudiConnessione() {
		cleanError();
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				manageError(e,"Problemi nel chiudere la Connessione ");
				return;
			}
		}
	}

	/**
	 * esegue una query.  
	 * @param query
	 * @return
	 */
	public ResultSet executeQuery(String query) {
		cleanError();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			return preparedStatement.executeQuery();

		} catch (SQLException e) {
			manageError(e,"ERROR executeQuery:" + query);
			return null;
		}		
	}

	/**
	 * esegue una istruzione di modifica.  
	 * @param query
	 * @return
	 */
	public int executeUpdate(String query)  {
		cleanError();
		Statement ps=null; 
		try {
		
			ps = connection.createStatement();// createStatement(query);
			int out=ps.executeUpdate(query);
			ps.close();
			return out;

		} catch (SQLException e) {
			manageError(e, "ERROR executeUpdate: " + query);
			return -1;
		}		
	}
	/**
	 * esegue una stored procedure  
	 * @param statement blocco chiamata stored procedure
	 * @return
	 */
	public boolean executeStored(String statement)  {
		cleanError();
		CallableStatement ps=null; 
		try {
		
			ps = connection.prepareCall(statement);// createStatement(query);
			boolean out=ps.execute(statement);
			ps.close();
			return out;

		} catch (SQLException e) {
			manageError(e, "ERROR executeStored: " + statement);
			return false;
		}		
	}

	/**
	 * esegue una query.  
	 * @param connection
	 * @param query
	 * @param preparedStatement può essere null
	 * @return
	 */
	public ResultSet executeQuery(String query, PreparedStatement ps) {
		cleanError();

		ResultSet out=null;
		try {		
			if(ps == null) {
				ps = connection.prepareStatement(query);
			}
			out=ps.executeQuery();
			ps.close();		

		} catch (SQLException e) {
			manageError(e,"ERROR prepareStatement: " + query);
		}		
		return out;
	}

	public ResultSet executeQuery(String query, PreparedStatement preparedStatement, List<String> l) {
		cleanError();

		/*
	String query = “insert into Employee values (?, ?, ?)”;
	PreparedStatement prepStat = connection.prepareStatement(query);
	prepStat.setString(1, “Mario”);
	prepStat.setString(2, “Rossi”);
	prepStat.setString(3, “Analyst”);
	prepStat.executeUpdate();		 
		 */
		return null;
	}

	public Connection getConnection() {
		return connection;
	}
	
	private void manageError(Exception e, String msg) {
		lastException=e;
		if(! msg.isEmpty()) {		lg.error(msg); }		
		lg.error(e);		
	}

	/**
	 * set lastException = null;
	 */
	private void cleanError() {
		lastException = null;		
	}

	private Exception getError() {
		return lastException;		
	}

	private String getErrorMessage() {
		return Str.getErrorMessage(lastException);
	}

	private String getErrorStackTrace() {
		return Str.getErrorStackTrace(lastException);
	}

	
	
}


/*
try (Connection c = DriverManager.getConnection(url, properties);
     Statement s = c.createStatement()) {
 
    try {
        // First, we have to enable the DBMS_OUTPUT. Otherwise,
        // all calls to DBMS_OUTPUT made on our connection won't
        // have any effect.
        s.executeUpdate("begin dbms_output.enable(); end;");
 
        // Now, this is the actually interesting procedure call
        s.executeUpdate("begin my_procedure(1, 2); end;");
 
        // After we're done with our call(s), we can proceed to
        // fetch the SERVEROUTPUT explicitly, using
        // DBMS_OUTPUT.GET_LINES
        try (CallableStatement call = c.prepareCall(
            "declare "
          + "  num integer := 1000;"
          + "begin "
          + "  dbms_output.get_lines(?, num);"
          + "end;"
        )) {
            call.registerOutParameter(1, Types.ARRAY,
                "DBMSOUTPUT_LINESARRAY");
            call.execute();
 
            Array array = null;
            try {
                array = call.getArray(1);
                Stream.of((Object[]) array.getArray())
                      .forEach(System.out::println);
            }
            finally {
                if (array != null)
                    array.free();
            }
        }
    }
 
    // Don't forget to disable DBMS_OUTPUT for the remaining use
    // of the connection.
    finally {
        s.executeUpdate("begin dbms_output.disable(); end;");
    }

 */ 
