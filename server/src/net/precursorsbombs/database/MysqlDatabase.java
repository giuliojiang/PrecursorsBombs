package net.precursorsbombs.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.precursorsbombs.testsuite.TestUtils;
import net.precursorsbombs.utils.Pair;
import net.precursorsbombs.utils.PasswordHasher;

public class MysqlDatabase implements BombermanDatabase
{

    private static final int BASE_EXP = 25;

    MysqlDatabaseConnection dbconn = new MysqlDatabaseConnection();

    @Override
    public int getUserId(String username)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT user_id\r\nFROM Player\r\nWHERE BINARY username = \"");
        sb.append(username);
        sb.append("\";");
        String query = sb.toString();

        List<Map<String, Object>> result = dbconn.retrieveQuery(query);

        if (result.isEmpty())
        {
            // username not found
            return -1;
        }

        Integer uid;
        try
        {
            uid = (Integer) result.get(0).get("user_id");
        } catch (ClassCastException e)
        {
            return -1;
        }

        return uid;
    }

    @Override
    public boolean checkLogin(String username, String password)
    {
        // check that user exists
        int uid = getUserId(username);
        if (uid == -1)
        {
            return false;
        }

        // get existing salt
        String salt = "";
        StringBuilder query = new StringBuilder();
        query.append("SELECT \r\n\tsalt, password\r\nFROM \r\n\t`Player` \r\nWHERE \r\n\tuser_id = ");
        query.append(uid);
        query.append(";");
        List<Map<String, Object>> result = dbconn.retrieveQuery(query.toString());
        if (result.isEmpty())
        {
            return false; // user has no salt???
        }
        Map<String, Object> entry = result.get(0);
        try
        {
            salt = (String) entry.get("salt");
        } catch (Exception e)
        {
            return false;
        }

        // get old hash
        String oldhash = "";
        try
        {
            oldhash = (String) entry.get("password");
        } catch (Exception e)
        {
            return false;
        }

        // compare
        return PasswordHasher.checkPassword(password, oldhash, salt);
    }

    @Override
    public void addFriend(String username1, String username2)
    {
        // get id of username1
        int uid1 = getUserId(username1);
        if (uid1 == -1)
        {
            return;
        }

        // get id of username2
        int uid2 = getUserId(username2);
        if (uid2 == -1)
        {
            return;
        }

        // see if entry already exists
        String q = "SELECT `id`, `friend_id` FROM `Friends` WHERE `id` = " + uid1 + " AND `friend_id` = " + uid2 + ";";
        List<Map<String,Object>> r = dbconn.retrieveQuery(q);
        if (!r.isEmpty())
        {
            // If they are already friends, do nothing.
            return;
        }
        
        // add in the database
        StringBuilder query = new StringBuilder();
        if (!getFriendsOf(username1).contains(username2))
        {
            query.append("INSERT INTO Friends\r\nVALUES (");
            query.append(uid1);
            query.append(", ");
            query.append(uid2);
            query.append(");");
            dbconn.executeQuery(query.toString());
        }

        if (!getFriendsOf(username2).contains(username1))
        {
            query = new StringBuilder();
            query.append("INSERT INTO Friends\r\nVALUES (");
            query.append(uid2);
            query.append(", ");
            query.append(uid1);
            query.append(");");
            dbconn.executeQuery(query.toString());
        }
    }

    @Override
    public List<String> getFriendsOf(String username)
    {
        ArrayList<String> out = new ArrayList<>();
        int uid = getUserId(username);
        if (uid == -1)
        {
            return out;
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT p.username\r\nFROM Player p JOIN Friends f ON f.friend_id = p.user_id\r\nWHERE f.id = ");
        query.append(uid);
        query.append("\r\n;");
        List<Map<String, Object>> results = dbconn.retrieveQuery(query.toString());

        for (Map<String, Object> entry : results)
        {
            String aFriend = (String) entry.get("username");
            out.add(aFriend);
        }

        return out;
    }

    @Override
    public int getItemId(String itemType)
    {
        StringBuilder query = new StringBuilder();
        query.append("SELECT `item_id` FROM `Items` WHERE BINARY `type` = \"");
        query.append(itemType);
        query.append("\";");

        List<Map<String, Object>> results = dbconn.retrieveQuery(query.toString());

        if (results.isEmpty())
        {
            return -1;
        }

        Map<String, Object> entry = results.get(0);
        int iid = (Integer) entry.get("item_id");
        return iid;
    }

    @Override
    public List<String> getItemsOfUser(String username)
    {
        List<String> out = new ArrayList<>();

        // get uid of the user
        int uid = getUserId(username);
        if (uid == -1)
        {
            return out;
        }

        // make the query
        StringBuilder query = new StringBuilder();
        query.append("SELECT i.type\r\nFROM Items i JOIN PlayerItem p ON i.item_id = p.iid\r\nWHERE p.uid = ");
        query.append(uid);
        query.append(";");
        List<Map<String, Object>> result = dbconn.retrieveQuery(query.toString());
        for (Map<String, Object> entry : result)
        {
            String itemname = (String) entry.get("type");
            out.add(itemname);
        }
        return out;
    }

    @Override
    public void addItemToUser(String username, String itemType)
    {
        int uid = getUserId(username);
        if (uid == -1)
        {
            return;
        }

        int iid = getItemId(itemType);
        if (iid == -1)
        {
            return;
        }

        if (getItemsOfUser(username).contains(itemType))
        {
            return;
        }

        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO PlayerItem\r\nVALUES (");
        query.append(uid);
        query.append(",");
        query.append(iid);
        query.append(");");
        dbconn.executeQuery(query.toString());
    }

    @Override
    public void removeItemFromUser(String username, String itemType)
    {
        int uid = getUserId(username);
        if (uid == -1)
        {
            return;
        }

        int iid = getItemId(itemType);
        if (iid == -1)
        {
            return;
        }

        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM PlayerItem\r\nWHERE uid = ");
        query.append(uid);
        query.append(" AND iid = ");
        query.append(iid);
        query.append(";");
        dbconn.executeQuery(query.toString());
    }

    @Override
    public void newItem(String itemType)
    {
        // get a new iid
        int iid = incrementAndGetIid();

        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO `Items`(`item_id`, `type`) \r\nVALUES \r\n\t(");
        query.append(iid);
        query.append(", \"");
        query.append(itemType);
        query.append("\");");
        dbconn.executeQuery(query.toString());
    }

    @Override
    public void registerUser(String username, String password, String email, String playername, String guildname)
    {
        // check if user already exists
        if (getUserId(username) != -1)
        {
            return;
        }

        // get new uid
        int uid = incrementAndGetUid();

        // generate new password + salt
        Pair<String, String> pass_salt = PasswordHasher.hashNewPassword(password);
        String hashed = pass_salt.a;
        String salt = pass_salt.b;

        // add to database
        StringBuilder query = new StringBuilder();
        query.append(
                "INSERT INTO `Player`(\r\n    `user_id`, \r\n    `username`, \r\n    `password`, \r\n    `email`, \r\n    `playername`, \r\n    `guild`, \r\n    `points`, \r\n    `experience`, \r\n    `lastgameplayed`, \r\n    `wins`, \r\n    `totalgames`, \r\n    `salt`\r\n) \r\nVALUES \r\n\t(\r\n        ");
        query.append(uid);
        query.append(", \r\n        \"");
        query.append(username);
        query.append("\", \r\n        \"");
        query.append(hashed);
        query.append("\", \r\n        \"");
        query.append(email);
        query.append("\", \r\n        \"");
        query.append(playername);
        query.append(
                "\", \r\n        \"none\", \r\n        0, \r\n        0, \r\n        NOW(), \r\n        0, \r\n        0,\r\n        \"");
        query.append(salt);
        query.append("\"\r\n\t);");
        dbconn.executeQuery(query.toString());
    }

    private synchronized int incrementAndGetUid()
    {
        // get old uid
        String query = "SELECT `value` FROM `System` WHERE `type` = \"uid\";";
        List<Map<String, Object>> result = dbconn.retrieveQuery(query);
        Map<String, Object> entry = result.get(0);
        int uid = (Integer) entry.get("value");

        // increment uid
        uid++;

        // update uid
        StringBuilder q = new StringBuilder();
        q.append("UPDATE \r\n\t`System` \r\nSET \r\n\t`value` = ");
        q.append(uid);
        q.append("\r\nWHERE \r\n\t`type` = \"uid\";");
        dbconn.executeQuery(q.toString());

        return uid;
    }

    private synchronized int incrementAndGetIid()
    {
        // get old iid
        String query = "SELECT `value` FROM `System` WHERE `type` = \"iid\";";
        List<Map<String, Object>> result = dbconn.retrieveQuery(query);
        Map<String, Object> entry = result.get(0);
        int iid = (Integer) entry.get("value");

        // increment iid
        iid++;

        // update iid
        StringBuilder q = new StringBuilder();
        q.append("UPDATE \r\n\t`System` \r\nSET \r\n\t`value` = ");
        q.append(iid);
        q.append("\r\nWHERE \r\n\t`type` = \"iid\";");
        dbconn.executeQuery(q.toString());

        return iid;
    }

    @Override
    public PlayerStatistics getPlayerStatistics(String username)
    {

        // get user id
        int uid = getUserId(username);
        if (uid == -1)
        {
            return new PlayerStatistics(0, 0, 0);
        }

        // query
        StringBuilder query = new StringBuilder();
        query.append(
                "SELECT \r\n\t`experience`, \r\n\t`wins`, \r\n\t`totalgames`\r\nFROM \r\n\t`Player` \r\nWHERE \r\n\tuser_id = ");
        query.append(uid);
        query.append(";");

        List<Map<String, Object>> result = dbconn.retrieveQuery(query.toString());
        if (result.isEmpty())
        {
            return new PlayerStatistics(0, 0, 0);
        }

        Map<String, Object> entry = result.get(0);
        int experience = (Integer) entry.get("experience");
        int wins = (Integer) entry.get("wins");
        int totalgames = (Integer) entry.get("totalgames");

        return new PlayerStatistics(experience, wins, totalgames);
    }

    @Override
    public List<PlayerWins> getFriendsOrderedByWins(String username)
    {
        // get user id
        int uid = getUserId(username);
        if (uid == -1)
        {
            return new ArrayList<PlayerWins>();
        }

        // do query
        StringBuilder query = new StringBuilder();
        query.append(
                "SELECT p.username, p.wins\r\nFROM Player p JOIN Friends f ON f.friend_id = p.user_id\r\nWHERE f.id = ");
        query.append(uid);
        query.append(";");
        List<Map<String, Object>> results = dbconn.retrieveQuery(query.toString());

        ArrayList<PlayerWins> out = new ArrayList<>();
        for (Map<String, Object> entry : results)
        {
            String friendid = (String) entry.get("username");
            Integer wins = (Integer) entry.get("wins");
            out.add(new PlayerWins(friendid, wins));
        }

        // sort list
        Collections.sort(out);
        return out;

    }

    @Override
    public void incrementGamesPlayed(String username)
    {
        // get user id
        int uid = getUserId(username);
        if (uid == -1)
        {
            return;
        }

        // get old statistics
        PlayerStatistics oldStat = getPlayerStatistics(username);

        // increment
        int newGamesPlayed = oldStat.getTotalgames() + 1;

        // make query
        StringBuilder query = new StringBuilder();
        query.append("UPDATE \r\n\t`Player` \r\nSET \r\n\t`totalgames` = ");
        query.append(newGamesPlayed);
        query.append("\r\nWHERE \r\n\t`user_id` = ");
        query.append(uid);
        query.append(";");
        dbconn.executeQuery(query.toString());
    }

    @Override
    public void incrementWins(String username)
    {
        // get user id
        int uid = getUserId(username);
        if (uid == -1)
        {
            return;
        }

        // get old statistics
        PlayerStatistics oldStat = getPlayerStatistics(username);

        // increment
        int newWins = oldStat.getWins() + 1;

        // make query
        StringBuilder query = new StringBuilder();
        query.append("UPDATE \r\n\t`Player` \r\nSET \r\n\t`wins` = ");
        query.append(newWins);
        query.append("\r\nWHERE \r\n\t`user_id` = ");
        query.append(uid);
        query.append(";");
        dbconn.executeQuery(query.toString());
    }

    @Override
    public void giveExp(String username, int place) {
      // get user id
      int uid = getUserId(username);
      if (uid == -1)
      {
        return;
      }

      // get old statistics
      PlayerStatistics oldStat = getPlayerStatistics(username);

      // increment
      int expEarnt = BASE_EXP * place;
      int newExp = oldStat.getExperience() + expEarnt;

      // make query
      StringBuilder query = new StringBuilder();
      query.append("UPDATE \r\n\t`Player` \r\nSET \r\n\t`experience` = ");
      query.append(newExp);
      query.append("\r\nWHERE \r\n\t`user_id` = ");
      query.append(uid);
      query.append(";");
      dbconn.executeQuery(query.toString());
    }

    @Override
    public boolean friendRequest(String requester, String newfriend)
    {
        // Adds a pair (newfriend, requester) to table PendingFriends

        // Prevent from inviting himself
        if (requester.equals(newfriend))
        {
            return false;
        }

        // get newfriend's uid
        int newfriend_uid = getUserId(newfriend);
        if (newfriend_uid == -1)
        {
            return false;
        }

        // get requester's uid
        int requester_uid = getUserId(requester);
        if (requester_uid == -1)
        {
            return false;
        }

        // check if already friends
        List<String> existingFriends = getFriendsOf(requester);
        if (existingFriends.contains(newfriend))
        {
            TestUtils.log("Already friends: (" + requester + "," + newfriend + ")");
            return true;
        }

        // check if request was already made
        List<String> existingRequests = getFriendRequests(newfriend);
        if (existingRequests.contains(requester))
        {
            TestUtils.log("Request (" + newfriend + "," + requester + ") already made");
            return true;
        }

        // add to database
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO `PendingFriends`(`receiver`, `requester`) \r\nVALUES \r\n\t(");
        query.append(newfriend_uid);
        query.append(", ");
        query.append(requester_uid);
        query.append(");");
        dbconn.executeQuery(query.toString());

        return true;
    }

    @Override
    public boolean acceptFriendRequest(String receiver, String requester)
    {
        // check if request table has the entry (receiver, requester)
        List<String> existingRequests = getFriendRequests(receiver);
        if (!existingRequests.contains(requester))
        {
            TestUtils.log("No matching request found. Must be requested before accepting");
            return false;
        }

        // remove the entry from the request table using rejectFriendRequest
        rejectFriendRequest(receiver, requester);

        // use addFriend
        addFriend(receiver, requester);

        return true;
    }

    @Override
    public boolean rejectFriendRequest(String receiver, String requester)
    {
        // Remove from requests table entries
        // receiver_id, requester_id
        // requester_id, receiver_id

        // get receiver id
        int receiver_uid = getUserId(receiver);
        if (receiver_uid == -1)
        {
            return false;
        }

        // get requester id
        int requester_uid = getUserId(requester);
        if (requester_uid == -1)
        {
            return false;
        }

        // do query
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM \r\n\t`PendingFriends` \r\nWHERE \r\n\t`receiver` = ");
        query.append(receiver_uid);
        query.append(" \r\n\tAND `requester` = ");
        query.append(requester_uid);
        query.append(";");
        dbconn.executeQuery(query.toString());

        query = new StringBuilder();
        query.append("DELETE FROM \r\n\t`PendingFriends` \r\nWHERE \r\n\t`receiver` = ");
        query.append(requester_uid);
        query.append(" \r\n\tAND `requester` = ");
        query.append(receiver_uid);
        query.append(";");
        dbconn.executeQuery(query.toString());

        return true;
    }

    @Override
    public List<String> getFriendRequests(String receiver)
    {
        // get the uid of the receiver
        int receiver_uid = getUserId(receiver);

        if (receiver_uid == -1)
        {
            return new ArrayList<>();
        }

        // make query
        StringBuilder query = new StringBuilder();
        query.append(
                "SELECT p.username\r\nFROM PendingFriends f JOIN Player p ON f.requester = p.user_id\r\nWHERE f.receiver = ");
        query.append(receiver_uid);
        query.append(";");

        List<Map<String, Object>> results = dbconn.retrieveQuery(query.toString());

        // collect results
        ArrayList<String> out = new ArrayList<>();
        for (Map<String, Object> entry : results)
        {
            String value = (String) entry.get("username");
            out.add(value);
        }

        return out;
    }

    @Override
    public void removeUser(String username)
    {
        // get user id
        int uid = getUserId(username);
        if (uid == -1)
        {
            return;
        }

        // do query
        String query = "DELETE FROM `Player` WHERE `user_id` = " + uid + ";";
        dbconn.executeQuery(query);
    }

}
