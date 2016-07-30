package net.precursorsbombs.serverlogic.mapObjects;

import com.eclipsesource.json.JsonObject;

public class Explosion
{
    private int x;
    private int y;
    private ExplosionType type;
    
    public Explosion(int x, int z, ExplosionType type)
    {
        this.x = x;
        this.y = z;
        this.type = type;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public String getMessage()
    {
        JsonObject obj = new JsonObject();
        obj.add("type", "exp");
        obj.add("x", x);
        obj.add("y", y);
        obj.add("shape", type.getSymbol());
        return obj.toString();
    }
}
