package com.team.engine;

public abstract class GameObject {
	/**
	 * Called when the object is added to the scene.
	 */
	public abstract void init(Scene scene);
	
	public abstract void update();
	
	public abstract void render(Scene scene, Camera cam);
	
	public abstract void renderShadow(Shader s);
}
