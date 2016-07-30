package net.precursorsbombs.serverlogic.mapObjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.precursorsbombs.geometry.MapPosition;
import net.precursorsbombs.resources.MapObjectStatus;
import net.precursorsbombs.serverlogic.Map;
import net.precursorsbombs.serverlogic.Player;

/*
 * Class representing a bomb object  
 */

public class BombMapObject implements MapObject {

	// Map Object Type
	BlockTypes type = BlockTypes.BMB;
	// Bomb status
	private MapObjectStatus status;
	// Cell position
	// Radius of bomb
	private int radius;
	// Damage of bomb
	private int damage;
	// List of cells the bomb will have an effect on
	private List<Explosion> targetCells = new ArrayList<>();
	// Observers
	private ArrayList<MapObject> observers = new ArrayList<>();
	// List of players somehow related to the bomb
    private HashSet<Player> playersInRelationshipWithTheBomb;
    // Reference to current game manager
    // GameObjects is used to pass observer messages to all
    // other objects
    private Map map;

    private int bombNumber;

    private long initialUpdate = -1;
    private double bombDelay = -1;

    private boolean passable = true;
    private long x;
    private long z;

    // Cell thing can be removed or have two constructors
    public BombMapObject(double bombDelay, int radius, int damage, 
            Map map, long x, long z, int bombNumber)
    {
        status = MapObjectStatus.ALIVE;
        this.bombDelay = bombDelay;
        this.radius = radius;
        this.damage = damage;
        playersInRelationshipWithTheBomb = new HashSet<>();
        this.map = map;
        this.x = x;
        this.z = z;
        this.bombNumber = bombNumber;

        startBombTimer(System.currentTimeMillis());
    }

    // Getters, setters and removes
    @Override
    public BlockTypes getType()
    {
        return type;
    }

    @Override
    public MapObjectStatus getStatus()
    {
        return status;
    }

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	@Override
	public List<MapObject> getMapObjectObservers() {
		return observers;
	}

	@Override
	public void addMapObjectObserver(MapObject newCellObject) {
		// check if same object already in the cell
		if (!observers.contains(newCellObject))
		{
		    observers.add(newCellObject);
		}
	}

	@Override
	public void removeMapObjectObserver(MapObject observer) {
		if(observers.contains(observer)) {
			observers.remove(observer);
		}
	}

	@Override
	public Set<Player> getPlayerObservers() {
		return playersInRelationshipWithTheBomb;
	}

	@Override
	public void addPlayerObserver(Player player) {
		playersInRelationshipWithTheBomb.add(player);
	}

	@Override
	public void removePlayerObserver(Player observer) {
		if(playersInRelationshipWithTheBomb.contains(observer)) {
			playersInRelationshipWithTheBomb.remove(observer);		
		}
		return;
	}
	
	public List<Explosion> getTargetCells() {
		return targetCells;
	}

	// Methods that do random stuff	
	public void startBombTimer(long currentSystemTime)
	{
		this.initialUpdate = currentSystemTime;
	}

    public void update(long currentSystemTime)
    {

        if (initialUpdate == -1)
        {
            return;
        }
        
        // passability check
        if (passable && map.closestPlayersAt((int) x, (int) z) > 1.05)
        {
            passable = false;
        }

        long timeDiff = currentSystemTime - initialUpdate;
        double timeDiffSeconds = ((double) timeDiff) / 1000;
        assert (timeDiff >= 0);
        if (timeDiffSeconds >= bombDelay)
        {
            explode();
            initialUpdate = -1;
        }
    }

    // Generate the cells the bomb will have impact on
    private void generateTargetCells()
    {
        
        int bombX = (int) x;
        int bombY = (int) z;
        

        targetCells.add(new Explosion(bombX, bombY, ExplosionType.CROSS));
        boolean upCellContinue = true;
        boolean downCellContinue = true;
        boolean leftCellContinue = true;
        boolean rightCellContinue = true;

        // explosion stops at the first solid block encountered

        for (int i = 1; i <= radius; i++)
        {

            BlockTypes upTypes = map.getCellType(bombX, bombY + i);
            BlockTypes downTypes = map.getCellType(bombX, bombY - i);

            if (upCellContinue)
            {
                targetCells.add(new Explosion(bombX, bombY + i, ExplosionType.VERTICAL));
            }
            if (upTypes != null && upCellContinue
                    && (upTypes == BlockTypes.DES || upTypes == BlockTypes.IND))
            {
                upCellContinue = false;
            }

            if (downCellContinue)
            {
                targetCells.add(new Explosion(bombX, bombY - i, ExplosionType.VERTICAL));
            }
            if (downTypes != null && downCellContinue
                    && (downTypes == BlockTypes.DES || downTypes == BlockTypes.IND))
            {
                downCellContinue = false;
            }
        }

        for (int j = 1; j <= radius; j++)
        {
            BlockTypes leftTypes = map.getCellType(bombX - j, bombY);
            BlockTypes rightTypes = map.getCellType(bombX + j, bombY);

            if (leftCellContinue)
            {
                targetCells.add(new Explosion(bombX - j, bombY, ExplosionType.HORIZONTAL));
            }

            if (leftTypes != null && leftCellContinue
                    && (leftTypes == BlockTypes.DES || leftTypes == BlockTypes.IND))
            {
                leftCellContinue = false;
            }

            if (rightCellContinue)
            {
                targetCells.add(new Explosion(bombX + j, bombY, ExplosionType.HORIZONTAL));

            }

            if (rightTypes != null && rightCellContinue
                    && (rightTypes == BlockTypes.DES || rightTypes == BlockTypes.IND))
            {
                rightCellContinue = false;
            }
        }
    }

    // Explosion has happened, called when the timer delay has expired
    // or a bomb near this bomb has exploded
    @Override
    public void explode()
    {
        // generate the cells that will take some damage
        generateTargetCells();
        
        status = MapObjectStatus.DEAD;
        
        map.removeBomb(bombNumber);

        // notify the map for each cell that takes bomb impact
        if (!targetCells.isEmpty())
        {
            for (Explosion e : targetCells)
            {
                map.notifyExplosion(e);
            }
        }

        // set the status of the bomb to dead,
        // clean the list of targeted cells

        targetCells.clear();
    }

	// If that function is called then a bomb has exploded elsewhere,
	// so this bomb will explode as well regardless of timer if alive
	@Override
	public void notifyMapObjectObserver(int damage) {
		if (status == MapObjectStatus.DEAD) return;
		bombDelay = -1;
		initialUpdate = -1;
		explode();
	}

	@Override
	public int hashCode()
	{
		return 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		return this == obj;
	}

	@Override
	public String toString()
	{
		return "[ " + " " + this.radius + " " + this.getDamage() + " ]";
	}

    public boolean positionEquals(long x, long z)
    {
        return this.x == x && this.z == z;
    }

    public boolean isPassable()
    {
        return passable;
    }

}
