package net.precursorsbombs.testsuite;

import static net.precursorsbombs.testsuite.TestUtils.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.precursorsbombs.configuration.GlobalConfiguration;
import net.precursorsbombs.database.BombermanDatabase;
import net.precursorsbombs.database.DatabaseFactory;
import net.precursorsbombs.geometry.MapPosition;
import net.precursorsbombs.serverlogic.NetworkConnection;
import net.precursorsbombs.serverlogic.Player;
import net.precursorsbombs.serverlogic.mapObjects.Cell;
import net.precursorsbombs.serverlogic.mapObjects.EmptyMapObject;
import net.precursorsbombs.serverlogic.mapObjects.MapObject;


public class CellTests {
	
	public static void run()
    {		
        TestUtils.print("Cell Tests");
        
        MapPosition testPos = new MapPosition(2, 3);         
        MapObject testObj1 = new EmptyMapObject();
        MapObject testObj2 = new EmptyMapObject();
        MapObject testObj3 = new EmptyMapObject();
        
        BombermanDatabase db = DatabaseFactory.makeDatabase(new GlobalConfiguration());

        Player p1 = new Player(new NullNetworkConnection(), "p1", db);
        Player p2 = new Player(new NullNetworkConnection(), "p2", db);
        Player p3 = new Player(new NullNetworkConnection(), "p3", db);

        Cell testCell = new Cell(testPos); 
        
        // Test that returns correct cell position
        TestUtils.assertEquals(testCell.getCellPosition(), new MapPosition(2, 3));
        
        // Test that initially cell does not have any players and objects
        assertTrue(testCell.getMapObjectObservers().isEmpty());
        assertTrue(testCell.getPlayerObservers().isEmpty());
        
        // Test that adds a map object
        testCell.addMapObjectObserver(testObj1);
        assertTrue(testCell.getMapObjectObservers().size() == 1);
        
        // Test that adds a player 
        testCell.addPlayerObserver(p1);
        assertTrue(testCell.getPlayerObservers().size() == 1);
        
        // Test that does not add a map object more than once
        assertTrue(testCell.getMapObjectObservers().size() == 1);
        testCell.addMapObjectObserver(testObj1);
        assertTrue(testCell.getMapObjectObservers().size() == 1);
        
        // Test that does not add a player object more than once
        assertTrue(testCell.getPlayerObservers().size() == 1);
        testCell.addPlayerObserver(p1);
        assertTrue(testCell.getPlayerObservers().size() == 1);
        
        // Test that you can add more map objects
        testCell.addMapObjectObserver(testObj2);
        assertTrue(testCell.getMapObjectObservers().size() == 2);
        testCell.addMapObjectObserver(testObj3);
        assertTrue(testCell.getMapObjectObservers().size() == 3);   
        
        // Test that you can add more players
        testCell.addPlayerObserver(p2);
        assertTrue(testCell.getPlayerObservers().size() == 2);
        testCell.addPlayerObserver(p3);
        assertTrue(testCell.getPlayerObservers().size() == 3);
        
        // Test that you can remove a map object
        testCell.removeMapObjectObserver(testObj1);
        assertTrue(testCell.getMapObjectObservers().size() == 2);
        
        // Test that you can remove a player
        testCell.removePlayerObserver(p1);
        assertTrue(testCell.getPlayerObservers().size() == 2);
        
        // Test the cell is the same
        assertTrue(testCell.equals(testCell));
        
        // Test cells are different
        assertTrue(!testCell.equals(new Cell(new MapPosition(200, 300))));
        
    }
	
}

class NullNetworkConnection implements NetworkConnection {

    List<String> messages = new ArrayList<>();
    String lastMessage = "";
    
    @Override
    public void send(String message)
    {
        if (message == null)
        {
            lastMessage = "";
        } else
        {
            lastMessage = message;
        }
        messages.add(lastMessage);
    }
    
}

