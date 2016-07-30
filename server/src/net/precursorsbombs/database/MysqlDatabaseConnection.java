package net.precursorsbombs.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Class responsible for connecting to the database
 * 
 * NB: MUST ADD THE MYSQLCONNECTOR JAR TO EXT LIBRARIES
 *     FIND IN THE SERVER/JAR/LIB/EXT
 *     
 * NB: THIS WORKS ONLY WITHIN THE SERVER
 * 
 */

public class MysqlDatabaseConnection {

	// JDBC driver name and database URL
	private String DB_URL = "jdbc:mysql://localhost:3306/bomberman";

	// Database credentials
	static final String USER = "bomberman";
	static final String PASS = "bomberman";

	Connection conn;

	public MysqlDatabaseConnection() {
		connect();
	}

	private void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Query to retrieve results SELECT
	public List<Map<String, Object>> retrieveQuery(String sql) {
		return retrieveQuery(sql, true);
	}

	// if `retry` is set, the database will try to re-establish
	// a broken connection and query again. Otherwise, it will
	// print the Exception to console
	private List<Map<String, Object>> retrieveQuery(String sql, boolean retry) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// STEP 5: Extract data from result set
			Map<String, Object> row = null;
			ResultSetMetaData metaData = rs.getMetaData();
			Integer columnCount = metaData.getColumnCount();
			while (rs.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					row.put(metaData.getColumnName(i), rs.getObject(i));
				}
				result.add(row);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			connect();
			if (retry) {
				return retrieveQuery(sql, false);
			} else {
				e.printStackTrace();
			}
		}
		return result;
	}

	// Query to update/delete
	public int executeQuery(String sql) {
		return executeQuery(sql, true);
	}

	// if `retry` is set, the database will try to re-establish
	// a broken connection and query again. Otherwise, it will
	// print the Exception to console
	private int executeQuery(String sql, boolean retry) {
		int result = 0;
		try {
			Statement stmt = conn.createStatement();
			result = stmt.executeUpdate(sql);

			stmt.close();
		} catch (Exception e) {
			connect();
			if (retry) {
				return executeQuery(sql, false);
			} else {
				e.printStackTrace();
			}
		}
		return result;
	}

}
