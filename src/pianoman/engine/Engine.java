package pianoman.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.file.Paths;

import javax.swing.JOptionPane;
import org.lwjgl.opengl.GLContext;

import pianoman.engine.vecmath.Vec3;

public abstract class Engine {
	private static long window;
	
	public static Mat4 view;
	public static Mat4 projection = Mat4.perspective(45.0f, 1000/800, 0.1f, 100.0f);
	public static Vec3 ambient = new Vec3(0.2f, 0.2f, 0.2f);
	public static Vec3 background = new Vec3(0.2f, 0.2f, 0.2f);
	public static Camera camera = new Camera();
	
	private static KeyCallback keyCallback;
	private static CursorCallback cursorCallback;
	private static MouseCallback mouseCallback;
	
	public static float deltaTime = 0.0f;
	private static float lastFrame = 0.0f;
	
	public abstract void setupGame();
	
	public abstract void tick();
	
	public abstract void render();
	 
	public void initialize() {
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
	
	private static void setupContext() {
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
	
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	private void end() {
		glfwTerminate();
	}
}
