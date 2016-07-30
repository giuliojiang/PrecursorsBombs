package net.precursorsbombs.serverlogic;

import java.util.HashMap;

import com.eclipsesource.json.JsonObject;

import net.precursorsbombs.database.BombermanDatabase;
import net.precursorsbombs.database.PlayerStatistics;
import net.precursorsbombs.match.BombermanMatch;

// This class manages all the online players
public class PlayerManager implements PlayerManagerInterface {
	
	private HashMap<String, Player> players = new HashMap<>();
    private BombermanDatabase db;
	
	public PlayerManager(BombermanDatabase db)
	{
	    this.db = db;
	}
	
    private void addPlayer(NetworkConnection connection, String username)
    {
        String id = username + "";
        
        Player newPlayer = new Player(connection, id, db);
        
        players.put(id, newPlayer);
        
        // Send ID to player
        JsonObject youMessageObject = new JsonObject();
        youMessageObject.add("type", "you");
        youMessageObject.add("id", id);
        newPlayer.sendMessage(youMessageObject.toString());
        
        newPlayer.sendInitStats(this);
    }

    @Override
    public void connectPlayer(NetworkConnection conn, String username, String password)
    {
        // check username/password
        if (db.checkLogin(username, password))
        {
            // check if there was already the user connected
            Player existing = getPlayer(username);
            if (existing != null)
            {
                existing.alert("Your account has logged in from another browser");
                removePlayer(existing.getConnection());
            }
            
            addPlayer(conn, username);
        } else
        {
            // Login failed. Send message that it failed
            System.out.println("login failed");
            JsonObject obj = new JsonObject();
            obj.add("type", "loginfailed");
            conn.send(obj.toString());
        }
    }
    
    @Override
    public Player getPlayer(String id)
    {
        return players.get(id);
    }

    @Override
    public void removePlayer(NetworkConnection connection)
    {
        for (String v : players.keySet())
        {
            Player p = players.get(v);
            if (p.getConnection().equals(connection))
            {
                // remove player from any match he is in
                p.destroy();
                
                players.remove(p.getId());
                System.out.println(players.size() + " players connected");
                return;
            }
        }
        System.out.println(players.size() + " players connected");
    }

    // for now the name of the match is simply the name of the player
    // who created it
    @Override
    public BombermanMatch getMatch(String name)
    {
        Player owner = players.get(name);
        if (owner != null)
        {
            return owner.getMatch();
        }
        
        return null;
    }

    @Override
    public void connectGuest(NetworkConnection conn, String username)
    {
        addPlayer(conn, username);
    }

    @Override
    public void registerUser(NetworkConnection conn, String username, String password, String email)
    {
        String errorReason = "";
        boolean success = true;
        
        if (username == null || username.equals(""))
        {
            success = false;
            errorReason += "Username cannot be empty\n";
        }
        
        if (username != null && username.contains("guest"))
        {
            success = false;
            errorReason += "Username cannot contain 'guest'\n";
        }
        
        if (username != null && username.length() > 3 && username.substring(0, 4).equals("null"))
        {
            success = false;
            errorReason += "Username cannot be 'null'\n";
        }
        
        if (password == null || password.equals(""))
        {
            success = false;
            errorReason += "Password cannot be empty\n";
        }
        
        // check if username is already taken
        int uid = db.getUserId(username);
        if (uid != -1)
        {
            // if user id is valid, then username was already used
            success = false;
            errorReason += "Username was already taken by uid " + uid + "\n";
        }
        
        // register user
        if (!success)
        {
            JsonObject obj = new JsonObject();
            obj.add("type", "registrationfailed");
            obj.add("reason", errorReason);
            conn.send(obj.toString());
            return;
        } else
        {
            db.registerUser(username, password, email, username, "none");
            
            // check database for new user
            uid = db.getUserId(username);
            if (uid != -1)
            {
                JsonObject obj = new JsonObject();
                obj.add("type", "registrationsuccess");
                conn.send(obj.toString());
            } else
            {
                errorReason += "Could not complete registration due to database error";
                JsonObject obj = new JsonObject();
                obj.add("type", "registrationfailed");
                obj.add("reason", errorReason);
                conn.send(obj.toString());
            }
        }
    }

    @Override
    public boolean isOnline(String username)
    {
        return players.containsKey(username);
    }


    
}
