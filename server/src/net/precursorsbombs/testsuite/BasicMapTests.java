package net.precursorsbombs.testsuite;

import static net.precursorsbombs.testsuite.TestUtils.assertEquals;
import static net.precursorsbombs.testsuite.TestUtils.assertTrue;

import net.precursorsbombs.configuration.GlobalConfiguration;
import net.precursorsbombs.database.BombermanDatabase;
import net.precursorsbombs.database.DatabaseFactory;
import net.precursorsbombs.geometry.Vec3;
import net.precursorsbombs.serverlogic.BasicMap;
import net.precursorsbombs.serverlogic.Player;

public class BasicMapTests
{
    public static void run()
    {
        TestUtils.print("BasicMap Tests");
        
        BasicMap map = new BasicMap();
        
        BombermanDatabase db = DatabaseFactory.makeDatabase(new GlobalConfiguration());
        
        NullNetworkConnection p1c = new NullNetworkConnection();
        Player p1 = new Player(p1c, "p1", db);
        
        NullNetworkConnection p2c = new NullNetworkConnection();
        Player p2 = new Player(p2c, "p2", db);
        
        map.addPlayer("p1", p1);
        map.addPlayer("p2", p2);
        
        map.startSimulation(0);
        
        // Game start message
        assertTrue(p1c.lastMessage.contains("type"));
        assertTrue(p1c.lastMessage.contains("initgame"));
        assertTrue(p2c.lastMessage.contains("type"));
        assertTrue(p2c.lastMessage.contains("initgame"));
        
        // Test that the map update message is correct
        String mapupdate = map.sendUpdate();
        assertTrue(mapupdate.contains("map"));
        assertTrue(mapupdate.contains("type"));
        
        // getAStartingPosition test
        assertTrue(
                p1.getPosition().equals(new Vec3(11,0,11)) 
                && p2.getPosition().equals(new Vec3(1,0,11))
                ||
                p1.getPosition().equals(new Vec3(1,0,11)) 
                && p2.getPosition().equals(new Vec3(11,0,11))
                );
        
        // message broadcast test
        map.broadcastMessage("helo");
        assertEquals(p1c.lastMessage, "helo");
        assertEquals(p2c.lastMessage, "helo");
        
        // destroyAt tests
        map.setUpdated(false);
        map.destroyAt(-1, -1);
        assertTrue(!map.updated());
        
        map.setUpdated(false);
        map.destroyAt(5, 13);
        assertTrue(!map.updated());
        
        map.setUpdated(false);
        map.destroyAt(0, 0);
        assertTrue(!map.updated());
        
        map.setUpdated(false);
        map.destroyAt(1, 5);
        assertTrue(map.updated());
        
        // TODO carry on from hasBombHere
    }
}
