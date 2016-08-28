package com.team.engine;

import java.util.ArrayList;

import com.team.engine.vecmath.Vec3;

/**
 * Scene is meant to make life easier by taking care of all the object handling, lights, and rendering on it's own.
 * Currently this is only designed for 3D. You MUST call setupPhysics if you want there to be physics. If you have a game object
 * that uses bullet physics and setupPhysics was not called you will likely crash.
 */
public class Scene2d {
	public ArrayList<PointLight> lights = new ArrayList<>();

	public Vec3 skyColor = new Vec3(0.0f, 0.0f, 0.0f);
	
	public void render(Camera cam) {
		
	}
	
	public void update() {
		
	}

	public void add(PointLight light) {
		lights.add(light);
	}
}
