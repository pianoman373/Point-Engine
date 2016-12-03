package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.swing.JOptionPane;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.opengl.ARBSeamlessCubeMap;
import org.lwjgl.opengl.GL;

import com.team.engine.rendering.Framebuffer;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.Primitives;
import com.team.engine.rendering.Shader;
import com.team.engine.rendering.Tessellator;
import com.team.engine.rendering.Texture;
import com.team.engine.vecmath.Vec2i;

/**
 * This is the main class of the engine. All you really need to do here is call start() and the
 * engine takes everything over, starting the game loop and just about everything.
 *
 * note: You cannot call any opengl functions before first calling start() since it starts up OpenGL.
 */
public class Engine {
	public static Camera camera;
	
	public static Mesh cubeMesh;
	public static Mesh debugCubeMesh;
	public static Mesh debugSphereMesh;
	public static Mesh spriteMesh;
	public static Mesh framebufferMesh;
	
	public static Tessellator tessellator;
	
	/** This is constantly updated every frame. It represents the time elapsed in seconds since the last frame.
	 * It is usually less than 0 (unless you have serious lag). It should be used for any physics and movement
	 * related functions. Multiply the distance traveled by the delta time every second, and you will always travel
	 * that distance in exactly one second.
	 */
	public static float deltaTime = 0.0f;
	
	/** turn on to render everything in wireframe */
	public static boolean wireframe = false;
	
	/** The main Scene object you should use */
	public static Scene scene;
	
	public static boolean is2d;
	public static boolean isVR;
	
	/** The pointer to the window object being used. Used for glfw stuff */
	public static long window;
	
	/** The frame buffer of the final rendered image before post processing. */
	public static Framebuffer fbuffer;

	
	private static Shader framebufferShader;
	private static Framebuffer pingPong1;
	private static Framebuffer pingPong2;
	private static Framebuffer pingPong3;
	private static Mesh skyboxMesh;
	private static float lastFrame = 0.0f;
	private static AbstractGame game;
	
	
	protected static HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	protected static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	protected static HashMap<String, Audio> sounds = new HashMap<String, Audio>();
	
	/**
	 * Sets a shader to be used for post-processing.
	 */
	public static void setFramebuffer(Shader shader) {
		framebufferShader = shader;
	}

	/**
	 * This is what kicks off the whole thing. You usually call this from main and let the engine do the work.
	 * 
	 * @param is2D basically changes how the whole engine sets itself up. If true then Engine.camera will be an OrthographicCamera
	 * instance. Post processing will default use a simple shader, and overall it will just be more setup for a 2D game.
	 * 
	 * TODO: Maybe one day this option can be removed. I don't really like separating 2D from 3D so much. Sometimes
	 * they need to mix.
	 * 
	 * @param vr enables the VRManager to be plugged into the engine. Windows only currently (even if it was setup for linux
	 * steamvr doesn't work anyways....).
	 * 
	 * @param g Whatever you want that extends AbstractGame. Most of the time this is your main game class. It's your primary method
	 * of communication with the engine.
	 */
	public static void start(boolean is2D, boolean vr, AbstractGame g) {
		game = g;
		is2d = is2D;
		isVR = vr;
		
		setupContext();
		init();
		
		//variables for calculating delta time and fps
		float time = 0;
		lastFrame = (float)glfwGetTime();
		int fps = 0;
		
		//the main game loop
		while (!glfwWindowShouldClose(window)) {
			//limit fps
			if ((float)glfwGetTime()-lastFrame <= 1.0f/Settings.MAX_FPS) {
				//if not enough time has elapsed, we start the loop again
				continue;
			}
			
			double begin = glfwGetTime();
			
			//calculate fps
			time += deltaTime;
			++fps;
			
			//calculate delta time
			float currentFrame = (float)glfwGetTime();
			deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;
			
			update();
			render();
			
			double nanoseconds = ((glfwGetTime() - begin) * 1000);
			
			//display fps every second
			if (time >= 1.0f) {
				time = 1.0f - time;
				
				glfwSetWindowTitle(window, ("Game Engine FPS: " + fps + " Delta: " + nanoseconds + " nanoseconds"));
				fps = 0;
			}
		}
		
		end();
	}
	
