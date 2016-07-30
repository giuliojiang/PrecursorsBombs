// Get the canvas element from HTML
var canvas = document.getElementById("renderCanvas");

// Load Babylon 3D engine
var engine = new BABYLON.Engine(canvas, true);

function SceneObjects()
{
    this.objects = {};
    
    this.scene = undefined;

//     this.addSphere = function()
//     {
//         // add a sphere
//         this.objects["sphere"] = BABYLON.Mesh.CreateSphere("sphere", 20, 2, this.scene);
//     }
//     
    this.getObject = function(name)
    {
        return this.objects[name];
    }
}

// createScene function
var createScene = function(objects)
{
    // new scene object
    var scene = new BABYLON.Scene(engine);
    
    objects.scene = scene;
    
    // colour of background
    scene.clearColor = new BABYLON.Color3(0.5, 0.8, 0.8);
    
    // create a camera
    var camera = new BABYLON.ArcRotateCamera("Camera", Math.PI / 2, 1.0, 110, BABYLON.Vector3.Zero(), scene);
    
    var canvas = scene.getEngine().getRenderingCanvas();
    var controlEnabled = false;
    canvas.addEventListener("click", function(evt) {
        canvas.requestPointerLock = canvas.requestPointerLock || canvas.msRequestPointerLock || canvas.mozRequestPointerLock || canvas.webkitRequestPointerLock;
        if (canvas.requestPointerLock) {
            canvas.requestPointerLock();
        }
    }, false);

    // Event listener when the pointerlock is updated.
    var pointerlockchange = function (event) {
        controlEnabled = (document.mozPointerLockElement === canvas || document.webkitPointerLockElement === canvas || document.msPointerLockElement === canvas || document.pointerLockElement === canvas);
        if (!controlEnabled) {
            camera.detachControl(canvas);
        } else {
            camera.attachControl(canvas);
        }
    };
    document.addEventListener("pointerlockchange", pointerlockchange, false);
    document.addEventListener("mspointerlockchange", pointerlockchange, false);
    document.addEventListener("mozpointerlockchange", pointerlockchange, false);
    document.addEventListener("webkitpointerlockchange", pointerlockchange, false);
    
    // create a light
    var light = new BABYLON.HemisphericLight("light1", new BABYLON.Vector3(0, 1, 0), scene);
    
    // change light intensity
    light.intensity = 0.5;
    
    // ------------------------------------------------------------------------
    // add meshes
    
    objects.addSphere();

    // add the ground(name, width, depth, subdivisions, scene
    var ground = BABYLON.Mesh.CreateGround("ground1", 6, 6, 2, scene);
    
    // exit function
    return scene;
};
