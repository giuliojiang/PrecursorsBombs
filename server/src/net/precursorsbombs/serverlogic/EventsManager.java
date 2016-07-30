package net.precursorsbombs.serverlogic;

import java.util.HashMap;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import net.precursorsbombs.match.BombermanMatch;

public class EventsManager implements EventsManagerInterface
{

    private final PlayerManagerInterface playerManager;
    // Keeps track on current guest
    private int currentGuest = 1; 
    private HashMap<String, EventHandler> handlers = new HashMap<>();

    public EventsManager(PlayerManagerInterface playerManager)
    {
        this.playerManager = playerManager;
        
        initHandlers();

    }

    public void initHandlers()
    {
        handlers.put("player", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.handleMessage(messageObject);
                }
            }
            
        });
        
        handlers.put("chatMessage", new EventHandler() {
        	
        	@Override
        	public void handle(NetworkConnection conn, JsonObject messageObject)
        	{
        		String message = messageObject.get("message").asString();
        		Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
        		if (p != null) {
        			p.sendChatMessage(message);
        		}
        	}
        });
        
        handlers.put("paction", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                JsonValue movingValue = messageObject.get("state");
                boolean moving = movingValue.asString().equals("moving");
                boolean jump = messageObject.get("jump") != null && messageObject.get("jump").asBoolean();
                boolean attack = messageObject.get("attack") != null && messageObject.get("attack").asBoolean();
                boolean boost = messageObject.get("boost") != null && messageObject.get("boost").asBoolean();

                JsonValue offsetValue = messageObject.get("rotationoffset");
                double offset = offsetValue.asDouble();

                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.handleActionMessage(moving, offset, jump, attack, boost);
                }
            }
            
        });
        
        
        handlers.put("connect",  new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                JsonValue usernameValue = messageObject.get("username");
                String username = usernameValue.asString();

                JsonValue passwordValue = messageObject.get("password");
                String password = passwordValue.asString();

                // Makes sure each guest has a unique name
                if (username.equals("guest")) {
                    username = username + "_" + currentGuest;
                    currentGuest++;
                    playerManager.connectGuest(conn, username);
                } else
                {
                    // check if this player was already connected
                    Player existing = playerManager.getPlayer(username);

                    playerManager.connectPlayer(conn, username, password);
                }
            }
            
        });
        
        
        handlers.put("mapCreate", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                JsonValue players = messageObject.get("players");
                int numPlayers = players.asInt();

                JsonValue size = messageObject.get("size");
                int mapSize = size.asInt();

                JsonValue levels = messageObject.get("levels");
                int numLevels = levels.asInt();

                MapGenerator gen = new MapGenerator(numPlayers, mapSize, numLevels);
            }
            
        });
        
        
        handlers.put("lobbies", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {

                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.sendInviteLobbies();
                }
            }
            
        });
        
        
        handlers.put("join", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                JsonValue nameValue = messageObject.get("name");
                String name = nameValue.asString();
                
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    BombermanMatch theMatch = playerManager.getMatch(name);

                    p.joinMatch(theMatch);
                }
            }
            
        });
        
        
        handlers.put("exitlobby", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.exitLobby();
                }
            }
            
        });
        
        
        handlers.put("newlobby", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.createLobby();
                }
            }
            
        });
        
        
        handlers.put("startgame", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.startGame();
                }
            }
            
        });
        
        
        handlers.put("invite", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                JsonValue guestValue = messageObject.get("guest");
                String guest = guestValue.asString();
                
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    Player g = playerManager.getPlayer(guest);
                    p.invite(g);
                }
            }
            
        });
        
        
        handlers.put("placebomb", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.placeNewBomb();
                }
            }
            
        });
        
        
        handlers.put("listfriends", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.sendFriendsList();
                    p.sendPendingFriendsList();
                }
            }
            
        });
        
        
        handlers.put("owneditems", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.sendItemList();
                }
            }
            
        });
        
        
        handlers.put("addfriend", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    String friendName = messageObject.getString("friend", "");
                    if (p.addFriend(friendName))
                    {
                        Player f = playerManager.getPlayer(friendName);
                        if (f != null)
                        {
                            f.sendPendingFriendsList();
                        }
                    }
                }
            }
            
        });
        
        handlers.put("registeruser", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                String username = messageObject.getString("username", "");
                String password = messageObject.getString("password", "");
                String email = messageObject.getString("email", "example@example.com");
                
                playerManager.registerUser(conn, username, password, email);
            }
            
        });
        
        
        handlers.put("adduseritem", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    String itemName = messageObject.getString("item", "");
                    p.buyItem(itemName);
                }
            }
            
        });
        
        
        handlers.put("removeuseritem", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    String itemName = messageObject.getString("item", "");
                    p.sellItem(itemName);
                }
            }
            
        });
        
        
        handlers.put("onlinefriends", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.sendOnlineFriendsList(playerManager);
                }
            }
            
        });
        
        
        handlers.put("playerstats", new EventHandler()
        {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = playerManager.getPlayer(messageObject.getString("id", ""));
                if (p != null)
                {
                    p.sendStatsMessage();
                }
            }

        });
        
        handlers.put("topfriends", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.sendTopFriends();
                }
            }
            
        });
        
        
        handlers.put("acceptfriend", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.acceptFriendshipFrom(messageObject.getString("friend", ""));
                    
                    p.sendFriendsList();
                    p.sendPendingFriendsList();
                    
                    Player f = playerManager.getPlayer(messageObject.getString("friend", ""));
                    if (f != null)
                    {
                        f.sendFriendsList();
                        f.sendPendingFriendsList();
                    }
                }
            }
            
        });
        
        
        handlers.put("rejectfriend", new EventHandler() {

            @Override
            public void handle(NetworkConnection conn, JsonObject messageObject)
            {
                Player p = getAndCheckPlayerConn(messageObject, playerManager, conn);
                if (p != null)
                {
                    p.rejectFriendshipFrom(messageObject.getString("friend", ""));
                
                    p.sendFriendsList();
                    p.sendPendingFriendsList();
                    
                    Player f = playerManager.getPlayer(messageObject.getString("friend", ""));
                    if (f != null)
                    {
                        f.sendFriendsList();
                        f.sendPendingFriendsList();
                    }
                }
            }
            
        });
    }
    
    private Player getAndCheckPlayerConn(JsonObject messageObject, PlayerManagerInterface playerManager, NetworkConnection conn)
    {
        JsonValue idValue = messageObject.get("id");
        if (idValue == null)
        {
            idValue = messageObject.get("uid");
        }
        String id = idValue.asString();
        
        
        Player p = playerManager.getPlayer(id);
        if (p != null && p.getConnection().equals(conn))
        {
            return p;
        } else
        {
            return null;
        }
    }

    @Override
    public void newConnection(NetworkConnection conn)
    {
        System.out.println("A new player connected");
    }

    @Override
    public void handleString(NetworkConnection conn, String s)
    {

        JsonValue message = Json.parse(s);
        JsonObject messageObject;
        if (message.isObject())
        {
            messageObject = message.asObject();
        } else
        {
            return;
        }

        String type = messageObject.getString("type", "");
        
        EventHandler h = handlers.get(type);
        if (h != null)
        {
            h.handle(conn, messageObject);
        }

    }

    @Override
    public void closeConnection(NetworkConnection conn)
    {
        System.out.println("Connection closed");
        playerManager.removePlayer(conn);
    }
    
    interface EventHandler
    {
        void handle(NetworkConnection conn, JsonObject messageObject);
    }

}
