package pianoman.engine;

import pianoman.engine.vecmath.Vec3;

public class PointLight {
	public Vec3 position;
	public Vec3 color;
	public float linear;
	public float quadric;
	
	public PointLight(Vec3 position, Vec3 color, float linear, float quadric) {
		this.position = position;
		this.color = color;
		this.linear = linear;
		this.quadric = quadric;
	}
}
