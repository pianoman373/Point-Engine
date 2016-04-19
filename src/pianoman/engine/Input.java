package pianoman.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import pianoman.engine.vecmath.Vec2;

public class Input {
	private static boolean isWireframe = false;
	
	
	public static boolean[] keys = new boolean[1024];
	public static Vec2 mousePos = new Vec2(0, 0);
	public static boolean firstMouse = true;
	public static boolean mouseGrabbed = false;
	
	public static boolean isKeyDown(int key) {
		return keys[key];
	}
	
	public static void keyEvent(long window, int key, int action) {
		if(action == GLFW_PRESS)
			keys[key] = true;
		else if(action == GLFW_RELEASE)
			keys[key] = false;  
		
		if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
			glfwSetWindowShouldClose(window, GL_TRUE);
		
		if(key == GLFW_KEY_M && action == GLFW_PRESS) {
			if (!isWireframe) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
				isWireframe = true;
			}
			else {
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				isWireframe = false;
			}
		}
	}
	
	public static void cursorEvent(long window, double xpos, double ypos) {
		mousePos = new Vec2((float)xpos, (float)ypos);
	}
	
	public static void mouseEvent(long window, int button, int action, int mods) {
		if (button == GLFW_MOUSE_BUTTON_1) {
			if (action == GLFW_PRESS) {
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
				firstMouse = true;
				mouseGrabbed = true;
			}
			if (action == GLFW_RELEASE) {
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
				mouseGrabbed = false;
			}
		}
	}
}

class KeyCallback extends GLFWKeyCallback {
	@Override
	public void invoke(long window, int key, int scancode, int action, int mode) {
		Input.keyEvent(window, key, action);
	}
}

class CursorCallback extends GLFWCursorPosCallback {
	@Override
	public void invoke(long window, double xpos, double ypos) {
		Input.cursorEvent(window, xpos, ypos);
	}
}

class MouseCallback extends GLFWMouseButtonCallback {
	@Override
	public void invoke(long window, int button, int action, int mods) {
		Input.mouseEvent(window, button, action, mods);
	}
	
}