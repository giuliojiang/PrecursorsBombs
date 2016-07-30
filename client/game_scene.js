// This file contains the game scene setup, using the BABYLON JS
// 3D rendering engine. Sets up the 3D environment, camera, and holds the
// 3D scene objects.

// Get the canvas element from HTML
//var canvas = document.getElementById("renderCanvas");

// Load Babylon 3D engine
//var engine = new BABYLON.Engine(canvas, true);

// Contains the scene objects
function SceneObjects()
{
	this.frames = 0;
	
//    this.PLAYER_MODEL;
    this.objects = {};
    this.mapCreated = undefined;

    this.addLight = function () {
    	 // create a light
        var light = new BABYLON.HemisphericLight("light1", new BABYLON.Vector3(0, 1, 0), this.scene);
        
        // change light intensity
        light.intensity = 0.5;


        var sun = new BABYLON.DirectionalLight("sun_Light", new BABYLON.Vector3(0, -0.5, 0.5), this.scene);
        sun.position = new BABYLON.Vector3(0.1, 100, -100);
        sun.intensity = 0.4;
        sun.diffuse = BABYLON.Color3.FromInts(242,202,39);
        this.mapCreated = true;
    }

    
	function parseMap(map){
        var m = buildArray(map.size);
        
        for (var key in map.map) {
            
            var loc = key.split(",");
            var type = map.map[key];
            m[loc[0]][loc[1]] = type;
        }
        return m; 
    };
    
    
    
    this.setScene = function(scene)
    {
        this.scene = scene;
    }
    
    this.getObject = function(name)
    {
        return this.objects[name];
    }
    
    this.addPlayer = function(id)
    {
        //var sphere = BABYLON.Mesh.CreateSphere('player_' + id, 20, 0.8, this.scene);

        var object_name = 'player_' + id;
        this.objects[object_name] = new Pawn(scene);
    }
    
    this.getPlayer = function(id)
    {
        var object_name = 'player_' + id;
        return this.objects[object_name];
    }
    
    this.removePlayer = function(id)
    {
        var object_name = 'player_' + id;
        var obj = this.objects[object_name];
        if (obj)
        {
            obj.dispose();
            
            if (obj.label)
            {
                obj.label.dispose();
            }
            if (obj.healthBar)
            {
                obj.healthBar.dispose();
            }
            if (obj.fullHealthBar)
            {
                obj.fullHealthBar.dispose();
            }
            if (obj.currentHealthBar)
            {
                obj.currentHealthBar.dispose();
            }
            
        }

        delete this.objects[object_name];
    }

    this.addBomb = function(id, x, y)
    {
        var bid = 'bomb_' + id;
        var sphere = BOMB.clone(BOMB.name);//BABYLON.Mesh.CreateSphere(bid, 20, 0.8, this.scene);
        sphere.isVisible = true;
        sphere.position.x = -x;
        sphere.position.z = -y;
        sphere.position.y = 0;
        var animation = new BABYLON.Animation("bombAnimation", "scaling", 80,
        BABYLON.Animation.ANIMATIONTYPE_VECTOR3, BABYLON.Animation.ANIMATIONLOOPMODE_RELATIVE);
        var keys = [];
        keys.push({ frame: 0, value: sphere.scaling});
        keys.push({ frame: 15, value: new BABYLON.Vector3(sphere.scaling.x*1.2, sphere.scaling.y*0.7, sphere.scaling.z*1.2)});
        keys.push({ frame: 30, value: sphere.scaling});
        keys.push({ frame: 45, value: new BABYLON.Vector3(sphere.scaling.x*0.9, sphere.scaling.y*1.23, sphere.scaling.z*0.9)});
        keys.push({ frame: 60, value: sphere.scaling});
        animation.setKeys(keys);
        sphere.animations.push(animation);
        scene.beginAnimation(sphere, 0, 60, true);
        this.objects[bid] = sphere;
    }
    
    this.removeBomb = function(id)
    {
        var bid = 'bomb_' + id;
        var sphere = this.objects[bid];
        if (sphere)
        {
            sphere.dispose();
        }
        delete this.objects[bid];
    }
    
    this.addExplosion = function(id, x, y, shape)
    {
        var sphere = BABYLON.Mesh.CreateSphere(id, 0, 0, this.scene);
        sphere.isVisible = false;
        sphere.position.x = -x;
        sphere.position.z = -y;
        sphere.position.y = 0;

        var particles = setupParticles(scene);
        particles.emitter = sphere;

        var minX = new BABYLON.Vector3(-0.5, 0, 0);
        var minZ = new BABYLON.Vector3(0, 0, -0.5);
        var maxX = new BABYLON.Vector3(0.5, 0, 0);
        var maxZ = new BABYLON.Vector3(0, 0, 0.5); 


        // TODO different shapes according to explosion shape
        switch(shape){
        // shape == '-' means a horizontal explosion (X axis)
          case '-':
            console.log("one");
            particles.minEmitBox = minX;
            particles.maxEmitBox = maxX;
            break;
        // shape == '|' means a vertical explosion (Z axis)
          case '|':
            console.log("two");
            particles.minEmitBox = minZ;
            particles.maxEmitBox = maxZ;
  //          particles.direction1 = new BABYLON.Vector3(0, 0, -1);
            break;
        // shape == '+' means both axis, a cross shape
          case '+':
            console.log("three");
            particles.minEmitBox = minX
            particles.maxEmitBox = maxX;

            var part = setupParticles(scene);

            part.minEmitBox = minZ;
            part.maxEmitBox = maxZ;
            part.emitter = sphere;

            particles.emitter = sphere;
            part.start();
    //        particles.direction1 = new BABYLON.Vector3(-1, 0, -1);
            break;
        }
        particles.start();


        this.objects[id] = sphere;
    }
    
    this.removeExplosion = function(id)
    {
        var sphere = this.objects[id];
        if (sphere)
        {
            sphere.dispose();
        }
        delete this.objects[id];
    }
    
    this.addPowerUp = function(id, x, y, type) {
    	var powerUp = BABYLON.Mesh.CreateBox(id, 0.7, scene);
    	this.objects[id] = powerUp;
        powerUp.scaling.x = 0.1;
        powerUp.scaling.z = 0.8;
        powerUp.position.x = -x;
        powerUp.position.y = 0;
        powerUp.position.z = -y;
        var material = new BABYLON.StandardMaterial("powerUpColour", scene);
        powerUp.material = material;
        powerUp.material.diffuseColor = new BABYLON.Color3(0, 0, 1);
        if (type == "MaxBombs") powerUp.material.diffuseColor = new BABYLON.Color3(1.0, 1.0, 0);
        else if (type == "BombRadius") powerUp.material.diffuseColor = new BABYLON.Color3(1.0, 0, 0);
        
        console.log("i = " + x + ", j = " + y);
        var animation = new BABYLON.Animation("rotationAnimation", "rotation.y", 60,
        BABYLON.Animation.ANIMATIONTYPE_FLOAT, BABYLON.Animation.ANIMATIONLOOPMODE_RELATIVE);
        var keys = [];
        keys.push({ frame: 0, value: 0});
        keys.push({ frame: 120, value: radians_rotation});
        animation.setKeys(keys);
        powerUp.animations.push(animation);
        scene.beginAnimation(powerUp, -1, 3, true);
    }
    this.removePowerUp = function(id) {
    	var powerUp = this.objects[id];
    	if (powerUp)
		{
    		powerUp.dispose();
		}
    	delete this.objects[id];
    }
    
	function parseMap(map){
        var m = buildArray(map.size);
        
        for (var key in map.map) {
            
            var loc = key.split(",");
            var type = map.map[key];
            m[loc[0]][loc[1]] = type;
        }
        return m; 
    };
    
    this.updateMap = function(map)
    {
        
        console.log('in addmap');
        
        var m = parseMap(map);
        
        var indestructable_mat = new BABYLON.StandardMaterial("metal", this.scene);
        
        var indestructable_texture = new BABYLON.Texture("assets/metal.jpg", this.scene);
        indestructable_texture.uScale = indestructable_texture.vScale = 0.5;
        indestructable_mat.diffuseTexture = indestructable_texture;
        indestructable_mat.specularColor = BABYLON.Color3.FromInts(200,200,200);
        
        var destructable_mat = new BABYLON.StandardMaterial("brick", this.scene);
        
        var destructable_texture = new BABYLON.Texture("assets/bricks.png", this.scene);
        destructable_texture.uScale = destructable_texture.vScale = 0.5;
        destructable_mat.diffuseTexture = destructable_texture;
        destructable_mat.specularColor = BABYLON.Color3.FromInts(100,100,100);
        
        console.log('blocksize' + map.blockSize);
                
        for(i = 0; i < map.size; i++) {
            for(j = 0; j < map.size; j++) {
                var type = m[i][j];
                var actual = getType(this.objects["box_" + i + "_" + j]);
                if (type != actual) {
                	if (this.objects["box_" + i + "_" + j] != undefined) this.objects["box_" + i + "_" + j].dispose();
                    this.objects["box_" + i + "_" + j] = undefined;
                } else continue;
                if(type === "Empty"){
                	continue;
                }
                
                var box = BABYLON.Mesh.CreateBox("box_" + i + "_" + j, map.blockSize, this.scene);
                this.objects["box_" + i + "_" + j] = box;
                box.position.x = - (i * map.blockSize);
                box.position.z = - (j * map.blockSize);
                
                switch(type) {
                    case "Indestructible":
                        box.material = indestructable_mat;
                        console.log("got to before loop");
                        break;
                        
                    case "Destructible":
                        box.material = destructable_mat;
                        break;
                }
            }
        }
        console.log("mapCreated = " +this.mapCreated);
        if (this.mapCreated != true) this.addLight();
        
       
    }
    function getType(block) {
    	if (!block) return "Empty";
        if (!block.material) return "Bomb";
    	if (block.material.name == "metal") return "Indestructible";
    	if (block.material.name == "brick") return "Destructible";
    	return "Bomb";
    }
    this.isPassable = function(x, y) {
    	var type = getType(this.objects["box_" + x + "_" + y]);
    	var res = (type === "Empty" || type === "Bomb");
    	console.log(res);
    	return res;
    }
    
}

