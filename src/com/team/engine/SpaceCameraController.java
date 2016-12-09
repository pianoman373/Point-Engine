package com.team.engine;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * A camera implementation with 3 degrees of rotation. Useful for space.
 */
public class SpaceCameraController {
	public static float WASD_SENSITIVITY = 100f;
	
	private static final float MOUSE_SENSITIVITY = 7f;
	private static float lastX;
	private static float lastY;
	
	/**
	 * Called by the game loop every frame. Get's keyboard and mouse input and moves the camera accordingly
	 */
	public static void update() {
		Camera cam = Engine.camera;
		
		Vec3 right = cam.front.cross(cam.up);
					
		if (Input.scrollingAmount <= 1) Input.scrollingAmount = 1;
		
		if (Input.mouseGrabbed == true) {
			if(Input.firstMouse) {
				lastX = Input.mousePos.x;
				lastY = Input.mousePos.y;
				Input.firstMouse = false;
			}

			float xoffset = Input.mousePos.x - lastX;
			float yoffset = lastY - Input.mousePos.y;
			lastX = Input.mousePos.x;
			lastY = Input.mousePos.y;

			float sensitivity = MOUSE_SENSITIVITY * Engine.deltaTime;
			xoffset *= sensitivity;
			yoffset *= sensitivity;
			
			//vertical
			Mat4 mat = mat4().rotate(vec4(right.x, right.y, right.z, yoffset));
			Vec4 vec = mat.multiply(vec4(cam.front.x, cam.front.y, cam.front.z, 1.0f));
			cam.front = vec3(vec.x, vec.y, vec.z);
			Vec4 vec2 = mat.multiply(vec4(cam.up.x, cam.up.y, cam.up.z, 1.0f));
			cam.up = vec3(vec2.x, vec2.y, vec2.z);
			
			//horizontal
			Mat4 mat2 = mat4().rotate(vec4(cam.up.x, cam.up.y, cam.up.z, -xoffset));
			Vec4 vec3 = mat2.multiply(vec4(cam.front.x, cam.front.y, cam.front.z, 1.0f));
			cam.front = vec3(vec3.x, vec3.y, vec3.z);
		}
		
		lastX = Input.mousePos.x;
		lastY = Input.mousePos.y;
		
		if (Input.isKeyDown(GLFW_KEY_E)) {
			Mat4 mat = mat4().rotate(vec4(cam.front.x, cam.front.y, cam.front.z, 20f * Engine.deltaTime));
			Vec4 vec = mat.multiply(vec4(cam.up.x, cam.up.y, cam.up.z, 1.0f));
			cam.up = vec3(vec.x, vec.y, vec.z);
		}
		if (Input.isKeyDown(GLFW_KEY_Q)) {
			Mat4 mat = mat4().rotate(vec4(cam.front.x, cam.front.y, cam.front.z, -20f * Engine.deltaTime));
			Vec4 vec = mat.multiply(vec4(cam.up.x, cam.up.y, cam.up.z, 1.0f));
			cam.up = vec3(vec.x, vec.y, vec.z);
		}
		
		float cameraSpeed = WASD_SENSITIVITY * Engine.deltaTime * ((float)Input.scrollingAmount * (float)Input.scrollingAmount * 0.05f);
	    if(Input.isKeyDown(GLFW_KEY_W)) {
	    	cam.position = cam.position.add((cam.front.multiply(cameraSpeed)));
	    }
	    if(Input.isKeyDown(GLFW_KEY_S)) {
	    	cam.position = cam.position.subtract((cam.front.multiply(cameraSpeed)));
	    }
	    if(Input.isKeyDown(GLFW_KEY_A)) {
	    	cam.position = cam.position.subtract(right.multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_D)) {
	    	cam.position = cam.position.add(right.multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_R)) {
	    	cam.position = cam.position.add(cam.up.multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_F)) {
	    	cam.position = cam.position.subtract(cam.up.multiply(cameraSpeed));
	    }
	}
}
