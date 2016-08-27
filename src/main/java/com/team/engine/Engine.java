package com.team.engine;

import static org.lwjgl.opengl.GL11.*;

import java.nio.file.Paths;
import java.util.HashMap;

import javax.swing.JOptionPane;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2i;
import com.team.engine.vecmath.Vec3;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
/**
 * The main class of a game should extend this one. It contains Everything needed to set up a game loop, and the opengl context.
 *
 * note: You cannot call any opengl functions before first calling start() since it starts up OpenGL.
 */
public abstract class Engine {
	public static int WINDOW_WIDTH = 1280;
	public static int WINDOW_HEIGHT = 720;
	public static int SHADOW_RESOLUTION = 4096;
	public static final boolean FULLSCREEN = false;

	public static Camera camera;
	/** The skybox that will be automatically rendered in the background. 
	 * Can be null to use background color instead */
	public static Cubemap skybox = null;
	/** Background color if skybox is null */
	public static Vec3 background = new Vec3(0.0f, 0.0f, 0.0f);
	/** all currently plugged in controller objects */
	public static Controller[] controllers;
	/** The buffer containing the shadow map, you can use it to bind the shadowmap texture */
	public static Framebuffer shadowBuffer;
	public static Mesh cubeMesh;
	/** This is constantly updated every frame. It represents the time elapsed in seconds since the last frame.
	 * It is usually less than 0 (unless you have serious lag). It should be used for any physics and movement
	 * related functions. Multiply the distance traveled by the delta time every second, and you will always travel
	 * that distance in exactly one second.
	 */
	public static float deltaTime = 0.0f;
	/** turn on to render everything in wireframe */
	public static boolean wireframe = false;

	private static Shader framebufferShader;
	private static long window;
	private static Framebuffer fbuffer;
	private static Framebuffer pingPong1;
	private static Framebuffer pingPong2;
	private static Mesh framebufferMesh;
	private static Mesh skyboxMesh;
	private static KeyCallback keyCallback;
	private static CursorCallback cursorCallback;
	private static MouseCallback mouseCallback;
	private static ScrollCallback scrollCallback;
	private static float lastFrame = 0.0f;
	private static HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	private static AbstractGame game;
	
	/**
	 * Sets a shader to be used for post-processing.
	 */
	public static void setFramebuffer(Shader shader) {
		framebufferShader = shader;
	}
	
	/**
	 * Gets the matrix (model * view) of the current shadow.
	 */
	public static Mat4 getShadowMat() {
		Vec3 offsetPosition = camera.getPosition().add(camera.getDirection().multiply(29f));
		return Mat4.orthographic(-30.0f, 30.0f, -30.0f, 30.0f, -30.0f, 30.0f).multiply(Mat4.LookAt(offsetPosition, offsetPosition.add(new Vec3(0.7f, -1.0f, 1.0f)), new Vec3(0.0f, 1.0f, 0.0f)));
	}

	/**
	 * This is what kicks off the whole thing. You usually call this from main and let the engine do the work.
	 */
	public static void start(boolean is2d, AbstractGame g) {
		//the contents of this function are the backbone of the engine. It contains the main loop
		//and all the rendering. I will comment through every step.
		game = g;
		setupContext();

		//load all our vital shaders
		loadShader("hdr");
		loadShader("blur");
		loadShader("skybox");
		loadShader("light");
		loadShader("shadow");
		
		//set hdr as default post-processing
		framebufferShader = getShader("hdr");

		//vital meshes
		framebufferMesh = new Mesh(Primitives.framebuffer());
		skyboxMesh = new Mesh(Primitives.skybox());
		cubeMesh = new Mesh(Primitives.cube(1.0f));

		//all the framebuffers, one for shadows, one for normal rendering, and 2 ping pong shaders for bloom
		shadowBuffer = Framebuffer.shadow(new Vec2i(SHADOW_RESOLUTION, SHADOW_RESOLUTION));
		fbuffer = Framebuffer.standard(new Vec2i(WINDOW_WIDTH, WINDOW_HEIGHT), 2, true);
		pingPong1 = Framebuffer.standard(new Vec2i(WINDOW_WIDTH, WINDOW_HEIGHT), 1, false);
		pingPong2 = Framebuffer.standard(new Vec2i(WINDOW_WIDTH, WINDOW_HEIGHT), 1, false);

		//setup our opengl states
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		//create the camera depending on what mode we're in
		if (is2d) {
			camera = new OrthographicCamera();
		}
		else {
			camera = new FPSCamera();
		}
		
		//initialize the main game
		game.init();
		
		//set the color used for clear as the background color
		glClearColor(background.x, background.y, background.z, 1.0f);
		
		//create values for calculating delta time
		float time = 0;
		lastFrame = (float)glfwGetTime();
		int fps = 0;

		while (!glfwWindowShouldClose(window)) {
			//refresh input
			glfwPollEvents();
			for (Controller c : controllers) {
				c.poll();
			}
			
			//calculate delta time
			float currentFrame = (float)glfwGetTime();
			deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;
			
			//calculate fps
			time += deltaTime;
			++fps;
			
			//display fps every second
			if (time >= 1.0f) {
				time = 1.0f - time;
				glfwSetWindowTitle(window, ("Game Engine FPS: " + fps));
				fps = 0;
			}

			//tick the main game and update camera
			game.tick();
			camera.update();
			
			//now we render the scene from the shadow-caster's point of view
			shadowBuffer.bind();
			glViewport(0, 0, SHADOW_RESOLUTION, SHADOW_RESOLUTION);
			clear();
			
			Shader s = getShader("shadow");
			s.bind();
			s.uniformMat4("lightSpace", getShadowMat());
			
			//tell the main game to render any shadow casters now
			glCullFace(GL_FRONT);
			game.renderShadow(s);
			glCullFace(GL_BACK);
			
			//bind the main rendering buffer and now we're ready to render normally
			fbuffer.bind();
			glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
			clear();

			//first, render the skybox if there is one
			if (skybox != null) {
				glDepthMask(false);
				getShader("skybox").bind();
				skybox.bind();

				skyboxMesh.draw();

				glDepthMask(true);
			}

			if (wireframe) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			}

			//and finally, tell the game to render, this goes to the framebuffer not the screen
			game.render();

			if (wireframe) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			}

