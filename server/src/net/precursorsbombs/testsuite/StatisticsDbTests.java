package net.precursorsbombs.testsuite;

import net.precursorsbombs.configuration.GlobalConfiguration;
import net.precursorsbombs.database.BombermanDatabase;
import net.precursorsbombs.database.DatabaseFactory;
import net.precursorsbombs.serverlogic.EventsManager;
import net.precursorsbombs.serverlogic.Player;
import net.precursorsbombs.serverlogic.PlayerManager;
import net.precursorsbombs.serverlogic.PlayerManagerInterface;

import static net.precursorsbombs.testsuite.TestUtils.*;


public class StatisticsDbTests
{
    
    public static void run()
    {
        TestUtils.print("Statistics DB Tests");
        
        NullNetworkConnection connection = new NullNetworkConnection();
        
        GlobalConfiguration conf = new GlobalConfiguration();
        
        BombermanDatabase db = DatabaseFactory.makeDatabase(conf);
        
        PlayerManagerInterface playerManager = new PlayerManager(db);
        
        playerManager.connectPlayer(connection, "null", "password");

        EventsManager em = new EventsManager(playerManager);
        
        Player p = playerManager.getPlayer("null");
        
        
        // test for listfriends message
        em.handleString(connection, "{\"type\":\"listfriends\",\"id\":\"null\"}");
        
        assertTrue(connection.messages.get(connection.messages.size()-2).contains("listfriends"));
        assertTrue(connection.messages.get(connection.messages.size()-2).contains("giulio"));
        
        // test for listitems message
        em.handleString(connection, "{\r\n    \"type\":\"owneditems\",\r\n    \"id\":\"null\"\r\n}");
        assertTrue(connection.lastMessage.contains("owneditems"));
        assertTrue(connection.lastMessage.contains("[]"));
        
        // test for addfriend message
        em.handleString(connection, "{\r\n    \"type\":\"addfriend\",\r\n    \"id\":\"null\",\r\n    \"friend\":\"B\"\r\n}");
        String logged = TestUtils.getLastLoggedMessage();
        assertTrue(logged.contains("You are already friends"));
        
        // registration test
        em.handleString(connection,  "{\r\n    \"type\":\"registeruser\",\r\n    \"username\":\"guest_2244\",\r\n    \"password\":\"password\",\r\n    \"email\":\"blabla@at.at\"\r\n}");
        assertTrue(connection.lastMessage.contains("Username cannot contain 'guest'"));

        em.handleString(connection, "{\r\n    \"type\":\"registeruser\",\r\n    \"username\":\"\",\r\n    \"password\":\"\",\r\n    \"email\":\"\"\r\n}");
        assertTrue(connection.lastMessage.contains("Username cannot be empty"));
        assertTrue(connection.lastMessage.contains("Password cannot be empty"));
        
        em.handleString(connection, "{\r\n    \"type\":\"registeruser\",\r\n    \"username\":\"newvaliduserblabla\",\r\n    \"password\":\"astupidrandompassword\",\r\n    \"email\":\"someemail@example.com\"\r\n}");
        assertTrue(TestUtils.getLastLoggedMessage().contains("newvaliduserblabla"));

        // item tests
        em.handleString(connection, "{\r\n    \"type\":\"adduseritem\",\r\n    \"uid\":\"null\",\r\n    \"item\":\"boooomb\"\r\n}");
        assertTrue(TestUtils.getLastLoggedMessage().contains("boooomb"));
        assertTrue(TestUtils.getLastLoggedMessage().contains("null"));
        
        // online friends
        playerManager.connectPlayer(new NullNetworkConnection(), "newvaliduserblabla", "astupidrandompassword");
        em.handleString(connection, "{\r\n    \"type\":\"addfriend\",\r\n    \"id\":\"null\",         \r\n    \"friend\":\"newvaliduserblabla\"  \r\n}");
        em.handleString(connection, "{\r\n    \"type\":\"onlinefriends\",\r\n    \"id\": \"null\"\r\n}");
        assertTrue(connection.lastMessage.contains("onlinefriends"));
        assertTrue(connection.lastMessage.contains("newvaliduserblabla"));
        
    }

}
