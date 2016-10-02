package com.team.engine.gameobject;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import com.team.engine.Camera;
import com.team.engine.Scene;


public abstract class GameObject2D {
	public String tag = "";
	public Body body;
	
	/**
	 * Called when the object is added to the scene.
	 */
	abstract public void init(Scene scene);
	
	/**
	 * Updates the object every tick. Called by Scene.
	 */
	abstract public void update();
	
	/**
	 * Renders the object every tick. Called by Scene.
	 */
	abstract public void render(Scene scene, Camera cam);
	
	/**
	 * Called whenever any of this object's fixtures comes in contact with another body's.
	 * Both the colliding fixture, and the colliding gameObject are passed through.
	 * 
	 * Calls to contact functions are called by Scene, but only work if the game object has called:
	 * 
	 * this.body.setUserData(self);
	 * 
	 * That way the Scene can figure out what GameObjects are attached to the bodies in box2D.
	 * The PhysicsObject class automatically sets up the GameObjects body and set's the user 
	 * data for you. It's recommended that you extend it rather than GameObject2D directly.
	 * 
	 * Warning: this function is likely to be called while box2D is in the middle of time stepping. Due
	 * to the way box2D works, you will not be able to alter many of your body's parameters (e.g position) from this
	 * function. What you should do is have this function set a flag, and then change parameters in the next call to update();
	 */
	public void onContact(Fixture f, GameObject2D other) {}
	
	/**
	 * Called whenever any of this object's fixtures end contact with another body's.
	 * Both the colliding fixture, and the colliding gameObject are passed through.
	 * 
	 * Calls to contact functions are called by Scene, but only work if the game object has called:
	 * 
	 * this.body.setUserData(self);
	 * 
	 * That way the Scene can figure out what GameObjects are attached to the bodies in box2D.
	 * The PhysicsObject class automatically sets up the GameObjects body and set's the user 
	 * data for you. It's recommended that you extend it rather than GameObject2D directly.
	 * 
	 * Warning: this function is likely to be called while box2D is in the middle of time stepping. Due
	 * to the way box2D works, you will not be able to alter many of your body's parameters (e.g position) from this
	 * function. What you should do is have this function set a flag, and then change parameters in the next call to update();
	 */
	public void endContact(Fixture f, GameObject2D other) {}
}