var makeTextPlane = function(text, color, size) {
    var dynamicTexture = new BABYLON.DynamicTexture("DynamicTexture", 200, scene, true);
    dynamicTexture.hasAlpha = true;
    dynamicTexture.drawText(text, 5, 40, "bold 36px Arial", color , "transparent", true);
    var plane = new BABYLON.Mesh.CreatePlane("TextPlane", size*4, scene, true);
    plane.material = new BABYLON.StandardMaterial("TextPlaneMaterial", scene);
    plane.material.backFaceCulling = false;
    plane.material.specularColor = new BABYLON.Color3(0, 0, 0);
    plane.material.diffuseTexture = dynamicTexture;
    return plane;
    };

// Logic updater, called in the render loop
function update_scene(objects, game_logic, game_controller)
{
    // send updates to individual logic objects
    
    // update current player and camera
    var current_player = game_logic.current_player();
    if (current_player)
    {
        // camera update logic
        var camera = objects.getObject('camera');
        var camera_y = camera.alpha;
        camera_y = normalizeRadians(camera_y);
        var rotationUpdateMsg = game_logic.camera.updateRotation(camera_y, current_player.id);
        game_controller.send(rotationUpdateMsg);
        
        // update camera to follow the player
        var player_position = current_player.getPosition();
        camera.setTarget(new BABYLON.Vector3(-player_position.x, 0, -player_position.z));
    }
    
    // remove dead and disconnected players
    for (var pid in game_logic.pendingPlayersRemoved)
    {
        delete game_logic.pendingPlayersRemoved[pid];
        objects.removePlayer(pid);
    }

    // update all player positions
    // create player objects if necessary
    for (var pid in game_logic.players)
    {
        var player_logic = game_logic.players[pid];
        var player_model = objects.getPlayer(pid);
        if (!player_model)
        {
            console.log('dynamically adding player ' + pid);
            objects.addPlayer(pid);
            player_model = objects.getPlayer(pid);
        }
        var position = player_logic.getPosition();
        player_model.position.x = -position.x;
        player_model.position.z = -position.z;
        player_model.position.y = position.y;// - 0.5;
        
        //Update Health Bar
        if (player_model.healthBar == undefined) {
        	createHealthBar(player_model);
        }
        updateHealthBar(player_model, player_logic, camera);
        
        //Update Player name label
        if (player_model.label == undefined) {
        	createNameLabel(player_model, player_logic, pid);
        }
        updateNameLabel(player_model, camera);

        //Make the player rotation nice and smooth
        animatePlayerRotation(player_model, player_logic);
    }
    
    // update map
    if (game_logic.mapUpdated)
    {
        objects.updateMap(game_logic.map);
        game_logic.mapUpdated = false;
    }
    
    //add/remove powerUps as needed
    for (var key in game_logic.pendingPowerUps)
	{
    	var powerUp = game_logic.pendingPowerUps[key];
    	objects.addPowerUp(key.toString(), powerUp.x, powerUp.y, powerUp.type);
    	delete game_logic.pendingPowerUps[key];
	}
    for (var key in game_logic.pendingPowerUpsRemoved)
	{
    	objects.removePowerUp(key);
    	delete game_logic.pendingPowerUpsRemoved[key];
	}
    
    // update bombs
    // add bombs that have to be added
    for (var key in game_logic.pendingBombs)
    {
        var bomb = game_logic.pendingBombs[key];
        objects.addBomb(key.toString(), bomb.x, bomb.y);
        delete game_logic.pendingBombs[key];
    }
    
    // remove bombs that have to be removed
    for (var key in game_logic.pendingBombsRemoved)
    {
        var bomb = objects.removeBomb(key);
        delete game_logic.pendingBombsRemoved[key];
    }
    
    // update explosions
    // add explosions that have to be added
    for (var key in game_logic.pendingExplosions)
    {
        
        var explosion = game_logic.pendingExplosions[key];
        if (!objects.isPassable(explosion.x, explosion.y)) continue;
        console.log("Adding explosion");
        objects.addExplosion(explosion.id, explosion.x, explosion.y, explosion.shape);
        delete game_logic.pendingExplosions[key];
    }
    
    // remove explosions that timed out
    for (var key in game_logic.allExplosions)
    {
        var e = game_logic.allExplosions[key];
        if (e.finished())
        {
            objects.removeExplosion(key);
            game_logic.removeExplosion(key);
        }
    }
    
	
	// update player bar
	try {
		var frames = objects.frames++;
		bar_context.fillStyle = 'white';
		bar_context.fillRect(0, 0, bottom_bar.width, bottom_bar.height);
		bar_context.fillStyle = 'blue';
		bar_context.fillText(frames + ' frames drawn', 10, 10);
	} catch (e) {}
}

