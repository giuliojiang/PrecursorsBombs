package net.precursorsbombs.serverlogic;

import net.precursorsbombs.match.BombermanMatch;

public interface PlayerManagerInterface
{

    void connectPlayer(NetworkConnection conn, String username, String password);

    Player getPlayer(String id);

    void removePlayer(NetworkConnection connection);

    // for now the name of the match is simply the name of the player
    // who created it
    BombermanMatch getMatch(String name);

    void connectGuest(NetworkConnection conn, String username);

    void registerUser(NetworkConnection conn, String username, String password, String email);

    public boolean isOnline(String username);


    
}