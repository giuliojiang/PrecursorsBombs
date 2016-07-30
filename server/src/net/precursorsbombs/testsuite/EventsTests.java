package net.precursorsbombs.testsuite;

import static net.precursorsbombs.testsuite.TestUtils.assertTrue;

import net.precursorsbombs.configuration.GlobalConfiguration;
import net.precursorsbombs.database.BombermanDatabase;
import net.precursorsbombs.database.DatabaseFactory;
import net.precursorsbombs.match.BombermanMatch;
import net.precursorsbombs.serverlogic.EventsManager;
import net.precursorsbombs.serverlogic.NetworkConnection;
import net.precursorsbombs.serverlogic.Player;
import net.precursorsbombs.serverlogic.PlayerManager;
import net.precursorsbombs.serverlogic.PlayerManagerInterface;

public class EventsTests
{

    public static void run()
    {
        TestUtils.print("Events Tests");
        
        NullNetworkConnection connection = new NullNetworkConnection();
        
        GlobalConfiguration conf = new GlobalConfiguration();
        
        BombermanDatabase db = DatabaseFactory.makeDatabase(conf);
        
        PlayerManagerInterface playerManager = new PlayerManager(db);
        
        playerManager.connectPlayer(connection, "null", "emptypassword");

        EventsManager em = new EventsManager(playerManager);
        
        Player p = playerManager.getPlayer("null");
        
        p.startSimulation(0, new MockeryMap());

        em.handleString(connection, "{\"type\":\"paction\",\"id\":\"null\",\"state\":\"moving\",\"curBomb\":\"standard\",\"bombs\":[{\"type\":\"standard\",\"amount\":\"2\",\"shape\":\"+\",\"delay\":\"2\",\"radius\":\"1\",\"damage\":\"2\"}],\"rotationoffset\":1.5}");

        
        // handle lobby creation/joining messages
        PlayerManagerMockery pm = new PlayerManagerMockery();
        em = new EventsManager(pm);
        
        // create player A
        Player a = pm.getPlayer("A");
        assertTrue(a != null);
        
        // create player B
        Player b = pm.getPlayer("B");
        assertTrue(b != null);

        // A and B ask for their lobbies, which should be empty
        em.handleString(a.getConnection(), "{\"type\":\"lobbies\",\"id\":\"A\"}");
        em.handleString(b.getConnection(), "{\"type\":\"lobbies\",\"id\":\"B\"}");
        String ma = pm.ca.lastMessage;
        String mb = pm.cb.lastMessage;
        assertTrue(ma.contains("[]"));
        assertTrue(mb.contains("[]"));
        
        // A invites B. B doesn't see the invite because A hasn't created lobby
        em.handleString(a.getConnection(), "{\"type\": \"invite\",\"id\": \"A\",\"guest\": \"B\"}");
        mb = pm.cb.lastMessage;
        assertTrue(mb.contains("[]"));
        
        // Now A creates the lobby and invites again
        em.handleString(a.getConnection(), "{\"type\": \"newlobby\",\"id\": \"A\"}");
        em.handleString(a.getConnection(), "{\"type\": \"invite\",\"id\": \"A\",\"guest\": \"B\"}");
        mb = pm.cb.lastMessage;
        assertTrue(mb.contains("\"A\""));
        
        // B joins the lobby. Both should be eable to see each other in the lobby list
        em.handleString(b.getConnection(), "{\"type\": \"join\",\"id\": \"B\",\"name\": \"A\"}");
        ma = pm.ca.lastMessage;
        mb = pm.cb.lastMessage;
        assertTrue(ma.contains("lobbyhost"));
        assertTrue(mb.contains("inlobby"));
        assertTrue(mb.contains("\"A\""));
        assertTrue(mb.contains("\"B\""));
    }
}

class PlayerManagerMockery implements PlayerManagerInterface
{
    NullNetworkConnection ca = new NullNetworkConnection();
    NullNetworkConnection cb = new NullNetworkConnection();
    BombermanDatabase db = DatabaseFactory.makeDatabase(new GlobalConfiguration());
    Player a = new Player(ca, "A", db);
    Player b = new Player(cb, "B", db);

    @Override
    public void connectPlayer(NetworkConnection conn, String username, String password)
    {

    }

    @Override
    public Player getPlayer(String id)
    {
        if (id.equals("A"))
        {
            return a;
        } else if (id.equals("B"))
        {
            return b;
        } else
        {
            return null;
        }
    }

    @Override
    public void removePlayer(NetworkConnection connection)
    {

    }

    @Override
    public BombermanMatch getMatch(String id)
    {
        if (id.equals("A"))
        {
            return a.getMatch();
        } else if (id.equals("B"))
        {
            return b.getMatch();
        } else
        {
            return null;
        }
    }

    @Override
    public void connectGuest(NetworkConnection conn, String username)
    {
        
    }

    @Override
    public void registerUser(NetworkConnection conn, String username, String password, String email)
    {

    }

    @Override
    public boolean isOnline(String username)
    {
        return username.equals("A") || username.equals("B");
    }

    
}
