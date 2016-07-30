package net.precursorsbombs.serverlogic.mapObjects;

import java.util.List;
import java.util.Set;

import net.precursorsbombs.resources.MapObjectStatus;
import net.precursorsbombs.serverlogic.Player;

/*
 * Class representing a brick
 */

public class BrickMapObject implements MapObject {

	// Map Object Type
	BlockTypes type = BlockTypes.DES;
	// Brick status
	private MapObjectStatus status;
	
	public BrickMapObject() {
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
		if (damage > 0) {
			status = MapObjectStatus.DEAD;
		}
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

    @Override
    public Set<Player> getPlayerObservers()
    {
        return null;
    }
}
