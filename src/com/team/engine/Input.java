package com.team.engine;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import com.team.engine.vecmath.Vec2;

/**
 * This is the main class for receiving any sort of input.
 */
public class Input {
	private static boolean[] keys = new boolean[1024];
	private static boolean[] mouse = new boolean[10];
	
	public static Vec2 mousePos = vec2(0, 0);
	public static boolean firstMouse = true;
	public static boolean mouseGrabbed = false;
	public static double scrollingAmount = 0;
	
	/**
	 * Returns true if the specified key is down. Keys are from glfw, for 
	 * 
	 * Example: 
	 * 
	 * Input.isKeyDown(GLFW_KEY_A);
	 */
	public static boolean isKeyDown(int key) {
		return keys[key];
	}
	
	/**
	 * Returns true if the specified mouse button is down.
	 */
	public static boolean isButtonDown(int key) {
		return mouse[key];
	}
	
	public static boolean isControllerPresent() {
		return glfwGetJoystickName(0) != null;
	}
	
	public static String getControllerName() {
		return glfwGetJoystickName(0);
	}
	
	public static float getJoystickAxis(int axis) {
		if (isControllerPresent()) {
			FloatBuffer axes = glfwGetJoystickAxes(0);
			
			return axes.get(axis);
		}
		
		return 0.0f;
	}
	
	public static boolean getJoystickButton(int button) {
		if (isControllerPresent()) {
			ByteBuffer buttons = glfwGetJoystickButtons(0);
			
			if (buttons.get(button) == 0) {
				return false;
			}
			else {
				return true;
			}
		}
		return false;
	}
	
	protected static void keyEvent(long window, int key, int action) {
		if(action == GLFW_PRESS)
			keys[key] = true;
		else if(action == GLFW_RELEASE)
			keys[key] = false;  
		
		if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
			glfwSetWindowShouldClose(window, true);
		
		if(key == GLFW_KEY_F1 && action == GLFW_PRESS) {
			Engine.wireframe = !Engine.wireframe;
		}
		if(key == GLFW_KEY_F2 && action == GLFW_PRESS) {
			Engine.scene.debug = !Engine.scene.debug;
		}
	}
	
	protected static void cursorEvent(long window, double xpos, double ypos) {
		mousePos = vec2((float)xpos, (float)ypos);
	}
	
	protected static void mouseEvent(long window, int button, int action, int mods) {
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
		
		if (action == GLFW_PRESS) {
			mouse[button] = true;
		}
		if (action == GLFW_RELEASE) {
			mouse[button] = false;
		}
	}
	
	protected static void scrollEvent(long window, double scrollAmount) {
		scrollingAmount += scrollAmount;
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

class ScrollCallback extends GLFWScrollCallback {

	@Override
	public void invoke(long window, double arg1, double scrollAmount) {
		Input.scrollEvent(window, scrollAmount);
	}
	
}