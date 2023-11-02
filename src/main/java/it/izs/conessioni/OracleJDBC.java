package it.izs.conessioni;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleJDBC {
	
	private Connection connection = null;
	
	private static final String JDBC_ORACLE = "oracle.jdbc.driver.OracleDriver";

	public OracleJDBC(String url, String user, String password) {
		super(); 
		
		creaConnessione(url, user, password);
	}

	private void creaConnessione(String url, String user, String password) {
		try {
			Class.forName(JDBC_ORACLE);
		} catch (ClassNotFoundException e) {
			System.out.println("Driver Oracle JDBC non trovato");
			e.printStackTrace();
			return;
		}

		try {
			connection = DriverManager. getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("Connessione fallita! Guarda lo stacktrace sulla console");
			e.printStackTrace();
			return;
		}
	}

	public void chiudiConnessione() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}
	}

	public ResultSet executeQuery(Connection connection, String query, PreparedStatement preparedStatement) {
		try {
			preparedStatement = connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// esecuzione dell'istruzione SELECT
		ResultSet rs = null;
		try {
			rs = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	public ResultSet executeQueryWithStringParameters(Connection connection, String query, PreparedStatement preparedStatement, String parameter) {
		try {
			query = query.replace("?", parameter);
			preparedStatement = connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// esecuzione dell'istruzione SELECT
		ResultSet rs = null;
		try {
			rs = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	

	public Connection getConnection() {
		return connection;
	}
	
}
