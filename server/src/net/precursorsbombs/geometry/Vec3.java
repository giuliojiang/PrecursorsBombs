package net.precursorsbombs.geometry;

public class Vec3
{

    private double x;
    private double y;
    private double z;

    public Vec3()
    {
        this.x = 0;
        this.y = 0;
        this.z = 0;

    }

    public Vec3(double x, double y, double z)
    {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void addDeltas(double dx, double dy, double dz)
    {
        x += dx;
        y += dy;
        z += dz;
    }

    /**
     * @return the x
     */
    public double getX()
    {
        return x;
    }

    /**
     * @param x
     *            the x to set
     */
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY()
    {
        return y;
    }

    /**
     * @param y
     *            the y to set
     */
    public void setY(double y)
    {
        this.y = y;
    }

    /**
     * @return the z
     */
    public double getZ()
    {
        return z;
    }

    /**
     * @param z
     *            the z to set
     */
    public void setZ(double z)
    {
        this.z = z;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        if (!(obj instanceof Vec3))
        {
            return false;
        }
        Vec3 other = (Vec3) obj;
        
        double delta = Math.abs(this.x - other.x);
        delta += Math.abs(this.y - other.y);
        delta += Math.abs(this.z - other.z);
        
        return delta < 0.001;
    }
    
    @Override
    public String toString()
    {
        return "[" + x + "," + y + "," + z + "]";
    }
    
    public Vec3 clone()
    {
    	return new Vec3(x, y, z);
    }

    public double distance(int x0, int z0)
    {
        double sum = (x0 - x)*(x0 - x) + (z0 - z)*(z0 - z);
        return Math.sqrt(sum);
    }
    
    

}
