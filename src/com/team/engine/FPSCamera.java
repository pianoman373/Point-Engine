package com.team.engine;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;

public class FPSCamera extends Camera {
	public Vec3 position = vec3(0.0f, 0.0f, 15.0f);
	public Vec3 front = vec3(0.0f, 0.0f, -1.0f);
	public float pitch = 0.0f;
	public float yaw = -90.0f;
	public float roll = 0;
	public Vec3 up = vec3(0.0f, 1.0f, 0.0f);
	public static float WASD_SENSITIVITY = 100f;
	
	private static final float MOUSE_SENSITIVITY = 7f;
	private static float lastX;
	private static float lastY;
	
	
	/**
	 * Called by the game loop every frame. Get's keyboard and mouse input and moves the camera accordingly
	 */
	public void update() {
		Vec3 right = front.cross(up);
					
		if (Input.scrollingAmount <= 1) Input.scrollingAmount = 1;
		
		if (Input.mouseGrabbed == true) {
			if(Input.firstMouse) {
				lastX = (float) Input.mousePos.x;
				lastY = (float) Input.mousePos.y;
				Input.firstMouse = false;
			}

			float xoffset = (float)Input.mousePos.x - lastX;
			float yoffset = lastY - (float)Input.mousePos.y;
			lastX = (float)Input.mousePos.x;
			lastY = (float)Input.mousePos.y;

			float sensitivity = MOUSE_SENSITIVITY * Engine.deltaTime;
			xoffset *= sensitivity;
			yoffset *= sensitivity;
			
			//vertical
			Mat4 mat = mat4().rotate(vec4(right.x, right.y, right.z, yoffset));
			Vec4 vec = mat.multiply(vec4(front.x, front.y, front.z, 1.0f));
			front = vec3(vec.x, vec.y, vec.z);
			
			//horizontal
			Mat4 mat2 = mat4().rotate(vec4(up.x, up.y, up.z, -xoffset));
			Vec4 vec3 = mat2.multiply(vec4(front.x, front.y, front.z, 1.0f));
			front = vec3(vec3.x, vec3.y, vec3.z);
		}
		
		lastX = (float)Input.mousePos.x;
		lastY = (float)Input.mousePos.y;
		
		float cameraSpeed = WASD_SENSITIVITY * Engine.deltaTime * ((float)Input.scrollingAmount * (float)Input.scrollingAmount * 0.05f);
	    if(Input.isKeyDown(GLFW_KEY_W)) {
	        position = position.add((vec3(front.x, 0, front.z).multiply(cameraSpeed)));
	    }
	    if(Input.isKeyDown(GLFW_KEY_S)) {
	    	position = position.subtract((vec3(front.x, 0, front.z).multiply(cameraSpeed)));
	    }
	    if(Input.isKeyDown(GLFW_KEY_A)) {
	    	position = position.subtract(vec3(right.x, 0, right.z).multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_D)) {
	    	position = position.add(vec3(right.x, 0, right.z).multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_R)) {
	    	position = position.add(up.multiply(cameraSpeed));
	    }
	    if(Input.isKeyDown(GLFW_KEY_F)) {
	    	position = position.subtract(up.multiply(cameraSpeed));
	    }
	    //print(acc);
	}

	@Override
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

	@Override
	public Mat4 getProjection() {
		
		if (Engine.isVR) {
			
			vr.HmdMatrix44_t mat = VRManager.lEyeProj;
			
			
			return mat4(
					vec4(mat.m[0], mat.m[4], mat.m[8], mat.m[12]),
					vec4(mat.m[1], mat.m[5], mat.m[9], mat.m[13]),
					vec4(mat.m[2], mat.m[6], mat.m[10], mat.m[14]),
					vec4(mat.m[3], mat.m[7], mat.m[11], mat.m[15]));
		}
		else {
			return Mat4.perspective(60.0f, (float)Settings.WINDOW_WIDTH/(float)Settings.WINDOW_HEIGHT, 0.1f, 10000000.0f);
		}
	}

	@Override
	public Vec3 getPosition() {
		return this.position;
	}

	@Override
	public Vec3 getDirection() {
		return this.front;
	}

	@Override
	public void setPosition(Vec3 pos) {
		this.position = pos;
	}

	@Override
	public void setDirection(Vec3 dir) {
		this.front = dir;
	}
}
