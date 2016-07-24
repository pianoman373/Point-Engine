package com.team.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.file.Paths;

import javax.swing.JOptionPane;

import org.lwjgl.opengl.GL;

import com.team.engine.vecmath.Vec2i;
import com.team.engine.vecmath.Vec3;
/**
 * The main class of a game should extend this one. It contains Everything needed to set up a game loop, and the opengl context.
 * 
 * note: You cannot call any opengl functions before first calling initialize() since it starts up OpenGL.
 */
public abstract class Engine {
	private long window;
	
	public static Engine instance;
	
	public Vec3 ambient = new Vec3(0.2f, 0.2f, 0.2f);
	public Camera camera;
	
	private KeyCallback keyCallback;
	private CursorCallback cursorCallback;
	private MouseCallback mouseCallback;
	private ScrollCallback scrollCallback;
	
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
	
	public float deltaTime = 0.0f;
	private float lastFrame = 0.0f;
	
	private boolean is2d = false;
	
	public boolean wireframe = false;
	
	
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
	 
	/**
	 * This is what kicks off the whole thing. You usually call this from main and let the engine do the work.
	 */
	public void initialize(boolean is2d) {
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
		
		fbuffer = new Framebuffer(new Vec2i(1000, 800), 2, true);
		pingPong1 = new Framebuffer(new Vec2i(1000, 800), 1, false);
		pingPong2 = new Framebuffer(new Vec2i(1000, 800), 1, false);
		
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
		
		while (!glfwWindowShouldClose(window)) {
			glfwPollEvents();
			
			float currentFrame = (float) glfwGetTime();
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
			
			glfwSwapBuffers(window);
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
		System.setProperty("java.library.path", Paths.get("lib").toAbsolutePath().toString());
		
		System.out.println(System.getProperty("java.library.path"));
		
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
		
		keyCallback = new KeyCallback();
		cursorCallback = new CursorCallback();
		mouseCallback = new MouseCallback();
		scrollCallback = new ScrollCallback();
		
		glfwSetKeyCallback(window, keyCallback);
		glfwSetCursorPosCallback(window, cursorCallback);
		glfwSetMouseButtonCallback(window, mouseCallback);
		glfwSetScrollCallback(window, scrollCallback);
		
		GL.createCapabilities();
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
		glfwTerminate();
	}
}
