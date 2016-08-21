package com.team.engine;

public abstract class GameObject {
	public abstract void update();
	
	public abstract void render(Scene scene, Camera cam);
}
