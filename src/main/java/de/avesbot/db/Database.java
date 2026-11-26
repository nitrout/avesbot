package de.avesbot.db;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;

/**
 * The database connection.
 * 
 * @author nitrout
 */
public class Database {

	private Database() {
		throw new IllegalStateException("Database shall not be instantiated");
	}
	
	public static Connection connection;
	public static MariaDbDataSource dataSource;
	
	/**
	 * Initializes the database connection withe the given connection parameters.
	 * 
	 * @param host the database host
	 * @param user the database user
	 * @param password the database password
	 * @param database the database name
	 * @throws SQLException 
	 */
	public static void init(String host, String user, String password, String database) throws SQLException {
		dataSource = new MariaDbDataSource("jdbc:mariadb://"+host+"/"+database);
		dataSource.setUser(user);
		dataSource.setPassword(password);
		connection = dataSource.getConnection();
	}
	
	private static Connection getConnection() throws SQLException {
		if(connection.isClosed()) {
			connection = dataSource.getConnection();
		}
		return connection;
	}
	
	/**
	 * Creates a new prepared statement.
	 * 
	 * @param stmt the sql string for the prepared statement
	 * @return the prepared statement
	 * @throws SQLException 
	 */
	public static PreparedStatement prepareStatement(String stmt) throws SQLException {
		return getConnection().prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
	}
	
	/**
	 * Executes an SQL update statement.
	 * 
	 * @param query the SQL string
	 * @return number of updated rows
	 * @throws SQLException 
	 */
	public static int update(String query) throws SQLException {
		try (Statement stmt = getConnection().createStatement()) {
			return stmt.executeUpdate(query);
		}
	}
	
	/**
	 * Executes an SQL select statement.
	 * 
	 * @param query the sql select string
	 * @return the statements result set
	 * @throws SQLException 
	 */
	public static ResultSet query(String query) throws SQLException {
		Statement stmt = getConnection().createStatement();
		return stmt.executeQuery(query);
	}
	
	/**
	 * Close the database connection.
	 * 
	 * @throws SQLException 
	 */
	public static void close() throws SQLException {
		getConnection().close();
	}
}
