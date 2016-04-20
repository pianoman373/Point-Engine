package com.team.engine;

import pianoman.engine.vecmath.Vec3;

/**
 *	Point light class for sending to our default shader's uniforms.
 */
public class PointLight {
	public Vec3 position;
	public Vec3 color;
	
	//these last two values control falloff, I'm still getting the hang of them. Reference: http://www.learnopengl.com/#!Lighting/Light-casters
	public float linear;
	public float quadric;
	
	public PointLight(Vec3 position, Vec3 color, float linear, float quadric) {
		this.position = position;
		this.color = color;
		this.linear = linear;
		this.quadric = quadric;
	}
}
