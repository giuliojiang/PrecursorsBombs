package net.precursorsbombs.geometry;

/* 
 * Class representing a position in 2d plane 
 */

public class MapPosition {

	private int x;
	private int y;
	
	public MapPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	// Increments x with the given amount
	public void incXWith(int amount) {
		x = x + amount;
	}
	
	// Decrements x with the given amount
	public void decXWith(int amount) {
		x = x - amount;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	// Increments y with the given amount
	public void incYWith(int amount) {
		y = y + amount;
	}
	
	// Decrements y with the given amount
	public void decYWith(int amount) {
		y = y - amount;
	}
	
	/* 
     * Almost copied from Vec3
     */
    @Override
    public int hashCode()
    {
        return x*883 + y*337;
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
        if (!(obj instanceof MapPosition))
        {
            return false;
        }
        MapPosition other = (MapPosition) obj;
        return this.x == other.x && this.y == other.y;
    }
    
    @Override
    public String toString()
    {
        return "[" + x + "," + y + "]";
    }
	
	
}
