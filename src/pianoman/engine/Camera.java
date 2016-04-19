package pianoman.engine;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import pianoman.engine.vecmath.Vec3;

public class Camera {
	public Vec3 position = new Vec3(0.0f, 0.0f, 5.0f);
	public Vec3 front = new Vec3(0.0f, 0.0f, 0.0f);
	public float pitch = 0.0f;
	public float yaw = -90.0f;
	public Vec3 up = new Vec3(0.0f, 1.0f, 0.0f);
	
	private static float lastX = 400;
	private static float lastY = 300;
	
	public void update() {
		front.x = (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
		front.y = (float) Math.sin(Math.toRadians(pitch));
		front.z = (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
		
		front = front.normalize();

		
		float cameraSpeed = 5.0f * Engine.deltaTime;
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

			Engine.camera.yaw   += xoffset;
			Engine.camera.pitch += yoffset;  

			if(Engine.camera.pitch > 89.0f)
				Engine.camera.pitch =  89.0f;
			if(Engine.camera.pitch < -89.0f)
				Engine.camera.pitch = -89.0f;
		}
		
		lastX = (float)Input.mousePos.x;
		lastY = (float)Input.mousePos.y;
	}
}
