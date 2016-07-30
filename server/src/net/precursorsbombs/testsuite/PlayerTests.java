package net.precursorsbombs.testsuite;

import static net.precursorsbombs.testsuite.TestUtils.assertEquals;
import static net.precursorsbombs.testsuite.TestUtils.assertTrue;

import net.precursorsbombs.configuration.GlobalConfiguration;
import net.precursorsbombs.database.BombermanDatabase;
import net.precursorsbombs.database.DatabaseFactory;
import net.precursorsbombs.geometry.Vec3;
import net.precursorsbombs.serverlogic.BasicMap;
import net.precursorsbombs.serverlogic.Player;

public class PlayerTests
{

    public static void run()
    {
        TestUtils.print("Player Tests");
        
        BombermanDatabase db = DatabaseFactory.makeDatabase(new GlobalConfiguration());

        
        Player player = new Player(new NullNetworkConnection(), "player", db);
        
        // Set the simulation's starting time to 0
        player.startSimulation(0, new MockeryMap());
        
        player.setMoving();
        
        player.setSpeed(1);
        
        player.updateRotation(0);
        
        // Set current time to 100 now
        player.update(1000);

        assertEquals(player.getPosition(), new Vec3(21,0,20));
        
        player.updateRotation((3d/2d) * Math.PI);
        
        player.update(2000);
        
        assertEquals(player.getPosition(), new Vec3(21,0,19));
        
        player.updateRotation((2d) * Math.PI);
        
        player.setSpeed(2);
        
        player.update(3000);
        
        assertEquals(player.getPosition(), new Vec3(23,0,19));
        
        // now disable movement
        
        player.stopMoving();
        
        player.update(5000);
        
        assertEquals(player.getPosition(), new Vec3(23,0,19));
        
        // now add rotation offset
        
        player.setMoving();
        player.setSpeed(1);
        
        player.updateRotation(1);
        player.setRotationOffset(2.141592);
        
        player.update(6000);
        assertEquals(player.getPosition(), new Vec3(22,0,19));
        
        // guest player
        
        Player g = new Player(new NullNetworkConnection(), "guest_54957", db);
        assertTrue(g.isGuest());
        
        assertTrue(!player.isGuest());
        
        // boundary collisions
        
        g.startSimulation(0, new BasicMap());
        g.setMoving();
        g.setSpeed(5);
        g.updateRotation(3d);
        g.update(50);
        assertEquals(g.getPosition(), new Vec3(1.0,0.0,11.035280002014966));
        g.setJump();
        g.updateRotation(5.06);
        g.update(100);
        assertEquals(g.getPosition(), new Vec3(1.0851631720269472,0.2,10.80023270011531));
    }
    
}
