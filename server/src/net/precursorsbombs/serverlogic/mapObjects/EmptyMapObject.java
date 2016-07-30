package net.precursorsbombs.serverlogic.mapObjects;

import java.util.List;
import java.util.Set;

import net.precursorsbombs.resources.MapObjectStatus;
import net.precursorsbombs.serverlogic.Player;

/*
 * Class representing an empty map object(i.e. empty cell) 
 */

public class EmptyMapObject implements MapObject {

	// Map Object Type
	BlockTypes type = BlockTypes.EMP;
	private static int unique = 0;
	private int id;

	public EmptyMapObject() {
		unique = unique + 1;
		id = unique;
	}
	
	@Override
	public BlockTypes getType() {
		return type;
	}

	public int getEmptyMapObjectId() {
		return id;
	}

	@Override
	public void notifyMapObjectObserver(int damage) {
		return;
	}

	@Override
	public void addMapObjectObserver(MapObject observer) {
		return;
	}

	@Override
	public MapObjectStatus getStatus() {
		return MapObjectStatus.ALIVE;
	}

	@Override
	public void explode() {
		return;
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof EmptyMapObject))
		{
			return false;
		}
		EmptyMapObject other = (EmptyMapObject) obj;
		return this.id == other.id;
	}

	@Override
	public String toString()
	{
		return "[ " + this.id + " ]";
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

}
