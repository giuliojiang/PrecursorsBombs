package net.precursorsbombs.serverlogic.mapObjects.powerUpEffects;

import net.precursorsbombs.resources.MapObjectStatus;
import net.precursorsbombs.serverlogic.Player;
import net.precursorsbombs.serverlogic.mapObjects.Cell;

public class SpeedPowerUp extends PowerUpObject {

	public SpeedPowerUp(Cell cell) {
		super(cell);
	}

	@Override
	public void acquirePowerUp(Player player) {
		setStatus (MapObjectStatus.DEAD);
		player.setDefaultSpeed(player.getDefaultSpeed()+0.4);
		tellURDead();
	}
	
	
	public String getPType() {
		return "Speed";
	}

}