			//take the output of our render and blur it
			doBloom();

			//we unbind framebuffers which means the output of this render will go to the screen
			Framebuffer.unbind();
			clear();
			
			//here we bind the post proccessing shader and render the framebuffer to the screen and apply bloom and hdr if using hdr shaders
			game.postRenderUniforms(framebufferShader);
			framebufferShader.bind();
			pingPong2.tex[0].bind(1);
			fbuffer.tex[0].bind(0);
			framebufferShader.uniformInt("screenTexture", 0);
			framebufferShader.uniformInt("bloomTexture", 1);

			//draw it all to the screen
			framebufferMesh.draw();

			//now we swap buffers which updates the window
			glfwSwapBuffers(window);
		}
		
		//if the loop ends, die peacefully
		end();
	}
	
	/**
	 * basically just blurs the second framebuffer
	 */
	private static void doBloom() {
		boolean horizontal = true;
		boolean first = true;
		int amount = 10;

		for (int i = 0; i < amount; i++) {
			if (horizontal) {
				pingPong1.bind();
			}
			else {
				pingPong2.bind();
			}
			Shader s = getShader("blur");
			s.bind();
			s.uniformBool("horizontal", horizontal);

			if (first) {
				fbuffer.tex[1].bind();
			}
			else {
				if (horizontal) {
					pingPong2.tex[0].bind();
				}
				else {
					pingPong1.tex[0].bind();
				}
			}

			framebufferMesh.draw();

			horizontal = !horizontal;

			if (first) {
				first = false;
			}
		}

		Framebuffer.unbind();
	}

	/**
	 * does all that opengl context stuff at the very beginning.
	 */
	private static void setupContext() {
		System.setProperty("org.lwjgl.librarypath", Paths.get("native").toAbsolutePath().toString());
		System.out.println(System.getProperty("java.library.path"));

		//set up our window options
		glfwInit();
		//4x anti-aliasing
		glfwWindowHint(GLFW_SAMPLES, 4);
		//opengl version stuff
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		
		//here we create our window in fullscreen or a normal window
		if(FULLSCREEN) {
			long monitor = glfwGetPrimaryMonitor();
		    GLFWVidMode vidMode = glfwGetVideoMode(monitor);
		    WINDOW_WIDTH = vidMode.width();
		    WINDOW_HEIGHT = vidMode.height();
		    window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Game", monitor, NULL);
		}
		else {
			window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Game", NULL, NULL);
		}
		
		if (window == NULL) {
			//something went wrong
			JOptionPane.showMessageDialog(null, "Sorry, your graphics card is incompatible with openGL 3");
		    glfwTerminate();
		}
		
		glfwMakeContextCurrent(window);
				
		//create and set all the event callbacks
		keyCallback = new KeyCallback();
		cursorCallback = new CursorCallback();
		mouseCallback = new MouseCallback();
		scrollCallback = new ScrollCallback();
		
		glfwSetKeyCallback(window, keyCallback);
		glfwSetCursorPosCallback(window, cursorCallback);
		glfwSetMouseButtonCallback(window, mouseCallback);
		glfwSetScrollCallback(window, scrollCallback);
		
		//set controllers
		controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		//and finally create the opengl context and we're ready to go
		GL.createCapabilities();
	}

	/**
	 * Clears the currently bound framebuffer.
	 */
	public static void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	private static void end() {
		game.kill();
		glfwTerminate();
		System.exit(0);
	}

	/**
	 * This will load a shader from the specified path on disk into memory.
	 * This does NOT bind the shader or even return the shader. Use getShader for that.
	 */
	public static void loadShader(String path) {
		Shader s = new Shader("shaders/" + path);
		shaders.put(path, s);
	}
	
	/**
	 * This will return a shader object from memory ONLY if it has been loaded with loadShader.
	 * The object will only be returned, you will have to bind it yourself.
	 */
	public static Shader getShader(String path) {
		return shaders.get(path);
	}

	/**
	 * This will load a texture from the specified path on disk into memory.
	 * This does NOT bind the texture or even return the texture. Use getTexture for that.
	 */
	public static void loadTexture(String path, boolean pixelated) {
		Texture s = new Texture("textures/" + path, pixelated);
		textures.put(path, s);
	}
	
	public static void loadTexture(String path) {
		loadTexture(path, false);
	}
	
	/**
	 * This will return a texture object from memory ONLY if it has been loaded with loadTexture.
	 * The object will only be returned, you will have to bind it yourself.
	 */
	public static Texture getTexture(String path) {
		return textures.get(path);
	}
}
