package net.precursorsbombs.serverlogic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.eclipsesource.json.JsonObject;

import net.precursorsbombs.geometry.MapPosition;
import net.precursorsbombs.geometry.Vec3;
import net.precursorsbombs.serverlogic.mapObjects.BlockTypes;
import net.precursorsbombs.serverlogic.mapObjects.BombMapObject;
import net.precursorsbombs.serverlogic.mapObjects.BrickMapObject;
import net.precursorsbombs.serverlogic.mapObjects.Cell;
import net.precursorsbombs.serverlogic.mapObjects.EmptyMapObject;
import net.precursorsbombs.serverlogic.mapObjects.Explosion;
import net.precursorsbombs.serverlogic.mapObjects.MapObject;
import net.precursorsbombs.serverlogic.mapObjects.WallMapObject;
import net.precursorsbombs.serverlogic.mapObjects.powerUpEffects.BombRadiusPowerUp;
import net.precursorsbombs.serverlogic.mapObjects.powerUpEffects.MaxBombsPowerUp;
import net.precursorsbombs.serverlogic.mapObjects.powerUpEffects.PowerUpObject;
import net.precursorsbombs.serverlogic.mapObjects.powerUpEffects.SpeedPowerUp;


public class BasicMap implements Map
{

	private static final int BLOCK_SIZE = 1;

    private Cell[][] pollyMap;

    public boolean updated = false;

    private final int size;

  // first coordinate is x axis, second coordinate is z axis
    private BlockTypes[][] map;

    private ArrayList<Vec3> startingPositions = new ArrayList<>();
    private int lastStartingPosition = 0;
    
    // # is an indestructible wall
    // . is a destructible brick
    //   is an empty space
    // S is an empty space where a player can spawn
    private String[] charMap = { 
            "#############", 
            "#S ....... S#", 
            "# #.#.#.#.# #", 
            "#...........#", 
            "#.#.#.#.#.#.#",
            "#...........#", 
            "#.#.#.#.#.#.#", 
            "#...........#", 
            "#.#.#.#.#.#.#", 
            "#...........#", 
            "# #.#.#.#.# #",
            "#S ....... S#", 
            "#############" };
    

    
    /*
    private String[] powerUpMap = {
    		"             ", 
    		"    s  s     ",
    		" j         s ",
    		"             ",
    		"             ",
    		"             ",
    		"             ",
    		"             ",
    		"           s ",
    		" s           ",
    		"             ",
    		"             ",
    		"             "
    };*/

    private HashMap<Integer, BombMapObject> bombs = new HashMap<>();
    private int bombNumber = 0;

    private HashMap<String, Player> players = new HashMap<>();

    private ArrayList<Integer> bombRemovalList = new ArrayList<>();

    private HashSet<Player> deadPlayers = new HashSet<>();

    public BasicMap()
    {
        this.size = charMap.length;
        reset();
    }

    @Override
    public void addPlayer(String name, Player p)
    {
        players.put(name, p);
    }

    @Override
    public void broadcastMessage(String msg)
    {
        for (String v : players.keySet())
        {
            players.get(v).sendMessage(msg);
        }
    }

    public void destroyAt(double x0, double z0)
    {
        long x = Math.round(x0);
        long z = Math.round(z0);
        if (x < 0 || z < 0 || z >= size || x >= size)
        {
            return;
        }

        int x1 = (int) x;
        int z1 = (int) z;


        if (map[x1][z1] == BlockTypes.DES || map[x1][z1] == BlockTypes.BMB)
        {
            map[x1][z1] = BlockTypes.EMP;
            pollyMap[x1][z1] = new Cell(new MapPosition(x1, z1));
            updated = true;
        }
    }
    public Vec3 getAStartingPosition()
    {
        if (startingPositions.isEmpty())
        {
            return new Vec3(0,0,0);
        }
        
        Vec3 result = startingPositions.get(lastStartingPosition);
        lastStartingPosition ++;
        lastStartingPosition = lastStartingPosition % startingPositions.size();
        return result;
    }

    @Override
    public BlockTypes getCellType(int x, int y)
    {
    	if (x >= map.length || y >= map.length || x < 0 || y < 0) return null;
        return map[x][y];
    }

    @Override
    public Player getPlayer(String id)
    {
        return players.get(id);
    }

    @Override
    public int getPlayerCount()
    {
        return players.size();
    }

    @Override
    public List<String> getPlayerNames()
    {
        ArrayList<String> out = new ArrayList<>();
        for (String k : players.keySet())
        {
            out.add(k);
        }
        return out;
    }

    @Override
       public int getSize() {
    	   return this.size;
       }

