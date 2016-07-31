package com.team.engine;

import static org.lwjgl.glfw.GLFW.*;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

public class FPSCamera extends Camera {
	public Vec3 position = new Vec3(0.0f, 0.0f, 15.0f);
	public Vec3 front = new Vec3(0.0f, 0.0f, -1.0f);
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
		/*front.x = (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
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

			this.yaw   += offsetVec.x;
			this.pitch += offsetVec.y;

			if(this.pitch > 89.0f)
				this.pitch =  89.0f;
			if(this.pitch < -89.0f)
				this.pitch = -89.0f;
		}*/
		
		Vec3 right = front.cross(up);
		
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
			
			//vertical
			Mat4 mat = new Mat4().rotate(new Vec4(right.x, right.y, right.z, yoffset));
			Vec4 vec = mat.multiply(new Vec4(front.x, front.y, front.z, 1.0f));
			front = new Vec3(vec.x, vec.y, vec.z);
			
			Vec4 vec2 = mat.multiply(new Vec4(up.x, up.y, up.z, 1.0f));
			up = new Vec3(vec2.x, vec2.y, vec2.z);
			
			//horizontal
			Mat4 mat2 = new Mat4().rotate(new Vec4(up.x, up.y, up.z, -xoffset));
			Vec4 vec3 = mat2.multiply(new Vec4(front.x, front.y, front.z, 1.0f));
			front = new Vec3(vec3.x, vec3.y, vec3.z);
		}
		
		
		
		lastX = (float)Input.mousePos.x;
		lastY = (float)Input.mousePos.y;
		
		if (Input.isKeyDown(GLFW_KEY_E)) {
			Mat4 mat = new Mat4().rotate(new Vec4(front.x, front.y, front.z, 0.3f));
			Vec4 vec = mat.multiply(new Vec4(up.x, up.y, up.z, 1.0f));
			up = new Vec3(vec.x, vec.y, vec.z);
		}
		if (Input.isKeyDown(GLFW_KEY_Q)) {
			Mat4 mat = new Mat4().rotate(new Vec4(front.x, front.y, front.z, -0.3f));
			Vec4 vec = mat.multiply(new Vec4(up.x, up.y, up.z, 1.0f));
			up = new Vec3(vec.x, vec.y, vec.z);
		}
		
		if (Input.scrollingAmount <= 1) Input.scrollingAmount = 1;
		
		float cameraSpeed = 50.0f * Engine.instance.deltaTime * ((float)Input.scrollingAmount * (float)Input.scrollingAmount * 0.05f);
	    if(Input.isKeyDown(GLFW_KEY_W)) {
	        position = position.add((front.multiply(cameraSpeed)));
	    }
	    if(Input.isKeyDown(GLFW_KEY_S)) {
	    	position = position.subtract((front.multiply(cameraSpeed)));
	    }
	    if(Input.isKeyDown(GLFW_KEY_A)) {
	    	position = position.subtract(right.multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_D)) {
	    	position = position.add(right.multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_R)) {
	    	position = position.add(up.multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_F)) {
	    	position = position.subtract(up.multiply(cameraSpeed));
	    }
	}

	@Override
	public Mat4 getView() {
		return Mat4.LookAt(this.position, this.position.add(this.front), this.up);
	}

	@Override
	public Mat4 getProjection() {
		return Mat4.perspective(45.0f, 1000/800, 0.1f, 10000000.0f);
	}

	@Override
	public Vec3 getPosition() {
		return this.position;
	}
}
