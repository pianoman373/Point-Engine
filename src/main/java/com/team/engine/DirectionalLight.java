package com.team.engine;

import com.team.engine.vecmath.Vec3;

/**
 *	Directional light class for sending to our default shader's uniforms.
 */
public class DirectionalLight {
	public Vec3 direction;
	public Vec3 color;
	
	public DirectionalLight(Vec3 direction, Vec3 color) {
		this.direction = direction;
		this.color = color;
	}
}
