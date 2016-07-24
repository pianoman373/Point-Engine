package com.team.engine;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

public class OrthographicCamera extends Camera {
	
	private Vec2 position = new Vec2(0, 0);
	private float zoom = 1;

	@Override
	public Mat4 getView() {
		return new Mat4().translate(new Vec3(-position.x, -position.y, 0));
	}
	
	public void update() {
		float cameraSpeed = 10.0f * Engine.instance.deltaTime;
		
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
	    
	    zoom = (float)(-Input.scrollingAmount > 0.1 ? -Input.scrollingAmount : 0.1) + 2;
	}

	@Override
	public Mat4 getProjection() {
		return Mat4.orthographic(-10 * zoom * .1f, 10 * zoom * .1f, -8 * zoom * .1f, 8 * zoom * .1f, -100, 100);
	}

	@Override
	public Vec3 getPosition() {
		return new Vec3(this.position.x, this.position.y, 0);
	}
	
}
