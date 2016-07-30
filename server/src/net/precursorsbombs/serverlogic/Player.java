package net.precursorsbombs.serverlogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import net.precursorsbombs.database.BombermanDatabase;
import net.precursorsbombs.database.PlayerStatistics;
import net.precursorsbombs.database.PlayerWins;
import net.precursorsbombs.geometry.Vec3;
import net.precursorsbombs.match.BombermanMatch;
import net.precursorsbombs.testsuite.TestUtils;

public class Player
{
    private static final int INITIAL_HEALTH = 100;
    private static final double INITIAL_SPEED = 3;
    private static final int INITIAL_BOMBS_AVAILABLE = 0;
    private static final int INITIAL_MAX_BOMBS = 1;
    private static final int INITIAL_BOMB_REGENERATION_INTERVAL = 3000;
    private static final int INITIAL_BOMB_RADIUS = 1;
    
    /*
     *     private int bombsAvailable = 0;
    private int maxBombs = 1;
    private long bombRegenerationInterval = 3000;
    private long lastBombRegeneration = -1;
    private int bombRadius = 1;
     */
    private NetworkConnection connection;
    private String id;

    private int health = INITIAL_HEALTH;
    private int maxHealth = INITIAL_HEALTH;

    private boolean isMoving = false;

    // Time is measured in milliseconds
    private long lastUpdate = -1;

    private Map map;

    // Position is measured in units
    private double default_speed = 3;
    private Vec3 position = new Vec3(20, 0, 20);
    private double playerSize = 0.4;

    private double yRotation = 0;

    private double yRotationOffset = 0;
    private double lastRotationOffset = 0;

    // Speed is measured in units/second
    private double speed = 3;
    private boolean onGround = true;
    private boolean attack = false;

    private boolean jump = false;
    private double ySpeed = 0;
    private double gravity = 15;
    private double jumpHeight = 4; 
    
    private int bombsAvailable = 0;
    private int maxBombs = 1;
    private long bombRegenerationInterval = 3000;
    private long lastBombRegeneration = -1;
    private int bombRadius = 1;

    private int place = 1; // the player is the place th person to die (incremented after each death)

    // The lobby/match the player is in
    private BombermanMatch match = null;

    // List of lobbies the player has been invited to
    private HashSet<BombermanMatch> inviteList = new HashSet<>();
    
    private BombermanDatabase db;

    public Player(NetworkConnection connection, String id, BombermanDatabase db)
    {
        this.connection = connection;
        this.id = id;
        this.db = db;
    }

    public void alert(String s)
    {
        JsonObject msgobj = new JsonObject();
        msgobj.add("type", "alert");
        msgobj.add("msg", s);
        sendMessage(msgobj.toString());
    }

  public void incrementPlace(){
    this.place++;
  }

    public void boostJumpHeight() {
    	jumpHeight = 8;
    }
    /**
     * @return the connection
     */
    public NetworkConnection getConnection()
    {
        return connection;
    }

    public String getId()
    {
        return id;
    }

    public BombermanMatch getMatch()
    {
        return match;
    }

    public Vec3 getPosition()
    {
        return this.position;
    }

    public double getDefaultSpeed() {
    	return this.default_speed;
    }
    public double getSpeed() 
    {
    	return this.speed;
    }
    public double getRotationOffset()
    {
        return yRotationOffset;
    }
    
    public void handleActionMessage(boolean moving, double offset, boolean jump, boolean attack, boolean boost)
    {
        if (moving)
        {
            setMoving();
        } else
        {
            stopMoving();
        }
        if (attack)
        {
            setAttack();
        } else
        {
            stopAttack();
        }
        if (jump)
        {
            setJump();
        } else
        {
            stopJump();
        }
        if (boost)
        {
            setBoost();
        } else
        {
            stopBoost();
        }
        setRotationOffset(offset);

        update(System.currentTimeMillis());
    }


