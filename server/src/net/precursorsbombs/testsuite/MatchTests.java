package net.precursorsbombs.testsuite;

import net.precursorsbombs.match.BombermanMatch;
import net.precursorsbombs.serverlogic.Player;
import static net.precursorsbombs.testsuite.TestUtils.*;

import net.precursorsbombs.configuration.GlobalConfiguration;
import net.precursorsbombs.database.BombermanDatabase;
import net.precursorsbombs.database.DatabaseFactory;

public class MatchTests
{

    public static void run()
    {
        TestUtils.print("Match Tests");
        
        BombermanDatabase db = DatabaseFactory.makeDatabase(new GlobalConfiguration());
        
        MockeryMap map = new MockeryMap();
        NullNetworkConnection conn = new NullNetworkConnection();
        Player host = new Player(conn, "host", db);
        BombermanMatch match = new BombermanMatch(map, host);
        
        assertEquals(match.getLobbyName(), "host");
        
        match.sendUpdate();
        
        String msg = conn.lastMessage;
        assertTrue(msg.contains("lobbyhost"));
        
        assertTrue(match.isOwner(host));
        
        NullNetworkConnection conn2 = new NullNetworkConnection();
        Player guest = new Player(conn2, "guest", db);
        match.addPlayer(guest);;
        assertTrue(!match.isOwner(guest));
        match.sendUpdate();
        assertTrue(conn.lastMessage.contains("lobbyhost"));
        assertTrue(conn2.lastMessage.contains("\"host\""));
        assertTrue(conn2.lastMessage.contains("\"guest\""));

        assertTrue(!match.isStarted());
        
        match.startGame();
        assertTrue(match.isStarted());
        
        match.remove(host);
        match.remove(guest);
        assertTrue(!match.isStarted());
    }
}
