package net.precursorsbombs.serverlogic.mapObjects;

public enum ExplosionType
{
    HORIZONTAL("-"), VERTICAL("|"), CROSS("+");
    
    private String symbol;

    ExplosionType(String symbol)
    {
        this.symbol = symbol;
    }
    
    public String getSymbol()
    {
        return symbol;
    }
}
