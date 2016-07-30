package net.precursorsbombs.resources;

/*
 * Enumeration representing the status of a map object
 * If the the status is DEAD, the map object is represented 
 * by the EmptyMapObject;
 */

public enum MapObjectStatus {

	ALIVE,
	DEAD,
	NA; // NA = not applicable = used only for a cell object
	
}
