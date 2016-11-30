package com.team.engine;

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
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;

import org.lwjgl.nuklear.NkAllocator;
import org.lwjgl.nuklear.NkBuffer;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkConvertConfig;
import org.lwjgl.nuklear.NkDrawCommand;
import org.lwjgl.nuklear.NkDrawNullTexture;
import org.lwjgl.nuklear.NkDrawVertexLayoutElement;
import org.lwjgl.nuklear.NkMouse;
import org.lwjgl.system.MemoryStack;

import com.team.engine.gui.Font;
import com.team.engine.rendering.Shader;

public class NuklearManager {
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
	
	public static void init() {
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
	
	public static void render() {
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

			glBufferData(GL_ARRAY_BUFFER, MAX_VERTEX_BUFFER, GL_STREAM_DRAW);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, MAX_ELEMENT_BUFFER, GL_STREAM_DRAW);

			// load draw vertices & elements directly into vertex + element buffer
			ByteBuffer vertices = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_VERTEX_BUFFER, null);
			ByteBuffer elements = glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_ELEMENT_BUFFER, null);
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
					.shape_AA(NK_ANTI_ALIASING_ON)
					.line_AA(NK_ANTI_ALIASING_ON);

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
		glEnable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_SCISSOR_TEST);
		glEnable(GL_CULL_FACE);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void preInput() {
		nk_input_begin(ctx);
	}
	
	public static void postInput() {

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
	}
}
