package net.precursorsbombs.match;

import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import net.precursorsbombs.serverlogic.Map;
import net.precursorsbombs.serverlogic.Player;
import net.precursorsbombs.timer.BombermanTimer;

// Represents a match between players
public class BombermanMatch {
	
	private static final int MIN_PLAYERS = 1;
	private static final int MAX_PLAYERS = 4;

	private Map map;
	private BombermanTimer timer;

	private boolean started = false;
	
	private Player host;
    private Thread timerThread = null;
	
	public BombermanMatch(Map map, Player host)
	{
		this.map = map;
		this.timer = new BombermanTimer(this.map, this);
		this.host = host;
		
		addPlayer(host);
	}
	
	public synchronized void addPlayer(Player p)
	{
		int count = map.getPlayerCount();
		if (count >= MAX_PLAYERS)
		{
			p.alert("Could not join lobby: it is full.");
			return;
		}
		
		map.addPlayer(p.getId(), p);
	}

    public String getLobbyName()
    {
        return host.getId();
    }

    // send user list to all players in the lobby
    public void sendUpdate()
    {
        JsonObject msgobj = new JsonObject();
        
        msgobj.add("type", "inlobby");
        
        JsonArray list = new JsonArray();
        List<String> usernames = map.getPlayerNames();
        for (String i : usernames)
        {
            list.add(i);
        }
        msgobj.add("list", list);
        
        map.broadcastMessage(msgobj.toString());
        
        // send host information
        JsonObject obj = new JsonObject();
        obj.add("type", "lobbyhost");
        host.sendMessage(obj.toString());
    }

    public boolean isOwner(Player player)
    {
        return host == player;
    }

    public void startGame()
    {
        if (timerThread != null)
        {
            System.out.println("Another game in progress!");
            return;
        }
        
        map.startSimulation(System.currentTimeMillis());
        
        started = true;
        
        timer = new BombermanTimer(map, this);
        this.timerThread = new Thread(timer);
        this.timerThread.start();
        
    }
    
    public boolean isStarted()
    {
        return started;
    }

    public void remove(Player player)
    {

        map.disconnectPlayer(player.getId());
        if (map.getPlayerCount() == 0 && timerThread != null)
        {
            timerThread.interrupt();
            started = false;
        }
    }

    @Override
    public int hashCode()
    {
        return host.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BombermanMatch other = (BombermanMatch) obj;
        return this.host.getId().equals(other.host.getId());
    }

    public Map getGameObjects()
    {
        return map;
    }
    
    public void reset()
    {
        if (timerThread != null && timerThread.isAlive())
        {
            timerThread.interrupt();
        }
        timerThread = null;
        map.reset();
    }

    public void removeAll(Player host)
    {
        List<String> usernames = map.getPlayerNames();
        for (String u : usernames)
        {
            if (!u.equals(host.getId()))
            {
                Player p = map.getPlayer(u);
                if (p != null)
                {
                    p.exitLobby();
                }
            }
        }
        reset();
    }
    
    

}
