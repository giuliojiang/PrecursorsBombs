package net.precursorsbombs.serverlogic.mapObjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eclipsesource.json.JsonObject;

import net.precursorsbombs.geometry.MapPosition;
import net.precursorsbombs.resources.MapObjectStatus;
import net.precursorsbombs.serverlogic.Player;

/*
 * Class representing a cell on the map
 */

public class Cell implements MapObject {

	private final int BLOCK_SIZE = 1;
	// Map Object Type
	BlockTypes type = BlockTypes.CELL;
	// Defines the position of this cell on the 2D map plane
	private MapPosition cellPosition;
	// Defines list of objects currently in this cell
	private List<MapObject> cellObjects;	
	// Defines list of players close to the cell
	private HashSet<Player> playersCloseToCell;
	// Status of a cell is always the same
	private MapObjectStatus status;

	public Cell (MapPosition cellPosition) {
		status = MapObjectStatus.NA;
		this.cellPosition = cellPosition;
		cellObjects = new ArrayList<>();
		playersCloseToCell = new HashSet<>();
	}

	// Setters and getters and removes
	@Override
	public BlockTypes getType() {
		return type;
	}
	
	@Override
	public MapObjectStatus getStatus() {
		return status;
	}

	public MapPosition getCellPosition() {
		return cellPosition;
	}

	public void setCellPosition(MapPosition newCellPosition) {
		cellPosition = newCellPosition;
	}

	public List<BlockTypes> getMapObjectTypes() {
		if(!cellObjects.isEmpty()) {
			List<BlockTypes> result = new ArrayList<>();
			for (MapObject o : cellObjects) {
				result.add(o.getType());
			}
			return result;
		}
		return null;
	}

	@Override
	public void addMapObjectObserver(MapObject newCellObject) {
		// check if same object already in the cell
		if(!cellObjects.isEmpty()) {
			for (MapObject o : cellObjects) {
				if(o == newCellObject) return;
			}
		}
		cellObjects.add(newCellObject);
	}

	@Override
	public void removeMapObjectObserver(MapObject observer) {
		if(cellObjects.contains(observer)) {
			cellObjects.remove(observer);
		}
		return;
	}

	@Override
	public Set<Player> getPlayerObservers() {
		return playersCloseToCell;
	}

	@Override
	public void addPlayerObserver(Player player) {
		playersCloseToCell.add(player);
	}

	@Override
	public void removePlayerObserver(Player observer) {
		if(playersCloseToCell.contains(observer)) {
			playersCloseToCell.remove(observer);		
		}
		return;
	}
	
	public List<MapObject> getMapObjectObservers() {
		return cellObjects;
	}

	public void tellURDead(MapObject cellObject) {
		if(cellObjects.contains(cellObject)) {
			int index = cellObjects.indexOf(cellObject);
			cellObjects.remove(index);
			//MapObject o = cellObjects.get(index);
			//o = new EmptyMapObject();
		}
	}

	// Event has happened that has impact on the cell
	// Cell notifies the objects in it and the players near it
	@Override
	public void notifyMapObjectObserver(int damage) {
		notifyAllMapObjectObservers(damage);
		notifyAllPlayerObservers(damage);

		// If the objects at the cell are DEAD after the cell effect
		// change them with an empty map objects
		if (!cellObjects.isEmpty()) {
			for (MapObject o : cellObjects) {
				if (o.getStatus() == MapObjectStatus.DEAD) {
					o = new EmptyMapObject();
        }
			}
		}
	}

	// Notifies all objects in the cell that might take impact
	private void notifyAllMapObjectObservers(int damage) {
		if (cellObjects.isEmpty()) return;
		for (MapObject o : cellObjects) {
			o.notifyMapObjectObserver(damage);
		}
	}

	// Notifies all players near the cell that might take impact
	private void notifyAllPlayerObservers(int damage) {
		if (playersCloseToCell.isEmpty()) return;
		for (Player p : playersCloseToCell) {
			p.takeDamage(damage);
		}
	}

	// Method that can be used for further extensions, 
	// like we decide that a cell will explode randomly
	@Override
	public void explode() {
		return;
	}

	@Override
	public int hashCode()
	{
		return cellPosition.hashCode();
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
		if (!(obj instanceof Cell))
		{
			return false;
		}
		Cell other = (Cell) obj;
		return this.cellPosition.equals(other.getCellPosition());
	}

	@Override
	public String toString()
	{
		return "[ " + this.cellPosition.toString() + " ]";
	}

}
