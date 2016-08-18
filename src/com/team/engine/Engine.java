package com.team.engine;

import static org.lwjgl.opengl.GL11.*;

import java.nio.file.Paths;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.team.engine.vecmath.Vec2i;
import com.team.engine.vecmath.Vec3;
/**
 * The main class of a game should extend this one. It contains Everything needed to set up a game loop, and the opengl context.
 *
 * note: You cannot call any opengl functions before first calling initialize() since it starts up OpenGL.
 */
public abstract class Engine {
	public static final int WINDOW_WIDTH = 1000;
	public static final int WINDOW_HEIGHT = 800;



	public static Engine instance;

	public Camera camera;

	public Shader framebufferShader;
	public Shader blurShader;
	public Shader skyboxShader;
	public Shader lightShader;

	public Cubemap skybox = null;
	public Vec3 background = new Vec3(0.0f, 0.0f, 0.0f);

	private Framebuffer fbuffer;
	private Framebuffer pingPong1;
	private Framebuffer pingPong2;
	private Mesh framebufferMesh;
	private Mesh skyboxMesh;
	public Mesh cubeMesh;

	private static long startingTime;

	public float deltaTime = 0.0f;
	private float lastFrame = 0.0f;

	private boolean is2d = false;

	public boolean wireframe = false;

	private static HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();


	//these are all classes that the main game must inherit
	public abstract void setupGame();

	public abstract void tick();

	public abstract void render();

	public abstract void kill();

	/**
	 * Add any uniforms you want to the currently bound framebuffer shader before rendering.
	 */
	public abstract void postRenderUniforms(Shader shader);


	public void setFramebuffer(Shader shader) {
		this.framebufferShader = shader;
	}

	private float getTime() {
		return (System.nanoTime() - startingTime) / 1000000000.0f;
	}

	/**
	 * This is what kicks off the whole thing. You usually call this from main and let the engine do the work.
	 */
	public void initialize(boolean is2d) {
		startingTime = System.nanoTime();


		instance = this;
		this.is2d = is2d;
		setupContext();


		framebufferShader = new Shader("framebuffer");
		blurShader = new Shader("blur");
		skyboxShader = new Shader("skybox");
		lightShader = new Shader("light");

		framebufferMesh = new Mesh(Primitives.framebuffer());
		skyboxMesh = new Mesh(Primitives.skybox());
		cubeMesh = new Mesh(Primitives.cube(1.0f));

		fbuffer = new Framebuffer(new Vec2i(WINDOW_WIDTH, WINDOW_HEIGHT), 2, true);
		pingPong1 = new Framebuffer(new Vec2i(WINDOW_WIDTH, WINDOW_HEIGHT), 1, false);
		pingPong2 = new Framebuffer(new Vec2i(WINDOW_WIDTH, WINDOW_HEIGHT), 1, false);

		setupGame();

		glClearColor(background.x, background.y, background.z, 1.0f);
		glEnable(GL_DEPTH_TEST);
		//glDepthFunc(GL_LESS);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		//glEnable(GL_CULL_FACE);

		//setupPhysics();

		if (this.is2d) {
			this.camera = new OrthographicCamera();
		}
		else {
			this.camera = new FPSCamera();
		}

		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			float currentFrame = (float) getTime();
			deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;

			this.update();
			fbuffer.bind();
			this.clear();

			if (this.skybox != null) {
				glDepthMask(false);
				skyboxShader.bind();
				skybox.bind();

				skyboxMesh.draw();

				skyboxShader.unBind();
				glDepthMask(true);
			}

			if (wireframe) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			}

			this.render();

			if (wireframe) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			}

			doBloom();

			Framebuffer.unbind();
			this.clear();
			framebufferShader.bind();
			this.postRenderUniforms(framebufferShader);
			pingPong2.tex[0].bind(1);
			fbuffer.tex[0].bind(0);
			framebufferShader.uniformInt("screenTexture", 0);
			framebufferShader.uniformInt("bloomTexture", 1);

			framebufferMesh.draw();

			Display.update();
		}
		this.end();
	}

	private void doBloom() {
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
			blurShader.bind();
			blurShader.uniformBool("horizontal", horizontal);

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

	private void update() {
		this.tick();

		this.camera.update();
	}

	/**
	 * does all that opengl context stuff at the very beginning.
	 */
	private void setupContext() {
		System.setProperty("org.lwjgl.librarypath", Paths.get("lib/native").toAbsolutePath().toString());
		System.setProperty("java.library.path", System.getProperty("java.library.path") + ":" + "/home/joseph/Desktop");

		System.out.println(System.getProperty("java.library.path"));

		try {
			Display.setTitle("Game Engine");
			Display.setDisplayMode(new DisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT));
			Display.setFullscreen(false);

			Display.setVSyncEnabled(true);

			Display.create();

		} catch (LWJGLException e) {
			e.printStackTrace();
			System.err.println("Failed: "+e.getMessage());
		}

		try {
			Controllers.create();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Clears the screen with the current clear color just before rendering.
	 * Think it's not that important? Comment it out and see for yourself ;)
	 */
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	private void end() {
		kill();
		Display.destroy();
	}

	public static void loadShader(String path) {
		Shader s = new Shader(path);
		shaders.put(path, s);
	}

	public static Shader getShader(String path) {
		return shaders.get(path);
	}

	public static void loadTexture(String path) {
		Texture s = new Texture("resources/textures/" + path);
		textures.put(path, s);
	}

	public static Texture getTexture(String path) {
		return textures.get(path);
	}
}
