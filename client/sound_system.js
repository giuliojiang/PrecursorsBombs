/**
 * 
 */

function SoundSystem(system) {
	var explosion;
	var background;
	
	this.initialize = function () {
		this.explosion = new BABYLON.Sound("explosion", "assets/sounds/small_blast.wav", scene);
	    this.background = new BABYLON.Sound("background", "assets/sounds/background.mp3", scene,  function () {
	        // Call with the sound is ready to be played (loaded & decoded)
	        // TODO: add your logic
	    	sound_system.playBackground();
	        console.log("Sound ready to be played!");
	    }, { loop: true, autoplay: false });
	}
	
	this.playExplosion = function() {
		this.explosion.play();
	}
	this.playBackground = function() {
		this.background.setVolume(0.05);
		this.background.play();
		
	}
}