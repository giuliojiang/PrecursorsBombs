package net.precursorsbombs.serverlogic.mapObjects.powerUpEffects;

import net.precursorsbombs.resources.MapObjectStatus;
import net.precursorsbombs.serverlogic.Player;
import net.precursorsbombs.serverlogic.mapObjects.Cell;

public class MaxBombsPowerUp extends PowerUpObject {

	public MaxBombsPowerUp(Cell cell) {
		super(cell);
	}

	@Override
	public void acquirePowerUp(Player player) {
		setStatus (MapObjectStatus.DEAD);
		player.incrementMaxBombs();
		tellURDead();
	}
	
	public String getPType() {
		return "MaxBombs";
	}

}