  public void handleMessage(JsonObject messageObject)
    {
        if (match == null || !match.isStarted())
        {
            return;
        }
        double rotation = messageObject.get("rotation").asDouble();
        updateRotation(rotation);
        update(System.currentTimeMillis());
    }

    private boolean isColliding(double x, double z)
    {
        for (double i = -1; i <= 1; i += 2)
        {
            for (double j = -1; j <= 1; j += 2)
            {
                if (!map.isPassable(x + i * playerSize, z + j * playerSize)
                        || map.isBorder(x + i * playerSize, z + j * playerSize))
                {
                    return true;
                }

            }
        }
        return false;
    }

    public boolean isMoving()
    {
        return isMoving;
    }

    public void sendMessage(String msg)
    {
        connection.send(msg);
    }

    public String sendUpdate()
    {
        if (isAlive())
        {
            JsonObject messageObject = new JsonObject();
            messageObject.add("type", "player");
            messageObject.add("id", getId());
            Vec3 pos = getPosition();
    
            messageObject.add("x", pos.getX());
            messageObject.add("y", pos.getY());
            messageObject.add("z", pos.getZ());
            messageObject.add("alpha", lastRotationOffset);
            messageObject.add("health", this.health);
            messageObject.add("maxHealth", this.maxHealth);
            String msg = messageObject.toString();
    
            return msg;
        } else
        {
            return "{}";
        }
    }

    public void setAttack()
    {
        this.attack = true;
    }

    public void setJump()
    {
        this.jump = true;
    }

    public void incrementMaxBombs() 
    {
    	this.maxBombs++;
    	this.bombRegenerationInterval = this.bombRegenerationInterval * 80 / 100;
    }
    public void increaseBombRadius()
    {
    	this.bombRadius++;
    }
    
    public void setBoost()
    {
        this.speed = default_speed * 2;
    }

    public void stopBoost()
    {
        this.speed = default_speed;
    }

    public void setMoving()
    {
        this.isMoving = true;
    }

    public void setRotationOffset(double ofst)
    {
        this.yRotationOffset = ofst;
        if (isMoving)
            this.lastRotationOffset = ofst + yRotation;
    }

    //not sure if used
    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public void setDefaultSpeed(double speed)
    {
    	this.default_speed = speed;
        this.speed = speed;
    }
    
    public void startSimulation(long currentSystemTime, Map map)
    {
        db.incrementGamesPlayed(this.id);
        
        this.lastUpdate = currentSystemTime;
        this.map = map;
        
        this.position = map.getAStartingPosition();
        
        // send message that the game has started
        JsonObject obj = new JsonObject();
        obj.add("type", "initgame");
        sendMessage(obj.toString());
    }

    public void stopAttack()
    {
        this.attack = false;
    }

    public void stopJump()
    {
        this.jump = false;
    }

    public void stopMoving()
    {
        this.isMoving = false;
    }

    public void takeDamage(int damage)
    {
        health -= damage;
        
        // if player's now dead, send death message
        if (health <= 0)
        {
          giveExp();
          sendDeathMessage();
        }
        
        return;
    }

    public void giveExp()
    {
      db.giveExp(this.id, this.place);
    }

    public void sendDeathMessage()
    {
        if (map != null)
        {
            JsonObject obj = new JsonObject();
            obj.add("type", "death");
            obj.add("id", this.id);
            map.broadcastMessage(obj.toString());
            map.addDeadPlayer(this);
        }
    }

