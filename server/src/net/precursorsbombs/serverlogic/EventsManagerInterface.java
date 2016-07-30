package net.precursorsbombs.serverlogic;

public interface EventsManagerInterface
{

    public void newConnection(NetworkConnection conn);
    
    public void handleString(NetworkConnection conn, String s);
    
    public void closeConnection(NetworkConnection conn);

}
