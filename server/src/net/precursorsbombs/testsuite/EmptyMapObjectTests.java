package net.precursorsbombs.testsuite;

import static net.precursorsbombs.testsuite.TestUtils.assertTrue;
import net.precursorsbombs.serverlogic.mapObjects.EmptyMapObject;

public class EmptyMapObjectTests {
	
	public static void run()
    {		
        TestUtils.print("EmptyMapObject Tests");
        
        EmptyMapObject testObj1 = new EmptyMapObject();
        EmptyMapObject testObj2 = new EmptyMapObject();
        
        // Test that different empty objects are different
        assertTrue(testObj1.getEmptyMapObjectId() != testObj2.getEmptyMapObjectId());
        assertTrue(!testObj1.equals(testObj2));
        
        // Test that same empty object is same
        assertTrue(testObj1.equals(testObj1));
        
    }

}
