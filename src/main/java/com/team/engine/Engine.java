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
 * note: You cannot call any opengl functions before first calling initialize() since it starts up OpenGL.
 */
public abstract class Engine {
	public static int WINDOW_WIDTH = 1280;
	public static int WINDOW_HEIGHT = 720;
	public static final int SHADOW_RESOLUTION = 4096;
	public static final boolean FULLSCREEN = false;
	public static final int MAX_FPS = 60;

	public static Camera camera;

	public static Shader framebufferShader;
	
	public static Cubemap skybox = null;
	public static Vec3 background = new Vec3(0.0f, 0.0f, 0.0f);
	
	public static Controller[] controllers;

	private static Framebuffer fbuffer;
	private static Framebuffer pingPong1;
	private static Framebuffer pingPong2;
	public static Framebuffer shadowBuffer;
	private static Mesh framebufferMesh;
	private static Mesh skyboxMesh;
	public static Mesh cubeMesh;
	
	private static KeyCallback keyCallback;
	private static CursorCallback cursorCallback;
	private static MouseCallback mouseCallback;
	private static ScrollCallback scrollCallback;
	
	private static long window;

	public static float deltaTime = 0.0f;
	private static float lastFrame = 0.0f;

	public static boolean wireframe = false;

	private static HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	
	private static AbstractGame game;
	

	public static void setFramebuffer(Shader shader) {
		framebufferShader = shader;
	}
	
	public static Mat4 getShadowMat() {
		Vec3 offsetPosition = camera.getPosition().add(camera.getDirection().multiply(29f));
		return Mat4.orthographic(-30.0f, 30.0f, -30.0f, 30.0f, -30.0f, 30.0f).multiply(Mat4.LookAt(offsetPosition, offsetPosition.add(new Vec3(0.7f, -1.0f, 1.0f)), new Vec3(0.0f, 1.0f, 0.0f)));
	}

	/**
	 * This is what kicks off the whole thing. You usually call this from main and let the engine do the work.
	 */
	public static void start(boolean is2d, AbstractGame g) {
		game = g;
		setupContext();

		loadShader("framebuffer");
		loadShader("blur");
		loadShader("skybox");
		loadShader("light");
		loadShader("shadow");
		
		framebufferShader = getShader("framebuffer");

		framebufferMesh = new Mesh(Primitives.framebuffer());
		skyboxMesh = new Mesh(Primitives.skybox());
		cubeMesh = new Mesh(Primitives.cube(1.0f));

		shadowBuffer = new Framebuffer(new Vec2i(SHADOW_RESOLUTION, SHADOW_RESOLUTION));
		fbuffer = new Framebuffer(new Vec2i(WINDOW_WIDTH, WINDOW_HEIGHT), 2, true);
		pingPong1 = new Framebuffer(new Vec2i(WINDOW_WIDTH, WINDOW_HEIGHT), 1, false);
		pingPong2 = new Framebuffer(new Vec2i(WINDOW_WIDTH, WINDOW_HEIGHT), 1, false);

		game.init();

		glClearColor(background.x, background.y, background.z, 1.0f);
		glEnable(GL_DEPTH_TEST);
		//glDepthFunc(GL_LESS);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		//glEnable(GL_CULL_FACE);

		//setupPhysics();

		if (is2d) {
			camera = new OrthographicCamera();
		}
		else {
			camera = new FPSCamera();
		}
		
		float time = 0;
		
		lastFrame = (float)glfwGetTime();
		int fps = 0;

		while (!glfwWindowShouldClose(window)) {
			
			glfwPollEvents();
			
			for (Controller c : controllers) {
				c.poll();
			}
			
			float currentFrame = (float)glfwGetTime();
			deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;
			
			time += deltaTime;
			++fps;
			
			if (time >= 1.0f) {
				time = 1.0f - time;
				glfwSetWindowTitle(window, ("Game Engine FPS: " + fps));
				fps = 0;
			}

			update();
			
			shadowBuffer.bind();
			glViewport(0, 0, SHADOW_RESOLUTION, SHADOW_RESOLUTION);
			clear();
			
			Shader s = getShader("shadow");
			s.bind();
			s.uniformMat4("camView", camera.getView());
			s.uniformMat4("camProjection", camera.getProjection());
			s.uniformMat4("view", new Mat4());
			s.uniformMat4("projection", getShadowMat());
			
			//s.uniformMat4("view", this.camera.getView());
			//s.uniformMat4("projection", this.camera.getProjection());
			
			glCullFace(GL_FRONT);
			game.renderShadow(s);
			glCullFace(GL_BACK);
			
			fbuffer.bind();
			glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
			clear();

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

			game.render();

			if (wireframe) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			}

			doBloom();

			Framebuffer.unbind();
			clear();
			framebufferShader.bind();
			game.postRenderUniforms(framebufferShader);
			pingPong2.tex[0].bind(1);
			fbuffer.tex[0].bind(0);
			//shadowBuffer.tex[0].bind(0);
			framebufferShader.uniformInt("screenTexture", 0);
			framebufferShader.uniformInt("bloomTexture", 1);

			framebufferMesh.draw();

			glfwSwapBuffers(window);
		}
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
				//this.clear();
			}
			else {
				pingPong2.bind();
				//this.clear();
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

	private static void update() {
		game.tick();

		camera.update();
	}

	/**
	 * does all that opengl context stuff at the very beginning.
	 */
	private static void setupContext() {
		System.setProperty("org.lwjgl.librarypath", Paths.get("native").toAbsolutePath().toString());

		System.out.println(System.getProperty("java.library.path"));

		glfwInit();
		glfwWindowHint(GLFW_SAMPLES, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		
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
			JOptionPane.showMessageDialog(null, "Sorry, your graphics card is incompatible with openGL 3");
		    glfwTerminate();
		}
		
		glfwMakeContextCurrent(window);
				
		keyCallback = new KeyCallback();
		cursorCallback = new CursorCallback();
		mouseCallback = new MouseCallback();
		scrollCallback = new ScrollCallback();
		
		glfwSetKeyCallback(window, keyCallback);
		glfwSetCursorPosCallback(window, cursorCallback);
		glfwSetMouseButtonCallback(window, mouseCallback);
		glfwSetScrollCallback(window, scrollCallback);
		
		controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		GL.createCapabilities();
	}

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