    public void sendChatMessage(String message)
    {
    	JsonObject obj = new JsonObject();
    	obj.add("type", "chatMessage");
    	obj.add("id", this.id);
    	obj.add("message", message);
    	map.broadcastMessage(obj.toString());
    }
    public void update(long currentSystemTime)
    {
        if (lastUpdate == -1)
        {
            System.out.println("Error, simulation not initialized");
            return;
        }
        
        // If player is dead, do nothing
        if (health <= 0)
        {
            return;
        }

        long timediff = currentSystemTime - lastUpdate;
        double timediffSeconds = ((double) timediff) / 1000;
        assert (timediff >= 0);

        if (attack)
        {
            double attackX = position.getX() + Math.cos(yRotation) * (playerSize + 0.2);
            double attackZ = position.getZ() + Math.sin(yRotation) * (playerSize + 0.2);
            map.destroyAt(attackX, attackZ);
            attack = false;
        }

        if (position.getY() <= 0 || (isColliding(position.getX(), position.getZ()) && position.getY() <= 1.0001))
        {
            onGround = true;
            ySpeed = 0;
        } else
        {
            onGround = false;
        }

        if (jump && onGround)
        {
            ySpeed = jumpHeight;
            onGround = false;
        } else if (!onGround)
        {
            ySpeed -= gravity * timediffSeconds;
        }

        if (position.getY() + ySpeed * timediffSeconds < 0)
        {
            ySpeed = -position.getY() / timediffSeconds;
        } else if ((position.getY() + ySpeed * timediffSeconds < 1) && isColliding(position.getX(), position.getZ()))
        {
            ySpeed = (1 - position.getY()) / timediffSeconds + 0.0001;
        }

        if (!onGround)
            position.addDeltas(0, ySpeed * timediffSeconds, 0);
        else
        {
            ySpeed = 0;
        }
        map.isPowerUp(position.getX(), position.getZ(), this);
        if (isMoving)
        {
            move(timediffSeconds);
        }

        lastUpdate = currentSystemTime;

        // update bomb regeneration
        if (currentSystemTime - lastBombRegeneration > bombRegenerationInterval)
        {
            if (bombsAvailable < maxBombs)
            {
                bombsAvailable++;
                lastBombRegeneration = currentSystemTime;
            }
        }
    }

    private void move(double timediffSeconds)
    {
    	final double THRESHOLD = 1;
    	
    	
        double effectiveRotation = yRotation + yRotationOffset;
        Vec3 newPosition = position.clone();
        double deltaX = Math.cos(effectiveRotation) * speed * timediffSeconds;
        double deltaZ = Math.sin(effectiveRotation) * speed * timediffSeconds;
        double zmod = deltaZ > 0 ? 1 : -1;
        double xmod = deltaX > 0 ? 1 : -1;
        newPosition.addDeltas(deltaX, 0, deltaZ);
        double x = position.getX();
        double z = position.getZ();
        double y = position.getY();
        if (y > 1 && !isOutsideBoundary(x + deltaX, z + deltaZ))
        {
            position = newPosition;
        } else if (y > 1 && !isOutsideBoundary(x + deltaX, z))
        {
            position.addDeltas(deltaX, 0, 0);
        } else if (y > 1 && !isOutsideBoundary(x, z + deltaZ))
        {
            position.addDeltas(0, 0, deltaZ);
        } else if (!isColliding(x + deltaX, z + deltaZ))
        {
            position = newPosition;
        } else if (!isColliding(x + deltaX, z))
        {
        	boolean open = false;
        	if (Math.abs(Math.sin(effectiveRotation)) > Math.sqrt(2)/2)
        	{
	        	for (double i = 0.01; i < THRESHOLD; i += 0.01) {
	        		if (!isColliding(x-xmod*i, z+deltaZ)) {
	        			xmod = -1;
	        			open = true;
	        			break;
	        		}
	        		if (!isColliding(x+xmod*i, z+deltaZ)) {
	        			xmod = 1;
	        			open = true;
	        			break;
	        		}
	        	}
	        	if (!open) xmod = 1;
        	}
        	else xmod = 1;
        	if (open && !isColliding(x + Math.sqrt(2)/2*speed*timediffSeconds*(Math.abs(deltaX)/deltaX)*xmod, z))
        			position.addDeltas(Math.sqrt(2)/2*speed*timediffSeconds*(Math.abs(deltaX)/deltaX)*xmod, 0, 0);
        	else {
        		position.addDeltas(deltaX*xmod, 0, 0);
        	}
        } else if (!isColliding(x, z + deltaZ))
        {
        	boolean open = false;
        	if (Math.abs(Math.cos(effectiveRotation)) > Math.sqrt(2)/2)
        	{
        		
	        	for (double i = 0.01; i < THRESHOLD; i += 0.01) {
	        		if (!isColliding(x+deltaX, z-zmod*i)) {
	        			zmod = -1;
	        			open = true;
	        			break;
	        		}
	        		if (!isColliding(x+deltaX, z+zmod*i)) {
	        			zmod = 1;
	        			open = true;
	        			break;
	        		}
	        	}
	        	if (!open) zmod = 1;
        	}
        	else zmod = 1;
        	if (open && !isColliding(x, z + Math.sqrt(2)/2*speed*timediffSeconds*(Math.abs(deltaZ)/deltaZ)*zmod)) {
        		position.addDeltas(0, 0, Math.sqrt(2)/2*speed*timediffSeconds*(Math.abs(deltaZ)/deltaZ)*zmod);
        	} else {
        		position.addDeltas(0, 0, deltaZ * zmod);
        	}
        }      
    }

