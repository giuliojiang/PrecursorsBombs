package net.precursorsbombs.testsuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.precursorsbombs.geometry.MapPosition;
import net.precursorsbombs.geometry.Vec3;
import net.precursorsbombs.serverlogic.Map;
import net.precursorsbombs.serverlogic.Player;
import net.precursorsbombs.serverlogic.mapObjects.BlockTypes;
import net.precursorsbombs.serverlogic.mapObjects.BrickMapObject;
import net.precursorsbombs.serverlogic.mapObjects.Cell;
import net.precursorsbombs.serverlogic.mapObjects.Explosion;
import net.precursorsbombs.serverlogic.mapObjects.WallMapObject;

public class MockeryMap implements Map
{

    boolean hasBeenCalled = false;
    List<Cell> testCells = new ArrayList<>();
    private boolean guestAdded = false;
    private Player host;
    private Player guest;

    public Cell getCellWithPosition(int x, int y)
    {
        hasBeenCalled = true;
        Cell c = new Cell(new MapPosition(x, y));
        if (x == 1 && y == 3)
        {
            c.addMapObjectObserver(new WallMapObject());
        }
        if (x == 2 && y == 4)
        {
            c.addMapObjectObserver(new BrickMapObject());
        }
        testCells.add(c);
        return c;
    }

    @Override
    public String sendUpdate()
    {
        return null;
    }

    @Override
    public void destroyAt(double attackX, double attackZ)
    {

    }

    @Override
    public int getSize()
    {
        return 0;
    }

    @Override
    public boolean isPassable(double d, double e)
    {
        return true;
    }

    @Override
    public Vec3 getAStartingPosition()
    {
        return new Vec3(20, 0, 20);
    }

    @Override
    public boolean isBorder(double x0, double z0)
    {
        return false;
    }

    @Override
    public void isPowerUp(double x0, double z0, Player player)
    {

    }

    @Override
    public BlockTypes getCellType(int x, int y)
    {
        hasBeenCalled = true;

        if (x == 1 && y == 3)
        {
            return BlockTypes.IND;
        }
        if (x == 2 && y == 4)
        {
            return BlockTypes.DES;
        }
        return null;
    }

    @Override
    public boolean hasBombHere(long x, long z)
    {
        return false;
    }

    @Override
    public void placeBombAt(long x, long z, int radius)
    {

    }


    @Override
    public void removeBomb(int bombNumber)
    {

    }

    @Override
    public void updateMap(long currentSystemTime)
    {

    }

    @Override
    public boolean updated()
    {
        return false;
    }

    @Override
    public void setUpdated(boolean b)
    {

    }

    @Override
    public void addPlayer(String name, Player p)
    {
        if (name.equals("host"))
        {
            host = p;
        } else
        {
            guestAdded = true;
            guest = p;
        }
    }

    @Override
    public void broadcastMessage(String msg)
    {
        host.sendMessage(msg);
        if (guestAdded)
        {
            guest.sendMessage(msg);
        }
    }

    @Override
    public Player getPlayer(String id)
    {
        return null;
    }

    @Override
    public int getPlayerCount()
    {
        return 0;
    }

    @Override
    public List<String> getPlayerNames()
    {
        if (guestAdded)
        {
            return Arrays.asList("host", "guest");
        } else
        {
            return Arrays.asList("host");
        }
    }

    @Override
    public void notifyPlayerExplosion(int x, int y)
    {

    }

    @Override
    public void disconnectPlayer(String id)
    {

    }

    @Override
    public void startSimulation(long currentTimeMillis)
    {

    }

    @Override
    public boolean updateAll()
    {
        return false;
    }

    @Override
    public void notifyExplosion(Explosion e)
    {
        
    }

    @Override
    public void reset()
    {
        
    }

    @Override
    public double closestPlayersAt(int x, int z)
    {
        return 0;
    }

    @Override
    public void addDeadPlayer(Player player)
    {
        
    }

}
