package net.precursorsbombs.serverlogic;

import java.util.Random;

import com.eclipsesource.json.JsonObject;

import net.precursorsbombs.database.BombermanDatabase;
import net.precursorsbombs.geometry.Vec3;

public class Bot extends Player
{
	private final double BOOST_SPEED = 15;
	private final double SPEED = 5;
    private NetworkConnection connection;
    private String id;

    private int health = 100;
    
    private boolean isMoving = false;

    // Time is measured in milliseconds
    private long lastUpdate = -1;
    
    private Map map;
    
    // Position is measured in units
    private Vec3 position = new Vec3(10, 0, 20);
    private double playerSize = 0.4;

    private double yRotation = 0;
    
    private double yRotationOffset = 0;
    private double lastRotationOffset = 0;

    // Speed is measured in units/second
    private double speed = 5;
    private boolean onGround = true;
    private boolean attack = false;
    
    private boolean jump = false;
    private double ySpeed = 0;
    private double gravity = 15;

    private Player player; 
    
    public Bot(NetworkConnection connection, String id, Map map, Player player, BombermanDatabase db)
    {	
        super(connection, id, db);
    	this.player = player;
    	this.map = map;
        this.connection = connection;
        this.id = id;
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

    public Vec3 getPosition()
    {
        return this.position;
    }

    public double getRotationOffset()
    {
        return yRotationOffset;
    }
    
    public void handleMessage(JsonObject messageObject)
    {
        double rotation = messageObject.get("rotation").asDouble();
        updateRotation(rotation);
        update(System.currentTimeMillis());
    }

    private boolean isColliding(double x, double z) {
    	for (double i = -1; i <= 1; i+=2){
        	for (double j = -1; j <= 1; j+=2){
        		if (!map.isPassable(x+i*playerSize, z+j*playerSize)) return true;
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

    @Override
    public String sendUpdate()
    {
        JsonObject messageObject = new JsonObject();
        messageObject.add("type", "player");
        messageObject.add("id", getId());
        Vec3 pos = getPosition();

        messageObject.add("x", pos.getX());
        messageObject.add("y", pos.getY());
        messageObject.add("z", pos.getZ());
        messageObject.add("alpha", lastRotationOffset);
        String msg = messageObject.toString();

        return msg;
    }

    public void setJump()
    {
    	this.jump = true;
    }
    
    public void setAttack()
    {
    	this.attack = true;
    }
    public void setBoost()
    {
    	this.speed = BOOST_SPEED;
    }
    public void stopBoost()
    {
    	this.speed = SPEED;
    }
    
    public void setMoving()
    {
        this.isMoving = true;
    }

    public void setRotationOffset(double ofst)
    {
        this.yRotationOffset = ofst;
        if (isMoving) this.lastRotationOffset = ofst+yRotation;
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public void stopJump()
    {
    	this.jump = false;
    }

    public void stopAttack()
    {
    	this.attack = false;
    }
    public void stopMoving()
    {
        this.isMoving = false;
    }

    public void takeDamage(int damage)
    {
        health -= damage;
        return;
    }


    private void botLogic(long currentSystemTime) {
    	Random r = new Random();
    	int rand = r.nextInt();
    	setMoving();
    	if (rand%10 == 0) {
    		updateRotation(yRotation + 1);
    	} else if (rand%10 == 1) {
    		updateRotation(yRotation - 1);
    	}
    	
    	long timeInSeconds = currentSystemTime/1000;
    	if (timeInSeconds % 5 == 0) setJump();
    	else { stopJump(); }
    	double playerX = player.getPosition().getX();
    	double playerZ = player.getPosition().getZ();
    	double x = position.getX();
    	double z = position.getZ();
    	double distX = (playerX - x);
    	double distZ = playerZ - z;
    	double dist = Math.sqrt(distX*distX+distZ*distZ);
    	if (dist > 10) {
    		double newRotation = Math.atan(distZ/distX);
    		if (distX < 0) newRotation += Math.PI;
    		updateRotation(newRotation);
    	}
    }
    
    @Override
    public void update(long currentSystemTime)
    {   
    	botLogic(currentSystemTime);
    	
    	
    	if (lastUpdate == -1)
        {
            System.out.println("Error, simulation not initialized");
        }

        long timediff = currentSystemTime - lastUpdate;
        double timediffSeconds = ((double) timediff) / 1000;
        assert (timediff >= 0);
        
        if (attack) {
        	double attackX = position.getX() + Math.cos(yRotation) * (playerSize + 0.2);
        	double attackZ = position.getZ() + Math.sin(yRotation) * (playerSize + 0.2);
        	map.destroyAt(attackX, attackZ);
        	attack = false;
        }
        
        if (position.getY() <= 0 || (isColliding(position.getX(), position.getZ()) && position.getY() <= 1.0001)) {
        	onGround = true;
        	ySpeed = 0;
        } else {
        	onGround = false;
        }
        if (jump && onGround) {
        	ySpeed = 8;
        	onGround = false;
        } else if (!onGround){
        	ySpeed -= gravity*timediffSeconds;
        }
        if (position.getY() + ySpeed*timediffSeconds < 0) 
        		 {
        	ySpeed = -position.getY()/timediffSeconds;
        } else if ((position.getY() + ySpeed*timediffSeconds < 1) &&  isColliding(position.getX(), position.getZ())) {
        	ySpeed = (1-position.getY())/timediffSeconds+0.0001;
        }
        if (!onGround)
        	position.addDeltas(0, ySpeed*timediffSeconds, 0);
        else {
        	ySpeed = 0;
        }
        
        if (isMoving)
        {
        	move(timediffSeconds);            
        }
        
        lastUpdate = currentSystemTime;
    }

    private void move(double timediffSeconds){
        double effectiveRotation = yRotation + yRotationOffset;
        Vec3 newPosition = position.clone();
        double deltaX = Math.cos(effectiveRotation) * speed * timediffSeconds;
        double deltaZ = Math.sin(effectiveRotation) * speed * timediffSeconds;
        newPosition.addDeltas(deltaX, 0,
                deltaZ);
        double x = position.getX();
        double z = position.getZ();
        double y = position.getY();
        if (y > 1) position = newPosition;
        else if (!isColliding(x+deltaX, z+deltaZ))
        	position = newPosition;
        else if (!isColliding(x+deltaX, z))
        	position.addDeltas(deltaX,  0, 0);
        else if (!isColliding(x, z+deltaZ))
        	position.addDeltas(0, 0, deltaZ);
    }
    
    public void updateRotation(double rotation)
    {
        this.yRotation = rotation;
        if (isMoving) this.lastRotationOffset = yRotationOffset+yRotation;
    }

}
