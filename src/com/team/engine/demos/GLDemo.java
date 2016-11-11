package com.team.engine.demos;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.nuklear.*;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.team.engine.*;
import com.team.engine.gameobject.MeshObject;
import com.team.engine.rendering.Cubemap;
import com.team.engine.rendering.Material;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.ObjLoader;
import com.team.engine.rendering.PointLight;
import com.team.engine.rendering.Primitives;
import com.team.engine.rendering.Shader;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL, bullet physics, skyboxes, and lighting shaders.
 */
public class GLDemo extends AbstractGame {
	public static Mesh cubeMesh;
	public static Mesh groundMesh;
	
	public static Mesh mat1;
	public static Mesh mat2;
	
	private Mesh sphere;
	
	private Model model;

	public static Material crateMaterial = new Material("container2.png", 0.8f, null, "container2_specular.png");
	public static Material groundMaterial = new Material("brickwall.jpg", 0.6f, "brickwall_normal.jpg", 0.3f);
	
	public static Material outsideMaterial = new Material("metal/albedo.png", "metal/roughness.png", "metal/normal.png", "metal/metallic.png");
	public static Material insideMaterial = new Material("plastic/albedo.png", "plastic/roughness.png", "plastic/normal.png", "plastic/metallic.png");
	
	
	//nuklear variables
	private static final NkAllocator ALLOCATOR;
	private static final NkDrawVertexLayoutElement.Buffer VERTEX_LAYOUT;
	private static final int BUFFER_INITIAL_SIZE = 4 * 1024;
	
	private static final int MAX_VERTEX_BUFFER  = 512 * 1024;
	private static final int MAX_ELEMENT_BUFFER = 128 * 1024;
	
	private int vbo, vao, ebo;
	private int prog;
	private int vert_shdr;
	private int frag_shdr;
	private int uniform_tex;
	private int uniform_proj;
	private NkDrawNullTexture null_texture = NkDrawNullTexture.create();
	
	private NkBuffer cmds = NkBuffer.create();
	private NkContext ctx = NkContext.create();
	private NkUserFont default_font = NkUserFont.create();
	
	private final Demo       demo = new Demo();
	private final Calculator calc = new Calculator();
	
	private ByteBuffer ttf;
	
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
	
	public static void main(String[] args) {
		Engine.start(false, false, new GLDemo());
	}