// createScene function
var createScene = function(objects)
{

    objects.setScene(scene);
    
    // colour of background
    scene.clearColor = new BABYLON.Color3(0.5, 0.8, 0.8);
    
    // create a camera
    var camera = new BABYLON.ArcRotateCamera("Camera", Math.PI / 2, 1.0, 7, BABYLON.Vector3.Zero(), scene);
    
    var rcanvas = scene.getEngine().getRenderingCanvas();
    var controlEnabled = false;
    rcanvas.addEventListener("click", function(evt) {
        rcanvas.requestPointerLock = rcanvas.requestPointerLock || rcanvas.msRequestPointerLock || rcanvas.mozRequestPointerLock || rcanvas.webkitRequestPointerLock;
        if (rcanvas.requestPointerLock) {
            rcanvas.requestPointerLock();
        }
    }, false);

    // Event listener when the pointerlock is updated.
    var pointerlockchange = function (event) {
        controlEnabled = (document.mozPointerLockElement === rcanvas || document.webkitPointerLockElement === rcanvas || document.msPointerLockElement === rcanvas || document.pointerLockElement === rcanvas);
        if (!controlEnabled) {
            camera.detachControl(rcanvas);
        } else {
            camera.attachControl(rcanvas);
        }
    };
    document.addEventListener("pointerlockchange", pointerlockchange, false);
    document.addEventListener("mspointerlockchange", pointerlockchange, false);
    document.addEventListener("mozpointerlockchange", pointerlockchange, false);
    document.addEventListener("webkitpointerlockchange", pointerlockchange, false);
    
    camera.lowerBetaLimit = 0.3;
    camera.upperBetaLimit = 1.3;
    camera.lowerRadiusLimit = 5;
    camera.upperRadiusLimit = 14;

    camera.inertia = 0;
    
    scene.activeCamera = camera;
    
    objects.objects['camera'] = camera;
    
    // ------------------------------------------------------------------------
    // add meshes

    BABYLON.Engine.ShadersRepository = "shaders/";
  
    var skybox = BABYLON.Mesh.CreateBox("skyBox", 1000.0, scene);
    var skyboxMaterial = new BABYLON.StandardMaterial("skybox", scene);
    skyboxMaterial.backFaceCulling = false;
    skyboxMaterial.reflectionTexture = new BABYLON.CubeTexture("./assets/SkyBox/Tropical/TropicalSunnyDay", scene);
    skyboxMaterial.reflectionTexture.coordinatesMode = BABYLON.Texture.SKYBOX_MODE; 
    skyboxMaterial.diffuseColor = new BABYLON.Color3(0,0,0);
    skyboxMaterial.specularColor = new BABYLON.Color3(0,0,0);
    skybox.material = skyboxMaterial;


    scene.fogMode = BABYLON.Scene.FOGMODE_EXP2;
    scene.fogDensity = 0.001;
    scene.fogColor = new BABYLON.Color3(0.8,0.83,0.8);
/*
    var mat = new BABYLON.StandardMaterial("ground", scene);
    var t = new BABYLON.Texture("assets/ground_texture.jpg", scene);
    t.uScale = t.vScale = 10;
    mat.diffuseTexture = t;
    mat.specularColor = BABYLON.Color3.Black();
*/

    //ground
    var extraGround = BABYLON.Mesh.CreateGround("extraGround", 1000, 1000, 1, scene, false);
    var extraGroundMaterial = new BABYLON.StandardMaterial("extraGround", scene);
    extraGroundMaterial.diffuseTexture = new BABYLON.Texture("./assets/Ground/dirt.jpg", scene);
    extraGroundMaterial.diffuseTexture.uScale = 60;
    extraGroundMaterial.diffuseTexture.vScale = 60;
    extraGround.position.y = -0.5;
    extraGround.material = extraGroundMaterial;

    var ground = BABYLON.Mesh.CreateGroundFromHeightMap("ground", "./assets/Ground/heightMap.png", 100, 100, 100, 0, 10, scene, false);
    var groundMaterial = new BABYLON.StandardMaterial("ground", scene);
    groundMaterial.diffuseTexture = new BABYLON.Texture("./assets/Ground/dirt.jpg", scene);
    groundMaterial.diffuseTexture.uScale = 6;
    groundMaterial.diffuseTexture.vScale = 6;
    groundMaterial.specularColor = new BABYLON.Color3(0,0,0);
    ground.position.y = 0.0;
    ground.position.z = 100.0;
    ground.material = groundMaterial;

//ADD PARTICLES

    sound_system.initialize();
    // exit function
    return scene;
};


