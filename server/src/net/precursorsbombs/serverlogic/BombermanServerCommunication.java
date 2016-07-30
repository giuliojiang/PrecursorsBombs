package net.precursorsbombs.serverlogic;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class BombermanServerCommunication extends WebSocketServer implements Runnable
{

    private final EventsManagerInterface eventsManager;

    public BombermanServerCommunication(int listenPort, EventsManagerInterface eventsManager)
            throws UnknownHostException
    {
        super(new InetSocketAddress(listenPort));
        this.eventsManager = eventsManager;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        eventsManager.newConnection(new WebSocketNetworkConnection(conn));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        eventsManager.closeConnection(new WebSocketNetworkConnection(conn));
    }

    @Override
    public void onMessage(WebSocket conn, String message)
    {
        try
        {
            eventsManager.handleString(new WebSocketNetworkConnection(conn), message);
        } catch (Exception e)
        {
            System.out.println("An exception has occurred:");
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        System.out.println(ex.getMessage());
    }

}
