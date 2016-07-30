package net.precursorsbombs.serverlogic.mapObjects;

import java.util.List;
import java.util.Set;

import net.precursorsbombs.resources.MapObjectStatus;
import net.precursorsbombs.serverlogic.Player;

public interface MapObject {
	
	BlockTypes getType();
	void notifyMapObjectObserver(int damage);
	List<MapObject> getMapObjectObservers();
	void addMapObjectObserver(MapObject observer);
	void removeMapObjectObserver(MapObject observer);
	Set<Player> getPlayerObservers();
	void addPlayerObserver(Player observer);
	void removePlayerObserver(Player observer);
	MapObjectStatus getStatus();
	void explode();
	
}