	@Override
	public void init() {
		print("yo");
		
		Engine.loadTexture("container2.png", false, true);
		Engine.loadTexture("container2_specular.png");
		Engine.loadTexture("brickwall.jpg", false, true);
		Engine.loadTexture("brickwall_normal.jpg");
		
		Engine.loadTexture("metal/albedo.png", false, true);
		Engine.loadTexture("metal/normal.png");
		Engine.loadTexture("metal/metallic.png");
		Engine.loadTexture("metal/roughness.png");
		
		Engine.loadTexture("plastic/albedo.png", false, true);
		Engine.loadTexture("plastic/normal.png");
		Engine.loadTexture("plastic/metallic.png");
		Engine.loadTexture("plastic/roughness.png");
		
		Engine.loadTexture("gravel/albedo.png", false, true);
		Engine.loadTexture("gravel/normal.png");
		Engine.loadTexture("gravel/metallic.png");
		Engine.loadTexture("gravel/roughness.png");
		
		Engine.loadTexture("stone_tile.png", false, true);
		Engine.loadTexture("stone_tile_normal.png");
		Engine.loadTexture("stone_tile_specular.png");
		
		cubeMesh = Mesh.raw(Primitives.cube(1.0f), false);
		groundMesh = Mesh.raw(Primitives.cube(16.0f), true);
		sphere = ObjLoader.loadFile("sphere.obj");
		mat1 = ObjLoader.loadFile("matmodel-1.obj");
		mat2 = ObjLoader.loadFile("matmodel-2.obj");
		
		model = new Model("adam.fbx", mat4().translate(vec3(0, -10, 0)).rotateX(-90).scale(0.053f), true);
		
		
		Engine.scene.skybox = new Cubemap("sunset");
		Engine.scene.irradiance = new Cubemap("sunset-irradiance");
		
		Engine.camera.setPosition(vec3(0, 0, 5));
		
		Engine.scene.sun.color = vec3(5, 5, 5);
		Engine.scene.sun.direction = vec3(-1, -0.8, -0.7f);
		
		Engine.scene.add(new MeshObject(vec3(-10, -10, -12), new Quat4f(), new BoxShape(new Vector3f(2f, 5f, 2f)), 0f, mat1,0.5f,  insideMaterial));
		Engine.scene.add(new MeshObject(vec3(-10, -10, -12), new Quat4f(), new SphereShape(0.5f), 0f, mat2,0.5f,  outsideMaterial));
		
		Engine.scene.add(new MeshObject(vec3(0, -60f, 0), new Quat4f(), new BoxShape(new Vector3f(50f, 50f, 50f)), 0f, groundMesh, 100f, groundMaterial));
		
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 7; y++) {
				Material mat = new Material("stone_tile.png", y / 7f, "stone_tile_normal.png", x / 7f);
				Engine.scene.add(new MeshObject(vec3(x * 3, y * 3, 0).add(vec3(0, -9, -15)), new Quat4f(), null, 0f, sphere, 1f, mat));
			}
		}
		
		ttf = Util.readFile("/home/joseph/git/Game-Engine/resources/HelvetiPixel.ttf");
		
		nk_init(ctx, ALLOCATOR, null);
		setupContext();
		setupFont();
		
	}
	
	private void render(int AA, int max_vertex_buffer, int max_element_buffer) {
		try ( MemoryStack stack = stackPush() ) {
			// setup global state
			glEnable(GL_BLEND);
			glBlendEquation(GL_FUNC_ADD);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glDisable(GL_CULL_FACE);
			glDisable(GL_DEPTH_TEST);
			glEnable(GL_SCISSOR_TEST);
			glActiveTexture(GL_TEXTURE0);

			// setup program
			glUseProgram(prog);
			glUniform1i(uniform_tex, 0);
			glUniformMatrix4fv(uniform_proj, false, stack.floats(
				2.0f / Settings.WINDOW_WIDTH, 0.0f, 0.0f, 0.0f,
				0.0f, -2.0f / Settings.WINDOW_HEIGHT, 0.0f, 0.0f,
				0.0f, 0.0f, -1.0f, 0.0f,
				-1.0f, 1.0f, 0.0f, 1.0f
			));
			glViewport(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
		}

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
	
	private void setupFont() {
		int BITMAP_W = 1024;
		int BITMAP_H = 1024;

		int FONT_HEIGHT = 18;
		int fontTexID = glGenTextures();

		STBTTFontinfo fontInfo = STBTTFontinfo.create();
		STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(95);

		float scale;
		float descent;

		try ( MemoryStack stack = stackPush() ) {
			stbtt_InitFont(fontInfo, ttf);
			scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);

			IntBuffer d = stack.mallocInt(1);
			stbtt_GetFontVMetrics(fontInfo, null, d, null);
			descent = d.get(0) * scale;

			ByteBuffer bitmap = memAlloc(BITMAP_W * BITMAP_H);

			STBTTPackContext pc = STBTTPackContext.mallocStack(stack);
			stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, null);
			stbtt_PackSetOversampling(pc, 4, 4);
			stbtt_PackFontRange(pc, ttf, 0, FONT_HEIGHT, 32, cdata);
			stbtt_PackEnd(pc);

			// Convert R8 to RGBA8
			ByteBuffer texture = memAlloc(BITMAP_W * BITMAP_H * 4);
			for ( int i = 0; i < bitmap.capacity(); i++ )
				texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
			texture.flip();

			glBindTexture(GL_TEXTURE_2D, fontTexID);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, BITMAP_W, BITMAP_H, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, texture);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

			memFree(texture);
			memFree(bitmap);
		}

		default_font
			.width((handle, h, text, len) -> {
				float text_width = 0;
				try ( MemoryStack stack = stackPush() ) {
					IntBuffer unicode = stack.mallocInt(1);

					int glyph_len = nnk_utf_decode(text, memAddress(unicode), len);
					int text_len = glyph_len;

					if ( glyph_len == 0 )
						return 0;

					IntBuffer advance = stack.mallocInt(1);
					while ( text_len <= len && glyph_len != 0 ) {
						if ( unicode.get(0) == NK_UTF_INVALID )
							break;

                        /* query currently drawn glyph information */
						stbtt_GetCodepointHMetrics(fontInfo, unicode.get(0), advance, null);
						text_width += advance.get(0) * scale;

						/* offset next glyph */
						glyph_len = nnk_utf_decode(text + text_len, memAddress(unicode), len - text_len);
						text_len += glyph_len;
					}
				}
				return text_width;
			})
			.height(FONT_HEIGHT)
			.query((handle, font_height, glyph, codepoint, next_codepoint) -> {
				try ( MemoryStack stack = stackPush() ) {
					FloatBuffer x = stack.floats(0.0f);
					FloatBuffer y = stack.floats(0.0f);

					STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
					IntBuffer advance = stack.mallocInt(1);

					stbtt_GetPackedQuad(cdata, BITMAP_W, BITMAP_H, codepoint - 32, x, y, q, false);
					stbtt_GetCodepointHMetrics(fontInfo, codepoint, advance, null);

					NkUserFontGlyph ufg = NkUserFontGlyph.create(glyph);

					ufg.width(q.x1() - q.x0());
					ufg.height(q.y1() - q.y0());
					ufg.offset().set(q.x0(), q.y0() + (FONT_HEIGHT + descent));
					ufg.xadvance(advance.get(0) * scale);
					ufg.uv(0).set(q.s0(), q.t0());
					ufg.uv(1).set(q.s1(), q.t1());
				}
			})
			.texture().id(fontTexID);

		nk_style_set_font(ctx, default_font);
	}
	
	private void setupContext() {
		String NK_SHADER_VERSION = Platform.get() == Platform.MACOSX ? "#version 150\n" : "#version 300 es\n";
		String vertex_shader =
			NK_SHADER_VERSION +
				"uniform mat4 ProjMtx;\n" +
				"in vec2 Position;\n" +
				"in vec2 TexCoord;\n" +
				"in vec4 Color;\n" +
				"out vec2 Frag_UV;\n" +
				"out vec4 Frag_Color;\n" +
				"void main() {\n" +
				"   Frag_UV = TexCoord;\n" +
				"   Frag_Color = Color;\n" +
				"   gl_Position = ProjMtx * vec4(Position.xy, 0, 1);\n" +
				"}\n";
		String fragment_shader =
			NK_SHADER_VERSION +
				"precision mediump float;\n" +
				"uniform sampler2D Texture;\n" +
				"in vec2 Frag_UV;\n" +
				"in vec4 Frag_Color;\n" +
				"out vec4 Out_Color;\n" +
				"void main(){\n" +
				"   Out_Color = Frag_Color * texture(Texture, Frag_UV.st);\n" +
				"}\n";

		nk_buffer_init(cmds, ALLOCATOR, BUFFER_INITIAL_SIZE);
		prog = glCreateProgram();
		vert_shdr = glCreateShader(GL_VERTEX_SHADER);
		frag_shdr = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(vert_shdr, vertex_shader);
		glShaderSource(frag_shdr, fragment_shader);
		glCompileShader(vert_shdr);
		glCompileShader(frag_shdr);
		if ( glGetShaderi(vert_shdr, GL_COMPILE_STATUS) != GL_TRUE )
			throw new IllegalStateException();
		if ( glGetShaderi(frag_shdr, GL_COMPILE_STATUS) != GL_TRUE )
			throw new IllegalStateException();
		glAttachShader(prog, vert_shdr);
		glAttachShader(prog, frag_shdr);
		glLinkProgram(prog);
		if ( glGetProgrami(prog, GL_LINK_STATUS) != GL_TRUE )
			throw new IllegalStateException();

		uniform_tex = glGetUniformLocation(prog, "Texture");
		uniform_proj = glGetUniformLocation(prog, "ProjMtx");
		int attrib_pos = glGetAttribLocation(prog, "Position");
		int attrib_uv = glGetAttribLocation(prog, "TexCoord");
		int attrib_col = glGetAttribLocation(prog, "Color");

		{
			// buffer setup
			vbo = glGenBuffers();
			ebo = glGenBuffers();
			vao = glGenVertexArrays();

			glBindVertexArray(vao);
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

			glEnableVertexAttribArray(attrib_pos);
			glEnableVertexAttribArray(attrib_uv);
			glEnableVertexAttribArray(attrib_col);

			glVertexAttribPointer(attrib_pos, 2, GL_FLOAT, false, 20, 0);
			glVertexAttribPointer(attrib_uv, 2, GL_FLOAT, false, 20, 8);
			glVertexAttribPointer(attrib_col, 4, GL_UNSIGNED_BYTE, true, 20, 16);
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
		
		glfwSetScrollCallback(Engine.window, (window, xoffset, yoffset) -> nk_input_scroll(ctx, (float)yoffset));
		glfwSetCharCallback(Engine.window, (window, codepoint) -> nk_input_unicode(ctx, codepoint));
		
		glfwSetCursorPosCallback(Engine.window, (window, xpos, ypos) -> nk_input_motion(ctx, (int)xpos, (int)ypos));
		glfwSetMouseButtonCallback(Engine.window, (window, button, action, mods) -> {
			try ( MemoryStack stack = stackPush() ) {
				DoubleBuffer cx = stack.mallocDouble(1);
				DoubleBuffer cy = stack.mallocDouble(1);

				glfwGetCursorPos(window, cx, cy);

				int x = (int)cx.get(0);
				int y = (int)cy.get(0);

				int nkButton;
				switch ( button ) {
					case GLFW_MOUSE_BUTTON_RIGHT:
						nkButton = NK_BUTTON_RIGHT;
						break;
					case GLFW_MOUSE_BUTTON_MIDDLE:
						nkButton = NK_BUTTON_MIDDLE;
						break;
					default:
						nkButton = NK_BUTTON_LEFT;
				}
				nk_input_button(ctx, nkButton, x, y, action == GLFW_PRESS);
			}
		});

		glBindTexture(GL_TEXTURE_2D, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	private static float accum;

	@Override
	public void tick() {
		accum += Engine.deltaTime;
		
		if (Input.isButtonDown(1) && accum > 0.1f) {
			FPSCamera cam = (FPSCamera)Engine.camera;
			MeshObject c = new MeshObject(cam.getPosition(), new Quat4f(1.0f, 0.3f, 0.5f, 0f), new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)), 1f, cubeMesh, 1f, crateMaterial);
			Engine.scene.add(c);
			c.rb.applyCentralForce(new Vector3f(0.0f, 100.0f, 0.0f));
			accum = 0;
		}
		if (Input.isButtonDown(2) && accum > 1f) {
			PointLight p = new PointLight(Engine.camera.getPosition(), vec3(1.0f, 1.0f, 2.0f), 5f, 10f);
			Engine.scene.add(p);

			accum = 0;
		}	
	}

	@Override
	public void render() {	
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
		
		model.render();
		
		demo.layout(ctx, 50, 50);
		calc.layout(ctx, 300, 50);
		
		render(NK_ANTI_ALIASING_ON, MAX_VERTEX_BUFFER, MAX_ELEMENT_BUFFER);
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		
	}

	@Override
	public void kill() {
		
	}

	@Override
	public void renderShadow(Shader s) {
		model.renderShadow(s);
	}
}
