package net.precursorsbombs.testsuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static net.precursorsbombs.testsuite.TestUtils.assertTrue;
import net.precursorsbombs.database.MysqlDatabaseConnection;

public class DatabaseConnectionTests {


	public static void run()
	{
		TestUtils.print("DatabaseConnection Tests");

		MysqlDatabaseConnection dc = new MysqlDatabaseConnection();
		String testQuery1 = "SELECT username FROM Player ORDER BY user_id DESC;";	
		List<String> expected = new ArrayList<>();
		expected.add("USER2");
		expected.add("USER");

		List<Map<String, Object>> result = dc.retrieveQuery(testQuery1);
		
		List<String> usernames = new ArrayList<>();
		for (Map<String, Object> entry : result)
		{
		    String aUsername = (String) entry.get("username");
		    usernames.add(aUsername);
		}
		
		for (String e : expected)
		{
		    assertTrue(usernames.contains(e));
		}

	}

}
