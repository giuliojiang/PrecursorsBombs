package net.precursorsbombs.database;

import java.util.List;

public interface BombermanDatabase
{

    public int getUserId(String username);

    public boolean checkLogin(String username, String password);

    public void registerUser(String username, String password,
            String email, String playername, String guildname);

    public void addFriend(String username1, String username2);

    public List<String> getFriendsOf(String username);

    public int getItemId(String itemType);

    public List<String> getItemsOfUser(String username);

    public void addItemToUser(String username, String itemType);

    public void removeItemFromUser(String username, String itemType);
    
    public void newItem(String itemType);
    
    public PlayerStatistics getPlayerStatistics(String username);
    
    public List<PlayerWins> getFriendsOrderedByWins(String username);
    
    public void incrementGamesPlayed(String username);
    
    public void incrementWins(String username);

    public void giveExp(String username, int place);

    public boolean friendRequest(String requester, String newfriend);
    
    public boolean acceptFriendRequest(String receiver, String requester);
    
    public boolean rejectFriendRequest(String receiver, String requester);
    
    public List<String> getFriendRequests(String receiver);
    
    public void removeUser(String username);

}
