package com.team.engine;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

public abstract class Camera extends GameObject {
	public abstract Mat4 getView();
	
	public abstract Mat4 getProjection();
	
	public abstract Vec3 getPosition();
}
