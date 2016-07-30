package net.precursorsbombs.serverlogic.mapObjects.powerUpEffects;

import java.util.List;
import java.util.Set;
/*
 * Class representing a power up
 */
import net.precursorsbombs.resources.MapObjectStatus;

import net.precursorsbombs.serverlogic.Player;
import net.precursorsbombs.serverlogic.mapObjects.BlockTypes;
import net.precursorsbombs.serverlogic.mapObjects.Cell;
import net.precursorsbombs.serverlogic.mapObjects.MapObject;

public abstract class PowerUpObject implements MapObject {

	// Map Object Type
	BlockTypes type = BlockTypes.POWER_UP;
	// PowerUp status
	private MapObjectStatus status;
	// Reward
	// Pointer to the cell
	private Cell cell;

	public PowerUpObject(Cell cell) {
		status = MapObjectStatus.ALIVE;
		this.cell = cell;
	}
	
	public void tellURDead () {
		cell.tellURDead(this);
	}
	@Override
	public BlockTypes getType() {
		return type;
	}
	public abstract String getPType();
	
	// Notifies the cell award has been taken and must change with an empty cell
	public abstract void acquirePowerUp(Player player);
	
	@Override
	public MapObjectStatus getStatus() {
		return status;
	}
	
	public void  setStatus(MapObjectStatus status) {
		this.status = status;
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