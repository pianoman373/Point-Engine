package com.team.engine;

import static org.lwjgl.glfw.GLFW.*;

import com.team.engine.vecmath.Vec3;

public class Camera {
	public Vec3 position = new Vec3(0.0f, 0.0f, 5.0f);
	public Vec3 front = new Vec3(0.0f, 0.0f, 0.0f);
	public float pitch = 0.0f;
	public float yaw = -90.0f;
	public float roll = 0;
	public Vec3 up = new Vec3(0.0f, 1.0f, 0.0f);
	
	private static float lastX = 400;
	private static float lastY = 300;
	/**
	 * Called by the game loop every frame. When we make GameObjects they'll all have this functionality for multiple objects. For now it's called directly
	 * from the loop.
	 * 
	 * TODO: this update it's own object instead of updating Engine.camera
	 */
	public void update() {
		front.x = (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
		front.y = (float) Math.sin(Math.toRadians(pitch));
		front.z = (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
		
		front = front.normalize();

		
		float cameraSpeed = 20.0f * Engine.instance.deltaTime;
	    if(Input.isKeyDown(GLFW_KEY_W)) {
	        position = position.add((front.multiply(cameraSpeed)));
	    }
	    if(Input.isKeyDown(GLFW_KEY_S)) {
	    	position = position.subtract((front.multiply(cameraSpeed)));
	    }
	    if(Input.isKeyDown(GLFW_KEY_A)) {
	    	position = position.subtract(((front.cross(up)).normalize()).multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_D)) {
	    	position = position.add(((front.cross(up)).normalize()).multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_Q)) {
	    	//roll += 0.5f;
	    }
	    if(Input.isKeyDown(GLFW_KEY_E)) {
	    	///roll -= 0.5f;
	    }
	    if(Input.isKeyDown(GLFW_KEY_R)) {
	    	position = position.add(new Vec3(0, cameraSpeed, 0));
	    }
	    if(Input.isKeyDown(GLFW_KEY_F)) {
	    	position = position.add(new Vec3(0, -cameraSpeed, 0));
	    }
	    
	    up = new Vec3(0.0f, 1.0f, 0.0f).rotateRoll(roll);
	    
	    if (Input.mouseGrabbed == true) {
			if(Input.firstMouse)
			{
				lastX = (float) Input.mousePos.x;
				lastY = (float) Input.mousePos.y;
				Input.firstMouse = false;
			}

			float xoffset = (float)Input.mousePos.x - lastX;
			float yoffset = lastY - (float)Input.mousePos.y;
			lastX = (float)Input.mousePos.x;
			lastY = (float)Input.mousePos.y;

			float sensitivity = 0.05f;
			xoffset *= sensitivity;
			yoffset *= sensitivity;
			
			Vec3 offsetVec = new Vec3(xoffset, yoffset, 0).rotateRoll(roll);

			Engine.instance.camera.yaw   += offsetVec.x;
			Engine.instance.camera.pitch += offsetVec.y;

			if(Engine.instance.camera.pitch > 89.0f)
				Engine.instance.camera.pitch =  89.0f;
			if(Engine.instance.camera.pitch < -89.0f)
				Engine.instance.camera.pitch = -89.0f;
		}
		
		lastX = (float)Input.mousePos.x;
		lastY = (float)Input.mousePos.y;
	}
}