    private boolean isOutsideBoundary(double x, double z)
    {
        for (double i = -1; i <= 1; i += 2)
        {
            for (double j = -1; j <= 1; j += 2)
            {
                if (map.isBorder(x + i * playerSize, z + j * playerSize))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateRotation(double rotation)
    {
        this.yRotation = rotation;
        if (isMoving)
            this.lastRotationOffset = yRotationOffset + yRotation;
    }

    public synchronized void sendInviteLobbies()
    {
        JsonObject msgobj = new JsonObject();

        if (match == null) // the player is not in a match yet
        {
            msgobj.add("type", "lobbies");

            JsonArray list = new JsonArray();
            for (BombermanMatch i : inviteList)
            {
                list.add(i.getLobbyName());
            }
            msgobj.add("list", list);

            String msg = msgobj.toString();
            connection.send(msg);
        }
    }

    public synchronized void joinMatch(BombermanMatch theMatch)
    {
        if (match == null)
        {
            boolean removed = this.inviteList.remove(theMatch);
            
            if (!removed)
            {
                // It's not allowed to join a match you
                // were not invited to
                return;
            }
            
            this.sendInviteLobbies();
            
            this.match = theMatch;

            this.match.addPlayer(this);

            match.sendUpdate();
        }
    }

    public synchronized void createLobby()
    {
        match = new BombermanMatch(new BasicMap(), this);

        // send a "inlobby" message
        match.sendUpdate();
    }

    public void startGame()
    {
        System.out.println("Player: Starting a game");
        if (match != null && match.isOwner(this))
        {
            match.startGame();
        }
    }

    public void destroy()
    {
        // free resources, remove itself from the match
        if (match != null)
        {
            match.remove(this);
        }
    }

    public void invite(Player g)
    {
        if (g == null)
        {
            return;
        }
        
        if (g.getId().equals(this.id))
        {
            return;
        }

        if (match != null && match.isOwner(this) && isFriend(g))
        {
            g.addToInviteList(match);
        }

        g.sendInviteLobbies();
    }

    private boolean isFriend(Player g)
    {

        if (g.isGuest() || this.isGuest())
        {
            return true;
        }
        
        // check database
        List<String> friendList = db.getFriendsOf(this.id);
        for (String f : friendList)
        {
            if (f.equals(g.id))
            {
                return true;
            }
        }
        
        return false;
    }

    public boolean isGuest()
    {
        return id != null && id.length() > 5 && id.substring(0, 6).equals("guest_");
    }

    private void addToInviteList(BombermanMatch m)
    {
        inviteList.add(m);
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        return this.id.equals(other.id);
    }

    public void placeNewBomb()
    {
        // cannot place a bomb when player is in the air
        if (position.getY() > 1d)
        {
            return;
        }

        long x = Math.round(position.getX());
        long z = Math.round(position.getZ());
        
        // No bomb is placed if there is already a bomb here
        if (map.hasBombHere(x, z))
        {
            return;
        }
        
        // Decrement the number of bombs available
        if (bombsAvailable == 0)
        {
            return;
        }
        bombsAvailable--;

        // This can be modified to pass in the type of bomb to be placed

        map.placeBombAt(x, z, bombRadius);
    }

    public void checkExplosionAt(int x, int y)
    {
        long x0 = Math.round(position.getX());
        long y0 = Math.round(position.getZ());
        
        if (x0 == x && y0 == y)
        {
            takeDamage(50); // TODO damage can be changed
        }
    }

    public boolean isAlive()
    {
        return health > 0;
    }

    public void resetStats(){
    	resetHealth();
    	resetSpeed();
    	resetBombs();
    }
    private void resetBombs() {
        bombsAvailable = INITIAL_BOMBS_AVAILABLE;
        maxBombs = INITIAL_MAX_BOMBS;
        bombRegenerationInterval = INITIAL_BOMB_REGENERATION_INTERVAL;
        lastBombRegeneration = -1;
        bombRadius = INITIAL_BOMB_RADIUS;
    }
    private void resetHealth()
    {
        health = INITIAL_HEALTH;
        maxHealth = INITIAL_HEALTH;
    }
    private void resetSpeed(){
    	default_speed = INITIAL_SPEED;
    	speed = INITIAL_SPEED;
    }
    
    
    
    public boolean isAt(int x0, int z0)
    {
        int x = (int) Math.round(position.getX());
        int z = (int) Math.round(position.getZ());
        return x == x0 && z == z0;
    }

    public void sendFriendsList()
    {
        // get list of friends
        List<String> friends = db.getFriendsOf(this.id);
        
        // Make the json object to send
        JsonObject obj = new JsonObject();
        obj.add("type", "listfriends");
        
        JsonArray list = new JsonArray();
        for (String f : friends)
        {
            list.add(f);
        };
        obj.add("friends", list);
        
        sendMessage(obj.toString());
    }

    public void sendItemList()
    {
        // get list of items
        List<String> items = db.getItemsOfUser(this.id);
        
        // make the json object to send
        JsonObject obj = new JsonObject();
        obj.add("type", "owneditems");
        
        JsonArray list = new JsonArray();
        for (String i : items)
        {
            list.add(i);
        }
        obj.add("items", list);
        
        sendMessage(obj.toString());
    }

    // send a friend request to friendName
    public boolean addFriend(String friendName)
    {
        if (friendName == null)
        {
            TestUtils.log("Friendname is null");
            return false;
        }
        
        if (friendName.equals(this.id))
        {
            alert("You can't add yourself as a friend");
            TestUtils.log("You can't add yourself as a friend");
            return false;
        }
        
        if (friendName.length() >= 5 && friendName.substring(0, 5).equals("guest"))
        {
            alert("You don't need to add a guest as friend in order to play");
            TestUtils.log("You don't need to add a guest as friend in order to play");
            return false;
        }
        
        if (db.getFriendsOf(this.id).contains(friendName))
        {
            alert("You are already friends");
            TestUtils.log("You are already friends");
            return false;
        }
        
        boolean code = db.friendRequest(this.id, friendName);
        if (!code)
        {
            TestUtils.log("Could not send request from "+ this.id +" to "+ friendName +"");
            return false;
        }
        
        sendFriendsList();
        
        sendPendingFriendsList();
        
        alert("Request sent");
        
        return true;
        
    }

    public void sendPendingFriendsList()
    {
        // get the list of pending friends
        List<String> pendingFriends = db.getFriendRequests(this.id);
        
        // put in json object
        JsonObject obj = new JsonObject();
        obj.add("type", "pendingfriends");
        
        JsonArray list = new JsonArray();
        for (String f : pendingFriends)
        {
            list.add(f);
        }
        obj.add("friends", list);
        
        sendMessage(obj.toString());
    }

    public void buyItem(String itemName)
    {
        // TODO money! remove money for price of item
        
        db.addItemToUser(this.id, itemName);
        
        sendItemList();
    }

    public void sellItem(String itemName)
    {
        // TODO Money! add to account
        
        db.removeItemFromUser(this.id, itemName);
        
        sendItemList();
    }

    public void sendOnlineFriendsList(PlayerManagerInterface playerManager)
    {
        // get list of friends
        List<String> allFriends = db.getFriendsOf(this.id);
        
        // add to results the friends that are online
        List<String> onlineFriends = new ArrayList<>();
        for (String f : allFriends)
        {
            if (playerManager.isOnline(f))
            {
                onlineFriends.add(f);
            }
        }
        
        // make message
        JsonObject obj = new JsonObject();
        obj.add("type", "onlinefriends");
        
        JsonArray list = new JsonArray();
        for (String f : onlineFriends)
        {
            list.add(f);
        }
        obj.add("friends", list);
        
        sendMessage(obj.toString());
        
    }

    public void sendTopFriends()
    {
        List<PlayerWins> list = db.getFriendsOrderedByWins(this.id);

        JsonObject obj = new JsonObject();
        obj.add("type", "topfriends");
        JsonArray topFriendsArray = new JsonArray();
        // get only the first three
        for (int i = 1; i <= 3; i++)
        {
          JsonObject friends = new JsonObject();
          int j = i - 1;
            if (j < list.size())
            {
                PlayerWins pw = list.get(j);
                friends.add("username", pw.username);
                friends.add("wins", pw.wins);
            } else
            {
                friends.add("username", "");
                friends.add("wins", 0);
            }
          topFriendsArray.add(friends);
        }

        obj.add("rivals", topFriendsArray);
        
        sendMessage(obj.toString());
    }

    public void incrementWins()
    {
        db.incrementWins(this.id);
    }

    public void sendInitStats(PlayerManagerInterface playerManager)
    {

        // Send player's own statistics
        sendStatsMessage();
        
        // Send player's data and information
        sendFriendsList();
        sendItemList();
        sendOnlineFriendsList(playerManager);
        sendTopFriends();
        sendPendingFriendsList();
    }

    public void sendStatsMessage()
    {
        PlayerStatistics stats = db.getPlayerStatistics(this.id);
        
        JsonObject obj = new JsonObject();
        obj.add("type", "playerstats");
        obj.add("player", id);
        obj.add("experience", stats.getExperience());
        obj.add("lastgameplayed", "0,0,0");
        obj.add("wins", stats.getWins());
        obj.add("totalgames", stats.getTotalgames());
        sendMessage(obj.toString());
    }

    public void acceptFriendshipFrom(String friend)
    {
        if (friend == null)
        {
            return;
        }
        
        String receiver = this.id;
        String requester = friend;
        
        db.acceptFriendRequest(receiver, requester);

    }

    public void rejectFriendshipFrom(String friend)
    {
        if (friend == null)
        {
            return;
        }
        
        String receiver = this.id;
        String requester = friend;
        
        db.rejectFriendRequest(receiver, requester);
    }

    public void exitLobby()
    {
        System.out.println(this.id + " exiting lobby");
        BombermanMatch match = getMatch();
        if (match == null)
        {
            return;
        }
        
        if (match.isOwner(this))
        {
            // remove everyone from match
            match.removeAll(this);
            
            this.match = null;
            this.map = null;
            
            this.sendInviteLobbies();
        } else
        {
        
            // exit from match
            match.remove(this);
            
            this.match = null;
            this.map = null;
            
            this.sendInviteLobbies();
    
            match.sendUpdate();
            
        }
        

    }

    

}
