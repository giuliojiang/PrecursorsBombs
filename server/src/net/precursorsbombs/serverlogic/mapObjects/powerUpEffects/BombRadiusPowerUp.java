package net.precursorsbombs.serverlogic.mapObjects.powerUpEffects;

import net.precursorsbombs.resources.MapObjectStatus;
import net.precursorsbombs.serverlogic.Player;
import net.precursorsbombs.serverlogic.mapObjects.Cell;

public class BombRadiusPowerUp extends PowerUpObject {

	public BombRadiusPowerUp(Cell cell) {
		super(cell);
	}

	@Override
	public void acquirePowerUp(Player player) {
		setStatus (MapObjectStatus.DEAD);
		player.increaseBombRadius();
		tellURDead();
	}
	
	public String getPType() {
		return "BombRadius";
	}

}
