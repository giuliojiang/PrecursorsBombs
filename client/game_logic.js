//geometry utilities

var pi = 3.14159265359;
var radians_rotation = 2*pi;

function normalizeRadians(r)
{
	var multiple = Math.floor(Math.abs(r) / radians_rotation);
	if (r >= 0)
	{
		return r - multiple * radians_rotation;
	} else
	{
		return r + (multiple+1) * radians_rotation;
	}
	return r;
};

function isNumeric(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
};

function Vector3(x, y, z)
{
	this.x = x;
	this.y = y;
	this.z = z;
};

//#############################################################################
//Classes for managing the client-side logic of the game.
//All classes do NOT know about the Babylon library, as they only manage
//the logic without making draw calls.

//Instances of logic classes take as parameters objects for:
//- Network communicator
//- System tools
//This allows to replace these function calls with mock interfaces during
//testing.y
function GameLogic(system)
{
    var self = this;
    
	/*, function () {
	    // Call with the sound is ready to be played (loaded & decoded)
	    // TODO: add your logic
	    console.log("Sound ready to be played!");
	}, { loop: false, autoplay: true });*/
    this.camera = new CameraLogic(system);
    
    this.map = undefined;
    this.mapUpdated = false;
    
    this.players = {};
    this.current_player_id = 'null';
    this.pendingPlayersRemoved = {};
    
    this.allBombs = {};
    this.pendingBombs = {};
    this.pendingBombsRemoved = {};
    
    this.pendingPowerUps = {};
    this.pendingPowerUpsRemoved = {};
    this.allPowerUps = {};
    
    this.allExplosions = {};
    this.pendingExplosions = {};
    
    this.in_game = false;
    
    this.current_player = function()
    {
        return this.players[this.current_player_id];
    }
    
    this.addPlayer = function(player, id)
    {
        this.players[id] = player;
    }
    
    this.getPlayer = function(id)
    {
        return this.players[id];
    }
    
    this.removePlayer = function(id)
    {
        if (id != this.current_player_id)
        {
            this.pendingPlayersRemoved[id] = undefined;
            delete this.players[id];
        }
    }
    
    this.removeAllPlayers = function()
    {
        for (var id in this.players)
        {
            self.removePlayer(id);
        }
    }
    
    this.addBomb = function(number, x, y)
    {
        var bomb = new BombLogic(number, x, y);
        this.pendingBombs[number.toString()] = bomb;
        this.allBombs[number.toString()] = bomb;
    }
    
    
    
    this.removeBomb = function(number)
    {
    	sound_system.playExplosion(0);
        var bomb = this.allBombs[number.toString()];
        delete this.allBombs[number.toString()];
        delete this.pendingBombs[number.toString()];
        this.pendingBombsRemoved[number.toString()] = bomb;

    }
    
    this.addExplosion = function(x, y, shape)
    {
        var explosion = new ExplosionLogic(x, y, shape, system);
        var id = explosion.id;
        var existing_explosion = this.allExplosions[id];
        if (existing_explosion)
        {
            // If there is already an explosion at this location,
            // refresh its timer
            existing_explosion.initial_time = system.now();
        } else
        {
            // Add the new explosion to the list, and start its timer
            explosion.initial_time = system.now();
            this.pendingExplosions[id] = explosion;
            this.allExplosions[id] = explosion;
        }
    }
    
    this.removeExplosion = function(id)
    {
        delete this.allExplosions[id];
        delete this.pendingExplosions[id];
    }
    
    this.addPowerUp = function(x, y, type)
    {
    	console.log("Adding powerup");
    	var powerUp = new PowerUpLogic(x, y, type, system);
    	var id = powerUp.id;
    	this.pendingPowerUps[id] = powerUp;
    	this.allPowerUps[id] = powerUp;

    	
    }
    this.removePowerUp = function(x, y, type)
    {
    	var powerUp = new PowerUpLogic(x, y, type, system);
    	var id = powerUp.id;
    	this.pendingPowerUpsRemoved[id] = powerUp;
    	delete this.allPowerUps[id];
    	delete this.pendingPowerUps[id];
    }
}

function CameraLogic(system)
{
    this.last_update = system.now();
    this.y_rotation = 0;
    
    this.updateRotation = function(new_rotation, id)
    {
        this.y_rotation = new_rotation;
        // update the y_rotation member.
        // If more than 1/30s has passed from last_update, send an update
        // to the network communicator. Otherwise, don't do it.
        
        // send message to server
        var message = 
        {
            'type' : 'player',
            'id': id,
            'player' : 'null',
            'rotation' : this.y_rotation
        }
        var message_json = JSON.stringify(message);
        return message_json;
    };

};

// Player logic
function PlayerLogic(system)
{
	this.maxHealth = 100;
	this.health = 100;
    this.forward = 0;
    this.right = 0;
    this.left = 0;
    this.backward = 0;
    this.along = 0;
    this.sideways = 0;
    this.jump = false;
    this.attack = false;
    this.alpha = 0;
    this.speedBoost = false;
    this.id = 'null';

    this.position = new Vector3(0,0,0);
    
    this.updatePosition = function(x, y, z)
    {
        this.position = new Vector3(x,y,z);
    };
    
    this.getPosition = function()
    {
        return this.position;
    };
    
    this.updateAlpha = function(a)
    {
        this.alpha = a;
    };

    this.getAlpha = function()
    {
    	return this.alpha;
    };


    this.sendActionUpdate = function()
    {
        this.along = this.forward - this.backward;
        this.sideways = this.right - this.left;
        var state = 'moving';
        if (this.along == 0 && this.sideways == 0)
        {
            state = 'idle';
        }
        var full_rotation_radians = 3.141592;
        var rotationoffset = 0;
        
        var directions = [[1/4, 0/4, 7/4], 
                          [2/4, 0/4, 6/4], 
                          [3/4, 4/4, 5/4]];
        
        rotationoffset = directions[1-this.along][this.sideways+1] * full_rotation_radians;
       
        var obj = 
        {
                'type' : 'paction',
                'id' : this.id,
                'state' : state,
                'jump' : this.jump,
                'attack' : this.attack,
                'boost' : this.speedBoost,
                'rotationoffset' : rotationoffset
        };
        this.attack = false;
        var objmsg = JSON.stringify(obj);
        return objmsg;
    }
}

//Bomb logic
function BombLogic(number, x, y)
{
    this.number = number;
    
    this.x = x;
    
    this.y = y;
}

function ExplosionLogic(x, y, shape, system)
{
    this.x = x;
    this.y = y;
    this.shape = shape;
    this.initial_time = undefined;
    
    this.id = 'exp_' + this.x + '_' + this.y + '_' + this.shape;

    this.timeout = 500; // milliseconds
    
    this.finished = function()
    {
        return system.now() - this.initial_time > this.timeout;
    }
}

function PowerUpLogic(x, y, type, system)
{
	this.x = x;
	this.y = y;
	this.type = type;
	this.id = 'powerup_' + this.x + '_' + this.y + '_' + this.type;
	
}
//Map logic

//Block logic
