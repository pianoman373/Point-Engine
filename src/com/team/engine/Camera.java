package com.team.engine;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
/**
 * The abstract superclass of all cameras.
 */
public abstract class Camera {
	public abstract Mat4 getView();
	
	public abstract Mat4 getProjection();
	
	public abstract Vec3 getDirection();
	
	public abstract Vec3 getPosition();
	
	public abstract void setPosition(Vec3 pos);
	
	public abstract void setDirection(Vec3 dir);
	
	public abstract void update();
}
