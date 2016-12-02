package com.team.engine.gameobject;

import com.team.engine.Camera;
import com.team.engine.Scene;
import com.team.engine.rendering.Shader;

/**
 * The superclass of all 3D objects wishing to be added to the Scene.
 * 
 * Pretty much just lays down the basics of that a game object should do.
 */
public interface GameObject {
	/**
	 * Called when the object is added to the scene.
	 * This is where you setup your physics and stuff instead of in your game object's constructor.
	 */
	public void init(Scene scene);
	
	/**
	 * Updates the object every tick. Called by Scene.
	 */
	public void update();
	
	/**
	 * Renders the object every tick. Called by Scene.
	 */
	public void render(Scene scene, Camera cam);
	
	/**
	 * Renders the object for the shadow pass. USE THE GIVEN SHADER. All you need to do here is render your
	 * geometry, nothing more.
	 */
	public void renderShadow(Shader s);
}