    @Override
    public boolean hasBombHere(long x, long z)
    {
        for (Integer i : bombs.keySet())
        {
            if (bombs.get(i).positionEquals(x, z))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isBorder(double x0, double z0)
    {
        long x = Math.round(x0);
        long z = Math.round(z0);

        return x <= 0 || x >= size - 1 || z <= 0 || z >= size - 1;
    }

    public boolean isPassable(double x0, double z0)
    {
        long x = Math.round(x0);
        long z = Math.round(z0);

        if (x < 0 || z < 0 || z >= size || x >= size)
        {
            return true;
        }
        
        BombMapObject bomb = findBombAt(x, z);
        if (bomb != null && !bomb.isPassable())
        {
            return false;
        }

        return map[(int) x][(int) z] == BlockTypes.EMP || map[(int) x][(int) z] == BlockTypes.BMB;
    }

    private BombMapObject findBombAt(long x, long z)
    {
        for (Integer i : bombs.keySet())
        {
            BombMapObject b = bombs.get(i);
            if (b.positionEquals(x, z))
            {
                return b;
            }
        }
        return null;
    }
    
    @Override
    public double closestPlayersAt(int x, int z)
    {
        double minimum = size;
        for (String s : players.keySet())
        {
            Player p = players.get(s);
            if (p.getPosition().distance(x, z) < minimum)
            {
                minimum = p.getPosition().distance(x, z);
            }
        }
        return minimum;
    }

    public void isPowerUp(double x0, double z0, Player player)
    {
    	 long x = Math.round(x0);
         long z = Math.round(z0);

         if (x < 0 || z < 0 || z >= size || x >= size || player.getPosition().getY() > 0.5)
         {
             return;
         }
         PowerUpObject p = null;
         for (MapObject o : pollyMap[(int) x][(int) z].getMapObjectObservers()) {;
        	 if (o.getType() == BlockTypes.POWER_UP) {
        		 p = ((PowerUpObject) o);
        		 updated = true;
        	 }
         }
         if (p != null) {
        	 p.acquirePowerUp(player);
             JsonObject obj = new JsonObject();
             obj.add("type", "powerup");
             obj.add("x", x);
             obj.add("y", z);
             obj.add("action", "remove");
             obj.add("p_type", p.getPType());
     		broadcastMessage(obj.toString());
         }
    }

    private void powerUpGeneration(Cell cell) {
    	Random r = new Random();
    	if (r.nextInt() % 4 == 0) {
    		int type = r.nextInt() % 3;
    		PowerUpObject p;
    		if (type % 3 == 0) p = new SpeedPowerUp(cell);
    		else if (type % 3 == 1) p = new MaxBombsPowerUp(cell);
    		else p = new BombRadiusPowerUp(cell);
    		cell.addMapObjectObserver(p);
    		
    		  
            JsonObject obj = new JsonObject();
            obj.add("type", "powerup");
            obj.add("x", cell.getCellPosition().getX());
            obj.add("y", cell.getCellPosition().getY());
            obj.add("action", "add");
            obj.add("p_type", p.getPType());
    		broadcastMessage(obj.toString());
    	}
    	
    }
    
    @Override
    public void notifyExplosion(Explosion e)
    {
        int x = e.getX();
        int y = e.getY();
        
        // check if there is a destructible block. If yes, destroy it.
        if (map[x][y] == BlockTypes.DES)
        {
            map[x][y] = BlockTypes.EMP;
            pollyMap[x][y] = new Cell(new MapPosition(x, y));
            powerUpGeneration(pollyMap[x][y]);
            updated = true;
        }
        
        // notify players of the explosion
        notifyPlayerExplosion(x, y);
        
        // notify other bombs of the explosion
        for (Integer i : bombs.keySet())
        {
            BombMapObject b = bombs.get(i);
            if (b.positionEquals(x, y) && !(bombRemovalList.contains(i)))
            {
                b.explode();
            }
        }
      
        broadcastMessage(e.getMessage());
    }
    
    
    @Override
    public void notifyPlayerExplosion(int x, int y)
    {
        for (String s : players.keySet())
        {
            players.get(s).checkExplosionAt(x, y);
        }
    }

    private void parseMap()
    {
        BlockTypes[][] map = new BlockTypes[charMap.length][charMap.length];
        for (int i = 0; i < charMap.length; i++)
        {
            for (int j = 0; j < charMap.length; j++)
            {
                char c = charMap[i].charAt(j);
                BlockTypes block = null;
                if (c == '#')
                {
                    block = BlockTypes.IND;
                } else if (c == '.')
                {
                    block = BlockTypes.DES;
                } else if (c == ' ' || c == 'S')
                {
                    block = BlockTypes.EMP;
                }
                map[j][size - i - 1] = block;
                
                if (c == 'S')
                {
                    // this cell can be starting position
                    startingPositions.add(new Vec3(j, 0, size - i - 1));
                }
            }
        }
        this.map = map;

    }
/*
    private void parsePowerUpMap() {
    	for (int i = 0; i < charMap.length; i++)
        {
            for (int j = 0; j < charMap.length; j++)
            {
                char c = powerUpMap[i].charAt(j);
                if (c == 's')
                {
                    pollyMap[j][i].addMapObjectObserver(new PowerUpObject(5, pollyMap[j][i]));
                } else if (c == 'j')
                {
                	pollyMap[j][i].addMapObjectObserver(new PowerUpObject(6, pollyMap[j][i]));
                }
            }
        }
    }
*/
    @Override
    public void placeBombAt(long x, long z, int radius)
    {
        if (matchEnded())
        {
            return;
        }
        
        bombNumber++;
        BombMapObject newBomb = new BombMapObject(2, radius, 50, this, x, z, bombNumber);
        bombs.put(bombNumber, newBomb);

        JsonObject obj = new JsonObject();
        obj.add("type", "bomb");
        obj.add("no", bombNumber);
        obj.add("action", "add");
        obj.add("x", x);
        obj.add("y", z);
        broadcastMessage(obj.toString());
    }

    @Override
    public void removeBomb(int bn)
    {
        bombRemovalList.add(bn);
        
        JsonObject obj = new JsonObject();
        obj.add("type", "bomb");
        obj.add("no", bn);
        obj.add("action", "remove");
        obj.add("x", 0);
        obj.add("y", 0);
        broadcastMessage(obj.toString());
    }

 
    
    @Override
    public void disconnectPlayer(String id)
    {
        Player p = players.get(id);
        if (p == null)
        {
            System.out.println("Player is null???");
        }
        p.sendDeathMessage();
        players.remove(id);
        deadPlayers.remove(p);
    }

    @Override
    public String sendUpdate()
    {
        JsonObject messageObject = new JsonObject();
        messageObject.add("type", "map");
        messageObject.add("size", Integer.toString(size));
        messageObject.add("blockSize", Integer.toString(BLOCK_SIZE));
        JsonObject mapObject = new JsonObject();
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                mapObject.add("" + i + "," + j, map[i][j].getType());
            }
        }

        messageObject.add("map", mapObject);
        
        String msg = messageObject.toString();
        return msg;
    }

