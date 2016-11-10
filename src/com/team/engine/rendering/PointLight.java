package com.team.engine.rendering;

import static com.team.engine.Globals.*;

import com.team.engine.vecmath.Vec3;

/**
 *	Point light class for sending to our default shader's uniforms.
 */
public class PointLight {
	public Vec3 position;
	public Vec3 color;
	
	//these last two values control falloff, I'm still getting the hang of them. Reference: http://www.learnopengl.com/#!Lighting/Light-casters
	public float linear;
	public float quadric;
	
	public PointLight(Vec3 position, Vec3 color, float strength, float linear, float quadric) {
		this.position = position;
		this.color = color.normalize().multiply(strength);
		this.linear = linear;
		this.quadric = quadric;
	}
	
	public PointLight(Vec3 position, Vec3 color, float strength, float distance) {
		this(position, color, strength, 4.5f/distance, 1275f/(distance * distance));
		
		print("linear: " + this.linear + ", quadric: " + this.quadric);
	}
}