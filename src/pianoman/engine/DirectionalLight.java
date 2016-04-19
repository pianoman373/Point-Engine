package pianoman.engine;

import pianoman.engine.vecmath.Vec3;

public class DirectionalLight {
	public Vec3 direction;
	public Vec3 color;
	
	public DirectionalLight(Vec3 direction, Vec3 color) {
		this.direction = direction;
		this.color = color;
	}
}
