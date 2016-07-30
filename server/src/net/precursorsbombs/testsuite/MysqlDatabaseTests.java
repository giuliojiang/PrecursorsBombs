package net.precursorsbombs.testsuite;

import static net.precursorsbombs.testsuite.TestUtils.assertEquals;
import static net.precursorsbombs.testsuite.TestUtils.assertTrue;

import java.util.List;

import net.precursorsbombs.database.MysqlDatabase;

public class MysqlDatabaseTests
{

    public static void run()
    {
        TestUtils.print("MysqlDatabase Tests");
        
        MysqlDatabase db = new MysqlDatabase();
        
        assertEquals(new Integer(db.getUserId("USER")), new Integer(2));
        assertEquals(new Integer(db.getUserId("USEr")), new Integer(-1));
        assertEquals(new Integer(db.getUserId("USER2")), new Integer(1));
        assertEquals(new Integer(db.getUserId("USERaserasfcsaersaer")), new Integer(-1));

        assertTrue(db.checkLogin("USER", "pass"));
        assertTrue(db.checkLogin("USER2", "pass"));
        assertTrue(!db.checkLogin("USER", "faseeeer"));
        assertTrue(!db.checkLogin("USER2", ""));
        
        db.addFriend("USER", "USER2");
        
        assertTrue(db.getFriendsOf("inexistinguser_-_-_-bablablaishsofhser").isEmpty());
        
        assertEquals(new Integer(db.getItemId("BOMB1")), new Integer(1));
        assertEquals(new Integer(db.getItemId("BOMB2")), new Integer(2));
        
        assertTrue(db.getItemsOfUser("USER").isEmpty());
        List<String> user2items = db.getItemsOfUser("USER2");
        assertEquals(user2items.size(), 2);
        assertTrue(user2items.contains("BOMB1"));
        assertTrue(user2items.contains("BOMB2"));
        
        db.addItemToUser("USER", "BOMB1");
        assertTrue(db.getItemsOfUser("USER").contains("BOMB1"));
        db.removeItemFromUser("USER", "BOMB1");
        assertTrue(db.getItemsOfUser("USER").isEmpty());
        
        // Friends request tests
        
        // create 2 new users
        String nullUser1 = "null_user_1";
        String nullUser2 = "null_user_2";
        db.registerUser(nullUser1, nullUser1, nullUser1, nullUser1, nullUser1);
        db.registerUser(nullUser2, nullUser2, nullUser2, nullUser2, nullUser2);
        
        // 1 makes a request to 2
        db.friendRequest(nullUser1, nullUser2);
        assertTrue(db.getFriendRequests(nullUser2).contains(nullUser1));
        
        // but they are not friends yet
        assertTrue(!db.getFriendsOf(nullUser2).contains(nullUser1));
        
        // now 2 accepts the invite
        db.acceptFriendRequest(nullUser2, nullUser1);
        
        // and they should be friends
        assertTrue(db.getFriendsOf(nullUser1).contains(nullUser2));
        
        // delete the two users
        db.removeUser(nullUser1);
        db.removeUser(nullUser2);
    }
    
}
