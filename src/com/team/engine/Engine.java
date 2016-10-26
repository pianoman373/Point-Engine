package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.swing.JOptionPane;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.BufferUtils;
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
import com.team.engine.rendering.Texture;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec2i;
import com.team.engine.vecmath.Vec3;

import vr.IVRCompositor_FnTable;
import vr.IVRSystem;
import vr.Texture_t;
import vr.TrackedDevicePose_t;
import vr.VR;
import vr.VREvent_t;
/**
 * The main class of a game should extend this one. It contains Everything needed to set up a game loop, and the opengl context.
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

	private static Shader framebufferShader;
	private static long window;
	public static Framebuffer fbuffer;
	private static Framebuffer pingPong1;
	private static Framebuffer pingPong2;
	private static Framebuffer pingPong3;
	private static Mesh skyboxMesh;
	private static KeyCallback keyCallback;
	private static CursorCallback cursorCallback;
	private static MouseCallback mouseCallback;
	private static ScrollCallback scrollCallback;
	private static float lastFrame = 0.0f;
	private static HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	private static HashMap<String, Audio> sounds = new HashMap<String, Audio>();
	private static AbstractGame game;
	
	//vr stuff
	public static boolean isVR;
	
	private static IVRCompositor_FnTable compositor;
	
	public static vr.HmdMatrix44_t lEyeProj;
	public static vr.HmdMatrix44_t rEyeProj;
	
	public static vr.HmdMatrix34_t lEyeView;
	public static vr.HmdMatrix34_t rEyeView;
	
	public static IntBuffer errorBuffer = BufferUtils.createIntBuffer(1);
	
	public static TrackedDevicePose_t.ByReference trackedDevicePosesReference = new TrackedDevicePose_t.ByReference();
	public TrackedDevicePose_t[] trackedDevicePose = (TrackedDevicePose_t[]) trackedDevicePosesReference.toArray(VR.k_unMaxTrackedDeviceCount);
	
	public static IVRSystem hmd;
	
	native static int sayHello();
	
	/**
	 * Sets a shader to be used for post-processing.
	 */
	public static void setFramebuffer(Shader shader) {
		framebufferShader = shader;
	}

	/**
	 * This is what kicks off the whole thing. You usually call this from main and let the engine do the work.
	 */
	public static void start(boolean i2d, boolean vr, AbstractGame g) {
		//the contents of this function are the backbone of the engine. It contains the main loop
		//and all the rendering. I will comment through every step.
		game = g;
		is2d = i2d;
		isVR = vr;
		setupContext();

		//load all our vital shaders
		loadShader("hdr");
		loadShader("framebuffer");
		loadShader("blur");
		loadShader("skybox");
		loadShader("light");
		loadShader("shadow");
		loadShader("sprite");
		loadShader("debug");
		
		loadTexture("ascii.png", true, false);

		//vital meshes
		framebufferMesh = Mesh.raw(Primitives.framebuffer(), false);
		skyboxMesh = Mesh.raw(Primitives.skybox(), false);
		cubeMesh = Mesh.raw(Primitives.cube(1.0f), true);
		debugCubeMesh = Primitives.debugCube();
		debugSphereMesh = Primitives.debugSphere(64);
		spriteMesh = Mesh.raw(Primitives.sprite(new Vec2(0, 0), new Vec2(1, 1)), true);

		//all the framebuffers, one for shadows, one for normal rendering, and 3 ping pong shaders for bloom
		fbuffer = Framebuffer.standard(new Vec2i(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT), 2, true);
		if (Settings.ENABLE_BLOOM) {
			pingPong1 = Framebuffer.standard(new Vec2i(Settings.WINDOW_WIDTH / 2, Settings.WINDOW_HEIGHT / 2), 1, false);
			pingPong2 = Framebuffer.standard(new Vec2i(Settings.WINDOW_WIDTH / 4, Settings.WINDOW_HEIGHT / 4), 1, false);
			pingPong3 = Framebuffer.standard(new Vec2i(Settings.WINDOW_WIDTH / 8, Settings.WINDOW_HEIGHT / 8), 1, false);
		}

		//setup our opengl states
		glEnable(GL_DEPTH_TEST);
		glEnable(ARBSeamlessCubeMap.GL_TEXTURE_CUBE_MAP_SEAMLESS);
		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		
		//setup the scene and it's physics
		scene = new Scene();
		
		//create the camera depending on what mode we're in
		if (is2d) {
			camera = new OrthographicCamera();
			framebufferShader = getShader("framebuffer");
		}
		else {
			camera = new FPSCamera();
			framebufferShader = getShader("hdr");
			glEnable(GL_FRAMEBUFFER_SRGB); 
		}
		scene.setupPhysics();
		
		//initialize the main game
		game.init();
			
		//create values for calculating delta time
		float time = 0;
		lastFrame = (float)glfwGetTime();
		int fps = 0;

		while (!glfwWindowShouldClose(window)) {
			//limit fps
			if ((float)glfwGetTime()-lastFrame <= 1.0f/Settings.MAX_FPS) {
				continue;
			}
			
			//clear the actual screen color
			glClearColor(0, 0, 0, 1.0f);
			clear();
			
			//calculate delta time
			float currentFrame = (float)glfwGetTime();
			deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;
			
			//refresh input
			glfwPollEvents();
			
			//calculate fps
			time += deltaTime;
			++fps;
			
			//display fps every second
			if (time >= 1.0f) {
				time = 1.0f - time;
				glfwSetWindowTitle(window, ("Game Engine FPS: " + fps));
				fps = 0;
			}
			
			if (isVR) {
				if (hmd == null) {
		        	System.out.println("HMD IS NULL!?!?!?!?!");
		        	return;
		        }
				
				// Process SteamVR events
				VREvent_t event = new VREvent_t();
				while (hmd.PollNextEvent.apply(event, event.size()) != 0) {
	        	
				}
			}

			
			scene.update();				
			
			game.tick();
			camera.update();
			
			//update matrices
			if (isVR) {
				lEyeProj = Engine.hmd.GetProjectionMatrix.apply(0, 0.1f, 10000000.0f, VR.EGraphicsAPIConvention.API_OpenGL);
				rEyeProj = Engine.hmd.GetProjectionMatrix.apply(1, 0.1f, 10000000.0f, VR.EGraphicsAPIConvention.API_OpenGL);
			
				lEyeView = Engine.hmd.GetEyeToHeadTransform.apply(0);
				rEyeView = Engine.hmd.GetEyeToHeadTransform.apply(1);
			}
			
			if (!is2d && scene.sun.castShadow) {
				//we render the scene from the shadow-caster's point of view
				scene.sun.shadowBuffer.bind();
				glViewport(0, 0, scene.sun.shadowResolution, scene.sun.shadowResolution);
				clear();
			
				Shader s = getShader("shadow");
				s.bind();
				s.uniformMat4("lightSpace", scene.sun.getShadowMat());
				
				//tell the main game to render any shadow casters now
				game.renderShadow(s);
			
				scene.renderShadow(s);
			}
			
			//do actual rendering to the fbuffer
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
			
			framebufferShader.uniformInt("screenTexture", 0);
			
			fbuffer.tex[0].bind(0);
			framebufferShader.uniformInt("screenTexture", 0);
			
			if (Settings.ENABLE_BLOOM) {
				framebufferShader.uniformBool("doBloom", true);
				pingPong1.tex[0].bind(1);
				pingPong2.tex[0].bind(2);
				pingPong3.tex[0].bind(3);
				
				framebufferShader.uniformInt("bloomTexture1", 1);
				framebufferShader.uniformInt("bloomTexture2", 2);
				framebufferShader.uniformInt("bloomTexture3", 3);
			}
			else {
				framebufferShader.uniformBool("doBloom", false);
			}
			
			framebufferMesh.draw();
			
			renderGui();
			
			if (isVR) {
				compositor.Submit.apply(0, new Texture_t(Engine.fbuffer.tex[0].id, VR.EGraphicsAPIConvention.API_OpenGL, VR.EColorSpace.ColorSpace_Gamma), null, VR.EVRSubmitFlags.Submit_Default);
			
				compositor.Submit.apply(1, new Texture_t(Engine.fbuffer.tex[0].id, VR.EGraphicsAPIConvention.API_OpenGL, VR.EColorSpace.ColorSpace_Gamma), null, VR.EVRSubmitFlags.Submit_Default);
				
				compositor.WaitGetPoses.apply(trackedDevicePosesReference, VR.k_unMaxTrackedDeviceCount, null, 0);
				
			}
			glFinish();

			//now we swap buffers which updates the window
			glfwSwapBuffers(window);
			
			glFlush();
			glFinish();
		}
		
		//if the loop ends, die peacefully
		end();
	}
	
	public static void doBloom() {
		Shader s = getShader("blur");
		s.bind();
		
		float bloomRange = 1.2f;
		
		//level 1
		glViewport(0, 0, Settings.WINDOW_WIDTH / 2, Settings.WINDOW_HEIGHT / 2);
		pingPong1.bind();
		
		glClearColor(0, 0, 0, 1.0f);
		clear();
		
		s.uniformVec2("offset", new Vec2(bloomRange / (Settings.WINDOW_WIDTH / 2), 0));
		fbuffer.tex[1].bind();
		framebufferMesh.draw();
		
		pingPong1.tex[0].bind();
		s.uniformVec2("offset", new Vec2(0f, bloomRange / (Settings.WINDOW_WIDTH / 2)));
		framebufferMesh.draw();
		
		//level 2
		glViewport(0, 0, Settings.WINDOW_WIDTH / 4, Settings.WINDOW_HEIGHT / 4);
		pingPong2.bind();
		
		glClearColor(0, 0, 0, 1.0f);
		clear();
		
		s.uniformVec2("offset", new Vec2(bloomRange / (Settings.WINDOW_WIDTH / 4), 0));
		pingPong1.tex[0].bind();
		framebufferMesh.draw();
		
		pingPong2.tex[0].bind();
		s.uniformVec2("offset", new Vec2(0f, bloomRange / (Settings.WINDOW_WIDTH / 4)));
		framebufferMesh.draw();
		
		glViewport(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
		
		//level 2
		glViewport(0, 0, Settings.WINDOW_WIDTH / 8, Settings.WINDOW_HEIGHT / 8);
		pingPong3.bind();
		
		glClearColor(0, 0, 0, 1.0f);
		clear();
		
		s.uniformVec2("offset", new Vec2(bloomRange / (Settings.WINDOW_WIDTH / 8), 0));
		pingPong2.tex[0].bind();
		framebufferMesh.draw();
		
		pingPong3.tex[0].bind();
		s.uniformVec2("offset", new Vec2(0f, bloomRange / (Settings.WINDOW_WIDTH / 8)));
		framebufferMesh.draw();
		
		glViewport(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
	}
	
	public static void renderGui() {
		glDepthFunc(GL_ALWAYS);
		Shader s = Engine.getShader("sprite");
		s.bind();
		
		s.uniformMat4("model", new Mat4().translate(new Vec3(1800/2, 1000/2, 0)).scale(new Vec3(1800, 1000, 0)));
		s.uniformMat4("view", new Mat4().scale(new Vec3(1f/(Settings.WINDOW_WIDTH/2f), 1f/(Settings.WINDOW_HEIGHT/2f), 0f)).translate(new Vec3(-Settings.WINDOW_WIDTH/2f, -Settings.WINDOW_HEIGHT/2f, 0f)));
		s.uniformMat4("projection", new Mat4());
		s.uniformVec3("overlayColor", new Vec3(1, 1, 1));
		
		pingPong3.tex[0].bind();
		//spriteMesh.draw();
		
		glDepthFunc(GL_LESS);
	}
	
	public static void renderWorld(Framebuffer target, Camera cam) {
		//bind the main rendering buffer and now we're ready to render normally
		target.bind();
		glViewport(0, 0, target.dimensions.x, target.dimensions.y);
		
		//clear the renderbuffer
		glClearColor(scene.skyColor.x, scene.skyColor.y, scene.skyColor.z, 1.0f);
		clear();
		
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
		
		//and finally, tell the game to render
		game.render();
		

		if (wireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		}
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
				
		//create and set all the event callbacks
		keyCallback = new KeyCallback();
		cursorCallback = new CursorCallback();
		mouseCallback = new MouseCallback();
		scrollCallback = new ScrollCallback();
		
		glfwSetKeyCallback(window, keyCallback);
		glfwSetCursorPosCallback(window, cursorCallback);
		glfwSetMouseButtonCallback(window, mouseCallback);
		glfwSetScrollCallback(window, scrollCallback);
		
		
		//and finally create the opengl context and we're ready to go
		GL.createCapabilities();
		
		//setup openAL
		long device = ALC10.alcOpenDevice((ByteBuffer)null);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);

		long context = ALC10.alcCreateContext(device, (IntBuffer)null);
		ALC10.alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
		
		if (isVR) setupVR();
	}
	
	private static void setupVR() {
		// Loading the SteamVR Runtime
		System.out.println(errorBuffer.isDirect());
		
        hmd = VR.VR_Init(errorBuffer, VR.EVRApplicationType.VRApplication_Scene);

        if (errorBuffer.get(0) != VR.EVRInitError.VRInitError_None) {
            hmd = null;
            String s = "Unable to init VR runtime: " + VR.VR_GetVRInitErrorAsEnglishDescription(errorBuffer.get(0));
            throw new Error("VR_Init Failed, " + s);
        }
        
//        IntBuffer width = BufferUtils.createIntBuffer(1), height = BufferUtils.createIntBuffer(1);
//        
//        hmd.GetRecommendedRenderTargetSize.apply(width, height);
//        
//        System.out.println("width: " + width.get(0) + ", height: " + height.get(0));
        
        
        compositor = new IVRCompositor_FnTable(VR.VR_GetGenericInterface(VR.IVRCompositor_Version, errorBuffer));

        if (compositor == null || errorBuffer.get(0) != VR.EVRInitError.VRInitError_None) {
            System.err.println("Compositor initialization failed. See log file for details");
        }
	}

	/**
	 * Clears the currently bound framebuffer.
	 */
	public static void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	private static void end() {
		game.kill();
		
		if (hmd != null) {
            VR.VR_Shutdown();
            hmd = null;
        }
		fbuffer.delete();
		
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
	 * This will load audio from the specified path on disk into memory.
	 * This does NOT play or even return the audio. Use getAudio for that.
	 */
	public static void loadAudio(String path) {
		Audio a = new Audio("audio/" + path);
		sounds.put(path, a);
	}
	
	/**
	 * This will return an audio object from memory ONLY if it has been loaded with loadSAudio.
	 * The object will only be returned, you will have to play it yourself.
	 */
	public static Audio getAudio(String path) {
		return sounds.get(path);
	}

	/**
	 * This will load a texture from the specified path on disk into memory.
	 * This does NOT bind the texture or even return the texture. Use getTexture for that.
	 */
	public static void loadTexture(String path, boolean pixelated, boolean srgb) {
		Texture s = new Texture("textures/" + path, pixelated, srgb);
		textures.put(path, s);
	}
	
	public static void loadTexture(String path) {
		loadTexture(path, false, false);
	}
	
	/**
	 * This will return a texture object from memory ONLY if it has been loaded with loadTexture.
	 * The object will only be returned, you will have to bind it yourself.
	 */
	public static Texture getTexture(String path) {
		return textures.get(path);
	}
}
