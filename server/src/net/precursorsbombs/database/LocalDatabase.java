package net.precursorsbombs.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.precursorsbombs.testsuite.TestUtils;

public class LocalDatabase implements BombermanDatabase {

    private int nextUid = 0;
    private int nextIid = 0;
    
    private HashMap<String, Integer> users = new HashMap<>();
    private HashMap<String, Integer> items = new HashMap<>();
    private HashMap<String, String> friends = new HashMap<>();
    
    private int getNextUid()
    {
        nextUid++;
        return nextUid;
    }



  private int getNextIid()
    {
        nextIid++;
        return nextIid;
    }
    
    public LocalDatabase()
    {
        registerUser("giulio", "9f690543059d726fb217336086d6f0242281b368", "g@gmail.com", "giulio", "none");
        registerUser("A", "fuhashriuhnapohpiuhvpiuase", "a@gmail.com", "A", "none");
        registerUser("B", "ioihfas8798f79a87f9s7e9r87er", "b@gmail.com", "B", "none");
    }
    
    @Override
    public int getUserId(String username)
    {
        if (users.containsKey(username))
        {
            return users.get(username);
        } else
        {
            return -1;
        }
    }

    @Override
    public boolean checkLogin(String username, String password)
    {
        return true;
    }


    @Override
    public void addFriend(String username1, String username2)
    {
        TestUtils.log("Adding friends " + username1 + " " + username2);
        friends.put(username1, username2);
        friends.put(username2, username1);
    }

    @Override
    public List<String> getFriendsOf(String username)
    {
        // In this implementation, everyone is friend of everyone
        ArrayList<String> result = new ArrayList<>();
        for (String user : users.keySet())
        {
            result.add(user);
        }
        return result;
    }

    @Override
    public int getItemId(String itemType)
    {
        if (items.containsKey(itemType))
        {
            return items.get(itemType);
        } else
        {
            newItem(itemType);
            return items.get(itemType);
        }
    }

    @Override
    public List<String> getItemsOfUser(String username)
    {
        // everyone owns everything
        ArrayList<String> result = new ArrayList<>();
        for (String item : items.keySet())
        {
            result.add(item);
        }
        return result;
    }

    @Override
    public void addItemToUser(String username, String itemType)
    {
        TestUtils.log("Adding item " + itemType + " to user " + username);
    }

    @Override
    public void removeItemFromUser(String username, String itemType)
    {
        // empty
    }

    @Override
    public void newItem(String itemType)
    {
        int iid = getNextIid();
        items.put(itemType, iid);
    }

    @Override
    public void registerUser(String username, String password, String email, String playername,
            String guildname)
    {
        int uid = getNextUid();
        users.put(username, uid);
        TestUtils.log("Registered new user " + username);
    }

    @Override
    public PlayerStatistics getPlayerStatistics(String username)
    {
        return new PlayerStatistics(999, 12, 666);
    }

    @Override
    public List<PlayerWins> getFriendsOrderedByWins(String username)
    {
        return new ArrayList<PlayerWins>();
    }

    @Override
    public void incrementGamesPlayed(String username)
    {
        
    }

    @Override
    public void incrementWins(String username)
    {
        
    }

    @Override
    public void giveExp(String username, int place) {

    }

    @Override
    public boolean friendRequest(String requester, String newfriend)
    {
        return users.containsKey(requester) && users.containsKey(newfriend);
    }

    @Override
    public boolean acceptFriendRequest(String receiver, String requester)
    {
        return true;
    }

    @Override
    public boolean rejectFriendRequest(String receiver, String requester)
    {
        return true;
    }

    @Override
    public List<String> getFriendRequests(String receiver)
    {
        return new ArrayList<>();
    }

    @Override
    public void removeUser(String username)
    {
        
    }

}