	private static void init() {
		//load all our vital shaders
		loadShader("hdr");
		loadShader("framebuffer");
		loadShader("blur");
		loadShader("skybox");
		loadShader("light");
		loadShader("shadow");
		loadShader("sprite");
		loadShader("debug");
		loadShader("pbr");
		loadShader("pbr-specular");
		loadShader("gui");
		loadShader("color");

		//vital meshes
		framebufferMesh = Mesh.raw(Primitives.framebuffer(), false);
		skyboxMesh = Mesh.raw(Primitives.skybox(), false);
		cubeMesh = Mesh.raw(Primitives.cube(1.0f), true);
		debugCubeMesh = Primitives.debugCube();
		debugSphereMesh = Primitives.debugSphere(64);
		spriteMesh = Mesh.raw(Primitives.sprite(vec2(0, 0), vec2(1, 1)), true);

		//initialize the main framebuffer
		fbuffer = Framebuffer.HdrWithBloom(new Vec2i(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT));
		if (Settings.ENABLE_BLOOM) {
			//if bloom is enabled set those framebuffers up too
			pingPong1 = Framebuffer.standard(new Vec2i(Settings.WINDOW_WIDTH / 2, Settings.WINDOW_HEIGHT / 2), false);
			pingPong2 = Framebuffer.standard(new Vec2i(Settings.WINDOW_WIDTH / 4, Settings.WINDOW_HEIGHT / 4), false);
			pingPong3 = Framebuffer.standard(new Vec2i(Settings.WINDOW_WIDTH / 8, Settings.WINDOW_HEIGHT / 8), false);
		}

		setupOpenglState();

		tessellator = new Tessellator();
		scene = new Scene();
		
		//create the camera depending on what mode we're in
		if (is2d) {
			camera = new OrthographicCamera();
			framebufferShader = getShader("framebuffer");
		}
		else {
			camera = new SpaceCamera();
			framebufferShader = getShader("hdr");
		}
		
		//initialize the game instance (A.K.A call init on the demo class)
		game.init();
	}
	
	private static void update() {
		//refresh key/mouse/etc input
		glfwPollEvents();
		
		scene.update();
		game.update();
		//TODO: why is camera getting it's own update and why is it last?
		camera.update();
		
		game.postUpdate();
		
		if (isVR) {
			VRManager.update();
		}
	}
	
	private static void render() {
		//clear the actual screen color with black
		clear(0, 0, 0, 1.0f);
		
		if (!is2d && scene.sun.castShadow) {
			//we render the scene from the shadow-caster's point of view
			scene.sun.shadowBuffer.bind();
			clear(0, 0, 0, 1.0f);
			
			glViewport(0, 0, scene.sun.shadowResolution, scene.sun.shadowResolution);
		
			Shader s = getShader("shadow");
			s.bind();
			s.uniformMat4("lightSpace", scene.sun.getShadowMat());
			
			//tell the main game to render any shadow casters now
			game.renderShadow(s);
		
			scene.renderShadow(s);
		}
		
		renderWorld(fbuffer, camera);
		
		if (Settings.ENABLE_BLOOM) {	
			doBloom();
		}

		//we unbind framebuffers which means the output of this next render will go to the screen
		Framebuffer.unbind();
		
		
		//here we bind the post proccessing shader and render the framebuffer to the screen and apply bloom and hdr if using hdr shaders
		framebufferShader.bind();
		framebufferShader.uniformFloat("exposure", 1.0f);
		game.postRenderUniforms(framebufferShader);
		
		fbuffer.tex[0].bind(0);
		framebufferShader.uniformInt("screenTexture", 0);
		
		framebufferShader.uniformBool("doBloom", Settings.ENABLE_BLOOM);
		if (Settings.ENABLE_BLOOM) {
			pingPong1.tex[0].bind(1);
			pingPong2.tex[0].bind(2);
			pingPong3.tex[0].bind(3);
			
			framebufferShader.uniformInt("bloomTexture1", 1);
			framebufferShader.uniformInt("bloomTexture2", 2);
			framebufferShader.uniformInt("bloomTexture3", 3);
		}
		
		framebufferMesh.draw();
		
		if (isVR) {
			VRManager.postRender();
		}

		//now we swap buffers which updates the window
		glfwSwapBuffers(window);
	}
	
