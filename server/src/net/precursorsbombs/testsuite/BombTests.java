package net.precursorsbombs.testsuite;

import static net.precursorsbombs.testsuite.TestUtils.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.precursorsbombs.geometry.MapPosition;
import net.precursorsbombs.serverlogic.mapObjects.BombMapObject;
import net.precursorsbombs.serverlogic.mapObjects.Cell;
import net.precursorsbombs.serverlogic.mapObjects.Explosion;

public class BombTests {
	
	public static void run()
    {
		TestUtils.print("Bomb Tests");
        
		// NB: no tests for adding or removing map objects or player
		// because the methods are same as in Cell
		MockeryMap map = new MockeryMap();
		BombMapObject testBomb = new BombMapObject(100, 2, 10, map, 2, 3, 0);
		// Test that the bomb behaves as desired
		
		// Test no explosion
		testBomb.startBombTimer(10);
		testBomb.update(50);
		assertTrue(!map.hasBeenCalled);
		
		// Test explosion
		testBomb.update(800200);
		assertTrue(map.hasBeenCalled);
		
		// Test correct cells has been damaged
		List<Cell> expectedCells = new ArrayList<>();
		expectedCells.add(new Cell(new MapPosition(2, 1)));
		expectedCells.add(new Cell(new MapPosition(2, 2)));
		expectedCells.add(new Cell(new MapPosition(2, 4)));
		expectedCells.add(new Cell(new MapPosition(1, 3)));
		expectedCells.add(new Cell(new MapPosition(3, 3)));
		expectedCells.add(new Cell(new MapPosition(4, 3)));
		
		for (Explosion o : testBomb.getTargetCells()) {
			assertTrue(expectedCells.contains(o));
		}
    }

}
