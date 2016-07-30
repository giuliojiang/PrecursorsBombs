package net.precursorsbombs.serverlogic;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

public class WebSocketNetworkConnection implements NetworkConnection
{

    private final WebSocket ws;

    public WebSocketNetworkConnection(WebSocket ws)
    {
        this.ws = ws;
    }

    @Override
    public void send(String message)
    {
        try {
            ws.send(message);
        } catch (WebsocketNotConnectedException e)
        {
            System.out.println("Connection was closed...");
        }
    }


    @Override
    public int hashCode()
    {
        return 0;
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
        if (!(obj instanceof WebSocketNetworkConnection))
        {
            return false;
        }
        WebSocketNetworkConnection other = (WebSocketNetworkConnection) obj;
        return this.ws == other.ws;
    }
    

}
