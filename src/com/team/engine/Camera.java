package com.team.engine;

import com.team.engine.vecmath.Mat4;

public abstract class Camera extends GameObject {
	public abstract Mat4 getView();
	
	public abstract Mat4 getProjection();
}
