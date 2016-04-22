package com.team.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.file.Paths;

import javax.swing.JOptionPane;
import org.lwjgl.opengl.GLContext;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
/**
 * The main class of a game should extend this one. It contains Everything needed to set up a game loop, and the opengl context.
 * 
 * note: You cannot call any opengl functions before first calling initialize() since it starts up OpenGL.
 */
public abstract class Engine {
	private long window;
	
	public static Engine instance;
	
	public Mat4 view;
	public Mat4 projection = Mat4.perspective(45.0f, 1000/800, 0.1f, 1000.0f);
	
	public Vec3 ambient = new Vec3(0.2f, 0.2f, 0.2f);
	public Vec3 background = new Vec3(0.2f, 0.2f, 0.2f);
	public Camera camera = new Camera();
	
	private KeyCallback keyCallback;
	private CursorCallback cursorCallback;
	private MouseCallback mouseCallback;
	
	public float deltaTime = 0.0f;
	private float lastFrame = 0.0f;
	
	//these are all classes that the main game must inherit
	public abstract void setupGame();
	
	public abstract void tick();
	
	public abstract void render();
	 
	/**
	 * This is what kicks off the whole thing. You usually call this from main and let the engine do the work.
	 */
	public void initialize() {
		instance = this;
		setupContext();
		
		setupGame();
		
		glClearColor(background.x, background.y, background.z, 1.0f);
		glEnable(GL_DEPTH_TEST);  
		//glEnable(GL_CULL_FACE);
		
		//setupPhysics();
		
		while (glfwWindowShouldClose(window) != GL_TRUE) {
			glfwPollEvents();
			
			float currentFrame = (float) glfwGetTime();
			deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;
			
			view = Mat4.LookAt(camera.position, camera.position.add(camera.front), camera.up);
			
			this.update();
			this.clear();
			this.render();
			
			glfwSwapBuffers(window);
		}
		this.end();
	}
	
	private void update() {
		this.tick();
		
		camera.update();
	}
	
	/**
	 * does all that opengl context stuff at the very beginning.
	 */
	private void setupContext() {
		System.setProperty("org.lwjgl.librarypath", Paths.get("lib/native").toAbsolutePath().toString());
		
		glfwInit();
		glfwWindowHint(GLFW_SAMPLES, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		
		window = glfwCreateWindow(1000, 800, "Game", NULL, NULL);
		if (window == NULL) {
			JOptionPane.showMessageDialog(null, "Sorry, your graphics card is incompatible with openGL 3");
		    glfwTerminate();
		}
		
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();
		
		keyCallback = new KeyCallback();
		cursorCallback = new CursorCallback();
		mouseCallback = new MouseCallback();
		
		glfwSetKeyCallback(window, keyCallback);
		glfwSetCursorPosCallback(window, cursorCallback);
		glfwSetMouseButtonCallback(window, mouseCallback);
	}
	
	/**
	 * Clears the screen with the current clear color just before rendering.
	 * Think it's not that important? Comment it out and see for yourself ;)
	 */
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	private void end() {
		glfwTerminate();
	}
}