	public static void doBloom() {
		Shader s = getShader("blur");
		s.bind();
		
		float bloomRange = 1f;
		
		//level 1 blur
		glViewport(0, 0, Settings.WINDOW_WIDTH / 2, Settings.WINDOW_HEIGHT / 2);
		pingPong1.bind();
		
		clear(0, 0, 0, 1.0f);
		
		s.uniformVec2("offset", vec2(bloomRange / (Settings.WINDOW_WIDTH / 2), 0));
		fbuffer.tex[1].bind();
		framebufferMesh.draw();
		
		pingPong1.tex[0].bind();
		s.uniformVec2("offset", vec2(0f, bloomRange / (Settings.WINDOW_WIDTH / 2)));
		framebufferMesh.draw();
		
		//level 2 blur
		glViewport(0, 0, Settings.WINDOW_WIDTH / 4, Settings.WINDOW_HEIGHT / 4);
		pingPong2.bind();
		
		clear(0, 0, 0, 1.0f);
		
		s.uniformVec2("offset", vec2(bloomRange / (Settings.WINDOW_WIDTH / 4), 0));
		pingPong1.tex[0].bind();
		framebufferMesh.draw();
		
		pingPong2.tex[0].bind();
		s.uniformVec2("offset", vec2(0f, bloomRange / (Settings.WINDOW_WIDTH / 4)));
		framebufferMesh.draw();
		
		glViewport(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
		
		//level 3 blur
		glViewport(0, 0, Settings.WINDOW_WIDTH / 8, Settings.WINDOW_HEIGHT / 8);
		pingPong3.bind();
		
		clear(0, 0, 0, 1.0f);
		
		s.uniformVec2("offset", vec2(bloomRange / (Settings.WINDOW_WIDTH / 8), 0));
		pingPong2.tex[0].bind();
		framebufferMesh.draw();
		
		pingPong3.tex[0].bind();
		s.uniformVec2("offset", vec2(0f, bloomRange / (Settings.WINDOW_WIDTH / 8)));
		framebufferMesh.draw();
		
		glViewport(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
	}
	
	/**
	 * Renders the world from the perspective of the camera to the specified
	 * framebuffer. No post processing is done in this function.
	 */
	public static void renderWorld(Framebuffer target, Camera cam) {
		if (is2d)
			glDepthMask(false);
		
		target.bind();
		glViewport(0, 0, target.dimensions.x, target.dimensions.y);
		
		//clear the given framebuffer with the sky color
		clear(scene.skyColor.x, scene.skyColor.y, scene.skyColor.z, 1.0f);
		
		//first, render the skybox if there is one
		if (!is2d) {
			if (scene.skybox != null) {
				glDepthMask(false);
				getShader("skybox").bind();
				scene.skybox.bind();

				skyboxMesh.draw();

				glDepthMask(true);
			}
		}

		if (wireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		}
		
		//now render the scene stuff first
		scene.render(camera);
		
		//and then tell the game to render
		game.render();
		

		if (wireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		}
		
		if (is2d)
			glDepthMask(true);
	}

	/**
	 * does all that opengl context stuff at the very beginning.
	 */
	private static void setupContext() {
		print(System.getProperty("java.library.path"));

		//set up our window options
		glfwInit();
		//4x anti-aliasing
		glfwWindowHint(GLFW_SAMPLES, 4);
		//opengl version stuff
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		glfwSwapInterval(1);
		
		//here we create our window in fullscreen or a normal window
		if(Settings.FULLSCREEN) {
			long monitor = glfwGetPrimaryMonitor();
		    GLFWVidMode vidMode = glfwGetVideoMode(monitor);
		    Settings.WINDOW_WIDTH = vidMode.width();
		    Settings.WINDOW_HEIGHT = vidMode.height();
		    window = glfwCreateWindow(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT, "Game", monitor, NULL);
		}
		else {
			window = glfwCreateWindow(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT, "Game", NULL, NULL);
		}
		
		if (window == NULL) {
			//something went wrong
			JOptionPane.showMessageDialog(null, "Sorry, your graphics card is incompatible with openGL 3");
		    glfwTerminate();
		}
		
		glfwMakeContextCurrent(window);
		
		//tell input events to be sent to Input
		glfwSetKeyCallback(window, (long window, int key, int scancode, int action, int mode) -> Input.keyEvent(window, key, action));
		glfwSetCursorPosCallback(window, (long window, double xpos, double ypos) -> Input.cursorEvent(window, xpos, ypos));
		glfwSetMouseButtonCallback(window, (long window, int button, int action, int mods) -> Input.mouseEvent(window, button, action, mods));
		glfwSetScrollCallback(window, (long window, double arg1, double scrollAmount) -> Input.scrollEvent(window, scrollAmount));
		
		GL.createCapabilities();
		
		//setup openAL
		long device = ALC10.alcOpenDevice((ByteBuffer)null);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);

		long context = ALC10.alcCreateContext(device, (IntBuffer)null);
		ALC10.alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
		
		if (isVR) VRManager.setupVR();
	}
	
	/**
	 * Restores the opengl state to it's default.
	 */
	public static void setupOpenglState() {
		glUseProgram(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		glEnable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_SCISSOR_TEST);
		glEnable(GL_CULL_FACE);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(ARBSeamlessCubeMap.GL_TEXTURE_CUBE_MAP_SEAMLESS);
		
		if (is2d) {
			glDisable(GL_FRAMEBUFFER_SRGB); 
		}
		else {
			glEnable(GL_FRAMEBUFFER_SRGB); 
		}
	}
	
	/**
	 * Clears the currently bound framebuffer with the specified color.
	 */
	public static void clear(float r, float g, float b, float a) {
		glClearColor(r, g, b, a);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	private static void end() {
		game.kill();
		
		fbuffer.delete();
		
		VRManager.delete();
		
		glfwTerminate();
		System.exit(0);
	}
}