function buildArray(size) {
  var m = [];
  for(i = 0; i < size; i++){
    var acc = [];
    for (j = 0; j < size ; j++)
    {
      acc.push("");
    }
    m.push(acc);

  }
  return m;
}


function setupParticles(scene, shape) {

    var particleSystem = new BABYLON.ParticleSystem("particles", 2000, scene);

    //Texture of each particle
    particleSystem.particleTexture = new BABYLON.Texture("assets/flare.png", scene);

    // Where the particles come from
//ADD    particleSystem.emitter = fountain; // the starting object, the emitter

    // Colors of all particles
    particleSystem.color1 = new BABYLON.Color4(0.97, 0.28, 0.36, 1.0);
    particleSystem.color2 = new BABYLON.Color4(0.94, 0.35, 0.12, 1.0);
    particleSystem.colorDead = new BABYLON.Color4(0.2, 0, 0, 0.0);

    // Size of each particle (random between...
    particleSystem.minSize = 0.1;
    particleSystem.maxSize = 0.5;

    // Life time of each particle (random between...
    particleSystem.minLifeTime = 0.3;
    particleSystem.maxLifeTime = 1.5;

    // Emission rate
    particleSystem.emitRate = 1500;

    // Blend mode : BLENDMODE_ONEONE, or BLENDMODE_STANDARD
    particleSystem.blendMode = BABYLON.ParticleSystem.BLENDMODE_ONEONE;

    // Set the gravity of all particles
    particleSystem.gravity = new BABYLON.Vector3(0, -9.81, 0);

    // Direction of each particle after it has been emitted
//ADD    particleSystem.direction1 = new BABYLON.Vector3(-7, 8, 3);
//ADD    particleSystem.direction2 = new BABYLON.Vector3(7, 8, -3);

    // Angular speed, in radians
    particleSystem.minAngularSpeed = 0;
    particleSystem.maxAngularSpeed = Math.PI;

    // Speed
    particleSystem.minEmitPower = 1;
    particleSystem.maxEmitPower = 3;
    particleSystem.updateSpeed = 0.005;

    // Start the particle system
//ADD    particleSystem.start();

    return particleSystem;

}
var createHealthBar = function (player_model) {
	var healthBar = BABYLON.Mesh.CreateBox("healthBar", 0, scene);
	healthBar.isVisible = false;
	healthBar.scaling.x = 1.3;
	healthBar.scaling.y = 0.10;
	healthBar.scaling.z = 0.01;
	player_model.healthBar = healthBar;
	
	var fullHealthBar = BABYLON.Mesh.CreateBox("maxHealthBar", 1, scene);
	player_model.fullHealthBar = fullHealthBar;
	var fhb_material = new BABYLON.StandardMaterial("fullHealthBarColour", scene);
	player_model.fullHealthBar.material = fhb_material;
    player_model.fullHealthBar.material.diffuseColor = new BABYLON.Color3(1, 0.1, 0.1);
    
	var currentHealthBar = BABYLON.Mesh.CreateBox("healthBar", 1, scene);
	player_model.currentHealthBar = currentHealthBar;
    player_model.currentHealthBar.scaling.x = 1.01;
    player_model.currentHealthBar.scaling.y = 1.01;
    player_model.currentHealthBar.scaling.z = 1.01;
	var material = new BABYLON.StandardMaterial("healthBarColour", scene);
    player_model.currentHealthBar.material = material;
    player_model.currentHealthBar.material.diffuseColor = new BABYLON.Color3(0.1, 1, 0.1);
	
	player_model.fullHealthBar.parent = player_model.healthBar;
	player_model.currentHealthBar.parent = player_model.healthBar;
}

