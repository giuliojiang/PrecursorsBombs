package net.precursorsbombs.serverlogic.mapObjects;

import java.util.List;
import java.util.Set;

import net.precursorsbombs.resources.MapObjectStatus;
import net.precursorsbombs.serverlogic.Player;

/*
 * Class representing a wall 
 */

public class WallMapObject implements MapObject {

	// Map Object Type
    BlockTypes type = BlockTypes.IND;
	// Wall status
	private MapObjectStatus status;

	public WallMapObject() {
		status = MapObjectStatus.ALIVE;
	}
	
	@Override
	public BlockTypes getType() {
		return type;
	}

	@Override
	public MapObjectStatus getStatus() {
		return status;
	}

	@Override
	public void explode() {
		return;
	}

	@Override
	public void notifyMapObjectObserver(int damage) {
		return;
	}

	@Override
	public List<MapObject> getMapObjectObservers() {
		return null;
	}

	@Override
	public void removeMapObjectObserver(MapObject observer) {		
		return;
	}

	@Override
	public Set<Player> getPlayerObservers() {
		return null;
	}

	@Override
	public void addPlayerObserver(Player observer) {
		return;
	}

	@Override
	public void removePlayerObserver(Player observer) {
		return;
	}

	@Override
	public void addMapObjectObserver(MapObject observer) {
		return;
	}
}
