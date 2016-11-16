package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.swing.JOptionPane;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nuklear.NkAllocator;
import org.lwjgl.nuklear.NkBuffer;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkConvertConfig;
import org.lwjgl.nuklear.NkDrawCommand;
import org.lwjgl.nuklear.NkDrawNullTexture;
import org.lwjgl.nuklear.NkDrawVertexLayoutElement;
import org.lwjgl.nuklear.NkMouse;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.opengl.ARBSeamlessCubeMap;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import com.team.engine.rendering.Framebuffer;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.Primitives;
import com.team.engine.rendering.Shader;
import com.team.engine.rendering.Texture;
import com.team.engine.vecmath.Vec2i;

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
	public static long window;
	public static Framebuffer fbuffer;
	private static Framebuffer pingPong1;
	private static Framebuffer pingPong2;
	private static Framebuffer pingPong3;
	private static Mesh skyboxMesh;
	private static float lastFrame = 0.0f;
	protected static HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	protected static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	protected static HashMap<String, Audio> sounds = new HashMap<String, Audio>();
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
	
	//nuklear variables
	private static final NkAllocator ALLOCATOR;
	private static final NkDrawVertexLayoutElement.Buffer VERTEX_LAYOUT;
	private static final int BUFFER_INITIAL_SIZE = 4 * 1024;
	
	private static Font font;
	
	private static final int MAX_VERTEX_BUFFER  = 512 * 1024;
	private static final int MAX_ELEMENT_BUFFER = 128 * 1024;
	
	private static int vbo, vao, ebo;
	private static NkDrawNullTexture null_texture = NkDrawNullTexture.create();
	public static NkContext ctx = NkContext.create();
	
	private static NkBuffer cmds = NkBuffer.create();
	
	static {
		ALLOCATOR = NkAllocator.create();
		ALLOCATOR.alloc((handle, old, size) -> {
			long mem = nmemAlloc(size);
			if ( mem == NULL )
				throw new OutOfMemoryError();

			return mem;

		});
		ALLOCATOR.mfree((handle, ptr) -> nmemFree(ptr));

		VERTEX_LAYOUT = NkDrawVertexLayoutElement.create(4)
			.position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0)
			.position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8)
			.position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16)
			.position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0)
			.flip();
	}
	
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
		loadShader("pbr");
		loadShader("pbr-specular");
		
		loadTexture("ascii.png", true, false);

		//vital meshes
		framebufferMesh = Mesh.raw(Primitives.framebuffer(), false);
		skyboxMesh = Mesh.raw(Primitives.skybox(), false);
		cubeMesh = Mesh.raw(Primitives.cube(1.0f), true);
		debugCubeMesh = Primitives.debugCube();
		debugSphereMesh = Primitives.debugSphere(64);
		spriteMesh = Mesh.raw(Primitives.sprite(vec2(0, 0), vec2(1, 1)), true);

		//all the framebuffers, one for shadows, one for normal rendering, and 3 ping pong shaders for bloom
		fbuffer = Framebuffer.HdrWithBloom(new Vec2i(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT));
		if (Settings.ENABLE_BLOOM) {
			pingPong1 = Framebuffer.standard(new Vec2i(Settings.WINDOW_WIDTH / 2, Settings.WINDOW_HEIGHT / 2), false);
			pingPong2 = Framebuffer.standard(new Vec2i(Settings.WINDOW_WIDTH / 4, Settings.WINDOW_HEIGHT / 4), false);
			pingPong3 = Framebuffer.standard(new Vec2i(Settings.WINDOW_WIDTH / 8, Settings.WINDOW_HEIGHT / 8), false);
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
			camera = new SpaceCamera();
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
			nk_input_begin(ctx);
			glfwPollEvents();

			NkMouse mouse = ctx.input().mouse();
			if ( mouse.grab() )
				glfwSetInputMode(Engine.window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
			else if ( mouse.grabbed() ) {
				float prevX = mouse.prev().x();
				float prevY = mouse.prev().y();
				glfwSetCursorPos(Engine.window, prevX, prevY);
				mouse.pos().x(prevX);
				mouse.pos().y(prevY);
			} else if ( mouse.ungrab() )
				glfwSetInputMode(Engine.window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

			nk_input_end(ctx);
			
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
		        	print("HMD IS NULL!?!?!?!?!");
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
			renderNuklear(NK_ANTI_ALIASING_ON, MAX_VERTEX_BUFFER, MAX_ELEMENT_BUFFER);
			
			
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
			
			if (isVR) {
				compositor.Submit.apply(0, new Texture_t(Engine.fbuffer.tex[0].id, VR.EGraphicsAPIConvention.API_OpenGL, VR.EColorSpace.ColorSpace_Gamma), null, VR.EVRSubmitFlags.Submit_Default);
			
				compositor.Submit.apply(1, new Texture_t(Engine.fbuffer.tex[1].id, VR.EGraphicsAPIConvention.API_OpenGL, VR.EColorSpace.ColorSpace_Gamma), null, VR.EVRSubmitFlags.Submit_Default);
				
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
		
		float bloomRange = 1f;
		
		//level 1
		glViewport(0, 0, Settings.WINDOW_WIDTH / 2, Settings.WINDOW_HEIGHT / 2);
		pingPong1.bind();
		
		glClearColor(0, 0, 0, 1.0f);
		clear();
		
		s.uniformVec2("offset", vec2(bloomRange / (Settings.WINDOW_WIDTH / 2), 0));
		fbuffer.tex[1].bind();
		framebufferMesh.draw();
		
		pingPong1.tex[0].bind();
		s.uniformVec2("offset", vec2(0f, bloomRange / (Settings.WINDOW_WIDTH / 2)));
		framebufferMesh.draw();
		
		//level 2
		glViewport(0, 0, Settings.WINDOW_WIDTH / 4, Settings.WINDOW_HEIGHT / 4);
		pingPong2.bind();
		
		glClearColor(0, 0, 0, 1.0f);
		clear();
		
		s.uniformVec2("offset", vec2(bloomRange / (Settings.WINDOW_WIDTH / 4), 0));
		pingPong1.tex[0].bind();
		framebufferMesh.draw();
		
		pingPong2.tex[0].bind();
		s.uniformVec2("offset", vec2(0f, bloomRange / (Settings.WINDOW_WIDTH / 4)));
		framebufferMesh.draw();
		
		glViewport(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
		
		//level 2
		glViewport(0, 0, Settings.WINDOW_WIDTH / 8, Settings.WINDOW_HEIGHT / 8);
		pingPong3.bind();
		
		glClearColor(0, 0, 0, 1.0f);
		clear();
		
		s.uniformVec2("offset", vec2(bloomRange / (Settings.WINDOW_WIDTH / 8), 0));
		pingPong2.tex[0].bind();
		framebufferMesh.draw();
		
		pingPong3.tex[0].bind();
		s.uniformVec2("offset", vec2(0f, bloomRange / (Settings.WINDOW_WIDTH / 8)));
		framebufferMesh.draw();
		
		glViewport(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
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
		//System.setProperty("org.lwjgl.librarypath", Paths.get("native").toAbsolutePath().toString());
		print(System.getProperty("java.library.path"));
		//System.setProperty( "java.library.path", "natives/" );

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
		
		glfwSetKeyCallback(window, (long window, int key, int scancode, int action, int mode) -> Input.keyEvent(window, key, action));
		glfwSetCursorPosCallback(window, (long window, double xpos, double ypos) -> Input.cursorEvent(window, xpos, ypos));
		glfwSetMouseButtonCallback(window, (long window, int button, int action, int mods) -> Input.mouseEvent(window, button, action, mods));
		glfwSetScrollCallback(window, (long window, double arg1, double scrollAmount) -> Input.scrollEvent(window, scrollAmount));
		glfwSetCharCallback(Engine.window, (window, codepoint) -> nk_input_unicode(ctx, codepoint));
		
		//and finally create the opengl context and we're ready to go
		GL.createCapabilities();
		
		//setup openAL
		long device = ALC10.alcOpenDevice((ByteBuffer)null);
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);

		long context = ALC10.alcCreateContext(device, (IntBuffer)null);
		ALC10.alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
		
		if (isVR) setupVR();
		setupNuklear();
	}
	
	private static void setupNuklear() {
		nk_init(ctx, ALLOCATOR, null);
		
		nk_buffer_init(cmds, ALLOCATOR, BUFFER_INITIAL_SIZE);
		{
			// buffer setup
			vbo = glGenBuffers();
			ebo = glGenBuffers();
			vao = glGenVertexArrays();

			glBindVertexArray(vao);
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);

			glVertexAttribPointer(0, 2, GL_FLOAT, false, 20, 0);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 8);
			glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, 20, 16);
		}

		{
			// null texture setup
			int nullTexID = glGenTextures();

			null_texture.texture().id(nullTexID);
			null_texture.uv().set(0.5f, 0.5f);

			glBindTexture(GL_TEXTURE_2D, nullTexID);
			try ( MemoryStack stack = stackPush() ) {
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF));
			}
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		}

		glBindTexture(GL_TEXTURE_2D, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		
		font = new Font("HelvetiPixel.ttf");
		font.enable();
	}
	
	private static void renderNuklear(int AA, int max_vertex_buffer, int max_element_buffer) {
		// setup global state
		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_SCISSOR_TEST);
		glActiveTexture(GL_TEXTURE0);

		// setup program
		Shader s = getShader("gui");
		s.bindSimple();
		s.uniformInt("Texture", 0);
		s.uniformMat4("ProjMtx", mat4(
				vec4(2.0f / Settings.WINDOW_WIDTH, 0.0f, 0.0f, 0.0f),
				vec4(0.0f, -2.0f / Settings.WINDOW_HEIGHT, 0.0f, 0.0f),
				vec4(0.0f, 0.0f, -1.0f, 0.0f),
				vec4(-1.0f, 1.0f, 0.0f, 1.0f)
		));
		glViewport(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);

		{
			// convert from command queue into draw list and draw to screen

			// allocate vertex and element buffer
			glBindVertexArray(vao);
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

			glBufferData(GL_ARRAY_BUFFER, max_vertex_buffer, GL_STREAM_DRAW);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, max_element_buffer, GL_STREAM_DRAW);

			// load draw vertices & elements directly into vertex + element buffer
			ByteBuffer vertices = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, max_vertex_buffer, null);
			ByteBuffer elements = glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, max_element_buffer, null);
			try ( MemoryStack stack = stackPush() ) {
				// fill convert configuration
				NkConvertConfig config = NkConvertConfig.callocStack(stack)
					.vertex_layout(VERTEX_LAYOUT)
					.vertex_size(20)
					.vertex_alignment(4)
					.null_texture(null_texture)
					.circle_segment_count(22)
					.curve_segment_count(22)
					.arc_segment_count(22)
					.global_alpha(1.0f)
					.shape_AA(AA)
					.line_AA(AA);

				// setup buffers to load vertices and elements
				NkBuffer vbuf = NkBuffer.mallocStack(stack);
				NkBuffer ebuf = NkBuffer.mallocStack(stack);

				nk_buffer_init_fixed(vbuf, vertices/*, max_vertex_buffer*/);
				nk_buffer_init_fixed(ebuf, elements/*, max_element_buffer*/);
				nk_convert(ctx, cmds, vbuf, ebuf, config);
			}
			glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
			glUnmapBuffer(GL_ARRAY_BUFFER);

			// iterate over and execute each draw command
			float fb_scale_x = 1;
			float fb_scale_y = 1;

			long offset = NULL;
			for ( NkDrawCommand cmd = nk__draw_begin(ctx, cmds); cmd != null; cmd = nk__draw_next(cmd, cmds, ctx) ) {
				if ( cmd.elem_count() == 0 ) continue;
				glBindTexture(GL_TEXTURE_2D, cmd.texture().id());
				glScissor(
					(int)(cmd.clip_rect().x() * fb_scale_x),
					(int)((Settings.WINDOW_HEIGHT - (int)(cmd.clip_rect().y() + cmd.clip_rect().h())) * fb_scale_y),
					(int)(cmd.clip_rect().w() * fb_scale_x),
					(int)(cmd.clip_rect().h() * fb_scale_y)
				);
				glDrawElements(GL_TRIANGLES, cmd.elem_count(), GL_UNSIGNED_SHORT, offset);
				offset += cmd.elem_count() * 2;
			}
			nk_clear(ctx);
		}

		// default OpenGL state
		glUseProgram(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_SCISSOR_TEST);
	}
	
	private static void setupVR() {
		// Loading the SteamVR Runtime
		print(errorBuffer.isDirect());
		
        hmd = VR.VR_Init(errorBuffer, VR.EVRApplicationType.VRApplication_Scene);

        if (errorBuffer.get(0) != VR.EVRInitError.VRInitError_None) {
            hmd = null;
            String s = "Unable to init VR runtime: " + VR.VR_GetVRInitErrorAsEnglishDescription(errorBuffer.get(0));
            throw new Error("VR_Init Failed, " + s);
        }
        
        IntBuffer width = BufferUtils.createIntBuffer(1), height = BufferUtils.createIntBuffer(1);
        
        hmd.GetRecommendedRenderTargetSize.apply(width, height);
        
        print("width: " + width.get(0) + ", height: " + height.get(0));
        
        
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
}