    @Override
    public void setUpdated(boolean b)
    {
        this.updated = b;
        
    }

    @Override
    public void startSimulation(long currentTimeMillis)
    {
        for (String v : players.keySet())
        {
            Player p = players.get(v);
            p.sendMessage(sendUpdate());
            p.startSimulation(currentTimeMillis, this);
        }
    }

    public void toPollyMap() {
    	Cell[][] pollyMap = new Cell[charMap.length][charMap.length];
    	for (int i = 0; i < charMap.length; i++)
        {
            for (int j = 0; j < charMap.length; j++)
            {
                char c = charMap[i].charAt(j);
                Cell cell = new Cell(new MapPosition(i, j));
                if (c == '#')
                    cell.addMapObjectObserver(new WallMapObject());
                else if (c == '.')
                	cell.addMapObjectObserver(new BrickMapObject());
                else
                    cell.addMapObjectObserver(new EmptyMapObject());
                pollyMap[j][size - i - 1] = cell;
            }
        }
    	this.pollyMap = pollyMap;
    }

    // returns true if game in progress,
    // false if game ended
    public synchronized boolean updateAll()
    {
        // check match ending conditions
        if (matchEnded())
        {
            Player winner = getWinner();
            String winnername = "THE BOMBS";
            if (winner != null)
            {
                winnername = winner.getId();
                winner.incrementWins();
                winner.giveExp();
            }
            JsonObject obj = new JsonObject();
            obj.add("type", "gameend");
            obj.add("winner", winnername);
            broadcastMessage(obj.toString());
            
            // update player statistics that were participating
            for (String s : players.keySet())
            {
                players.get(s).sendStatsMessage();
                players.get(s).sendInviteLobbies();
            }

            return false;
        }
        
        long currentSystemTime = System.currentTimeMillis();
        for (String v : players.keySet())
        {
            players.get(v).update(currentSystemTime);
            broadcastMessage(players.get(v).sendUpdate());

            updateMap(currentSystemTime);

            if (updated())
                broadcastMessage(sendUpdate());
            setUpdated(false);
        }
        return true;
    }

    private Player getWinner()
    {
        for (String id : players.keySet())
        {
            Player p = players.get(id);
            if (p.isAlive())
            {
                return p;
            }
        }
        return null;
    }

    private boolean matchEnded()
    {
        int alivePlayers = players.size() - deadPlayers.size();
        
        boolean singleplayer = players.size() == 1;
        
        if (singleplayer)
        {
            return deadPlayers.size() == 1;
        } else
        {
            return alivePlayers <= 1;
        }

    }

    @Override
    public boolean updated()
    {
        return updated;
    }

    public void updateMap(long currentSystemTime)
    {
        for (Integer b : bombs.keySet() )
        {
            if (!bombRemovalList.contains(b))
            {
                bombs.get(b).update(currentSystemTime);
            }
        }
        
        for (Integer i : bombRemovalList)
        {
            bombs.remove(i);
        }
        bombRemovalList.clear();
    }

    @Override
    public void reset()
    {
        parseMap();
        toPollyMap();
        //parsePowerUpMap();
        for (String id : players.keySet())
        {
            Player p = players.get(id);
            p.resetStats();
        }
        deadPlayers.clear();
    }

    @Override
    public void addDeadPlayer(Player player)
    {
      deadPlayers.add(player);
      for (String id : players.keySet())
      {
        Player p = players.get(id);
        p.incrementPlace();
      }
    }

}
