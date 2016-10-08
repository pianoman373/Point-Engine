package com.team.engine;

import static org.lwjgl.glfw.GLFW.*;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A 2D camera implementation. Engine.camera will automatically be orthographic
 * if running in 2D mode.
 */
public class OrthographicCamera extends Camera {
	
	private Vec3 position = new Vec3(0, 0, 0);
	private float zoom = 10f;

	@Override
	public Mat4 getView() {
		return new Mat4().translate(new Vec3(-position.x, -position.y, 0));
	}
	
	public void update() {
		float cameraSpeed = 10.0f * Engine.deltaTime;
		
		if(Input.isKeyDown(GLFW_KEY_W)) {
	        position.y += cameraSpeed;
	    }
	    if(Input.isKeyDown(GLFW_KEY_S)) {
	    	position.y -= cameraSpeed;
	    }
	    if(Input.isKeyDown(GLFW_KEY_A)) {
	    	position.x -= cameraSpeed;
	    }
	    if(Input.isKeyDown(GLFW_KEY_D)) {
	    	position.x += cameraSpeed;
	    }
	}

	@Override
	public Mat4 getProjection() {
		return Mat4.orthographic(-10 * zoom * .1f, 10 * zoom * .1f, -8 * zoom * .1f, 8 * zoom * .1f, -100, 100);
	}

	@Override
	public Vec3 getPosition() {
		return this.position;
	}

	@Override
	public Vec3 getDirection() {
		return new Vec3(0.0f, 0.0f, 1.0f);
	}
	
	@Override
	public void setDirection(Vec3 dir) {
		
	}

	@Override
	public void setPosition(Vec3 pos) {
		this.position = pos;
	}
	
}