var updateHealthBar = function(player_model, player_logic, camera) {
	player_model.healthBar.position.x = player_model.position.x;
    player_model.healthBar.position.y = player_model.position.y + 1.3;
    player_model.healthBar.position.z = player_model.position.z;
    player_model.healthBar.rotation.y = -camera.alpha-radians_rotation/4; 
    player_model.currentHealthBar.scaling.x = (player_logic.health/player_logic.maxHealth);
    player_model.currentHealthBar.position.x = -(1-player_model.currentHealthBar.scaling.x)*1/2;
}

var animatePlayerRotation = function (player_model, player_logic) {
	scene.stopAnimation(player_model); 
    player_model.animations = [];
    if (player_model.animations[0] == undefined)
    {
    	var newRotation = -player_logic.getAlpha()+radians_rotation/4;
        while (newRotation - player_model.rotation.y > radians_rotation/2) newRotation -= radians_rotation;
        while (player_model.rotation.y - newRotation > radians_rotation/2) newRotation += radians_rotation;
    	var animation = new BABYLON.Animation("rotationAnimation", "rotation.y", 60,
    	BABYLON.Animation.ANIMATIONTYPE_FLOAT, BABYLON.Animation.ANIMATIONLOOPMODE_RELATIVE);
        var keys = [];
    	keys.push({ frame: -1, value: player_model.rotation.y});
    	keys.push({ frame: 3, value: newRotation});
    	animation.setKeys(keys);
    	player_model.animations.push(animation);
    	scene.beginAnimation(player_model, -1, 3, false);
    }
}

var createNameLabel = function(player_model, player_logic, pid) {
	var colour = "red";
	if (pid == game_logic.current_player_id) colour = "blue";
	var label_name = "";
	if (player_logic.id.length >= 13) label_name = player_logic.id;
	else {
		 label_name = " ".repeat((13-player_logic.id.length)) + player_logic.id;
	}	
	var label = makeTextPlane (label_name, colour, 0.5);
	console.log("label:" + label_name);
    player_model.label = label;
}

var updateNameLabel = function(player_model, camera) {
    player_model.label.position.x = player_model.position.x;
    player_model.label.position.y = player_model.position.y + 0.2;
    player_model.label.position.z = player_model.position.z;
    player_model.label.rotation.y = -camera.alpha-radians_rotation/4;
}