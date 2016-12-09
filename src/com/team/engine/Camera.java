package com.team.engine;

import static com.team.engine.Globals.*;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
/**
 * The abstract superclass of all cameras.
 */
public class Camera {
	public Vec3 position = vec3(0.0f, 0.0f, 15.0f);
	public Vec3 front = vec3(0.0f, 0.0f, -1.0f);
	public Vec3 up = vec3(0.0f, 1.0f, 0.0f);
	public boolean is2D;
	private float zoom = 10f;
	
	public Camera(boolean is2D) {
		this.is2D = is2D;
	}
	
	public Mat4 getView() {
		if (Engine.isVR) {
			vr.HmdMatrix34_t mat = VRManager.lEyeView;
			
			
			return mat4(
					vec4(mat.m[0], mat.m[4], mat.m[8], 0f),
					vec4(mat.m[1], mat.m[5], mat.m[9], 0f),
					vec4(mat.m[2], mat.m[6], mat.m[10], 0f),
					vec4(mat.m[3], mat.m[7], mat.m[11], 1f)).inverse().translate(vec3(1, 1, 1));
		}
		else {
			return Mat4.LookAt(this.position, this.position.add(this.front), this.up);
		}
	}
	
	public Mat4 getProjection() {
		if (Engine.isVR) {
			
			vr.HmdMatrix44_t mat = VRManager.lEyeProj;
			
			
			return mat4(
					vec4(mat.m[0], mat.m[4], mat.m[8], mat.m[12]),
					vec4(mat.m[1], mat.m[5], mat.m[9], mat.m[13]),
					vec4(mat.m[2], mat.m[6], mat.m[10], mat.m[14]),
					vec4(mat.m[3], mat.m[7], mat.m[11], mat.m[15]));
		}
		else if(is2D) {
			return Mat4.orthographic(-10 * zoom * .1f, 10 * zoom * .1f, -8 * zoom * .1f, 8 * zoom * .1f, -100, 100);
		}
		else {
			return Mat4.perspective(60.0f, (float)Settings.WINDOW_WIDTH/(float)Settings.WINDOW_HEIGHT, 0.1f, 10000000.0f);
		}
	}
	
	public Vec3 getDirection() {
		return this.front;
	}
	
	public Vec3 getPosition() {
		return this.position;
	}
	
	public void setPosition(Vec3 pos) {
		this.position = pos;
	}
	
	public void setDirection(Vec3 dir) {
		this.front = dir;
	}
}
