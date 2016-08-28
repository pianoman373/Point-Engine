package com.team.engine;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2i;
import com.team.engine.vecmath.Vec3;

/**
 *	Directional light class for sending to our default shader's uniforms.
 */
public class DirectionalLight {
	public Vec3 direction;
	public Vec3 color;
	public boolean castShadow;
	public int shadowRange;
	public int shadowResolution;
	public Framebuffer shadowBuffer;
	
	public DirectionalLight(Vec3 direction, Vec3 color, boolean castShadow, int shadowRange, int shadowResolution) {
		this.direction = direction;
		this.color = color;
		this.castShadow = castShadow;
		this.shadowRange = shadowRange;
		this.shadowResolution = shadowResolution;
		
		if (castShadow) {
			shadowBuffer = Framebuffer.shadow(new Vec2i(shadowResolution, shadowResolution));
		}
	}
	
	/**
	 * Gets the matrix (model * view) of the current shadow.
	 */
	public Mat4 getShadowMat() {
		Vec3 offsetPosition = Engine.camera.getPosition().add(Engine.camera.getDirection().multiply(shadowRange - 1));
		return Mat4.orthographic(-shadowRange, shadowRange, -shadowRange, shadowRange, -shadowRange, shadowRange).multiply(Mat4.LookAt(offsetPosition, offsetPosition.add(direction), new Vec3(0.0f, 1.0f, 0.0f)));
	}
}
