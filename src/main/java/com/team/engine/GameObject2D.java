package com.team.engine;

import org.jbox2d.dynamics.Fixture;

public interface GameObject2D {
	/**
	 * Called when the object is added to the scene.
	 */
	public void init(Scene scene);
	
	public void update();
	
	public void render(Scene scene, Camera cam);
	
	public void onContact(Fixture f);
	
	public void endContact(Fixture f);
}
