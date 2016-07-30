package net.precursorsbombs.serverlogic;

import java.util.List;

import net.precursorsbombs.geometry.Vec3;
import net.precursorsbombs.serverlogic.mapObjects.BlockTypes;
import net.precursorsbombs.serverlogic.mapObjects.BombMapObject;
import net.precursorsbombs.serverlogic.mapObjects.Explosion;

public interface Map
{

    void addPlayer(String name, Player p);

    void broadcastMessage(String msg);

    public void destroyAt(double attackX, double attackZ);

    public Vec3 getAStartingPosition();

    public BlockTypes getCellType(int x, int y);

    Player getPlayer(String id);

    int getPlayerCount();

    List<String> getPlayerNames();

    public int getSize();

    public boolean hasBombHere(long x, long z);

    public boolean isBorder(double x0, double z0);

    public boolean isPassable(double d, double e);

    public void isPowerUp(double x0, double z0, Player player);

    void notifyExplosion(Explosion e);

    void notifyPlayerExplosion(int x, int y);

    public void placeBombAt(long x, long z, int radius);

    public void removeBomb(int bombNumber);

    void disconnectPlayer(String id);
    
    String sendUpdate();

    public void setUpdated(boolean b);

    void startSimulation(long currentTimeMillis);

    public boolean updateAll();

    public boolean updated();

    public void updateMap(long currentSystemTime);

    void reset();

    public double closestPlayersAt(int x, int z);

    void addDeadPlayer(Player player);

}