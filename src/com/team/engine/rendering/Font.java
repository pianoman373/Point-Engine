package com.team.engine.rendering;

import static com.team.engine.Globals.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.stb.STBTruetype.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;

import com.team.engine.Engine;
import com.team.engine.Settings;
import com.team.engine.Util;
import com.team.engine.vecmath.Mat4;

/**
 * Representation of a .ttf file that contains helper functions for drawing to the screen.
 */
public class Font {
	private int texID;
	private int size;
	private STBTTBakedChar.Buffer cdata;
	private ByteBuffer ttf;
	
	int BITMAP_W = 512;
	int BITMAP_H = 512;
	/**
	 * @param font The .ttf file to load
	 * @param size The size in screen pixels that this font will be rendered in.
	 */
	public Font(String font, int size) {
		this.texID = glGenTextures();
		this.cdata = STBTTBakedChar.malloc(96);
		this.size = size;
		this.ttf = Util.readFile(Settings.RESOURCE_PATH + font);

		ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
		stbtt_BakeFontBitmap(ttf, size, bitmap, BITMAP_W, BITMAP_H, 32, cdata);

		glBindTexture(GL_TEXTURE_2D, texID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	}
	
	/**
	 * Draws The specified text on screen. Not suitable for drawing text into a 3D world yet.
	 * 
	 * Note: 0, 0 is the bottom left of the window.
	 */
	public void draw(float xpos, float ypos, String text) {
		Tessellator ts = Engine.tessellator;
		glDisable(GL_CULL_FACE);
		
		Shader s = getShader("sprite");
		glBindTexture(GL_TEXTURE_2D, texID);
		s.bind();
		s.uniformMat4("model", mat4().translate(vec3(-(Settings.WINDOW_WIDTH / 2) + xpos, -(Settings.WINDOW_HEIGHT / 2) +ypos, 0)));
		s.uniformMat4("view", mat4());
		float w = Settings.WINDOW_WIDTH / 2;
		float h = Settings.WINDOW_HEIGHT / 2;
		s.uniformMat4("projection", Mat4.orthographic(-w, w, -h, h, -100, 100));
		s.uniformVec3("color", vec3(1f, 1f, 1f));
		
		
		try ( MemoryStack stack = stackPush() ) {
			FloatBuffer x = stack.floats(0.0f);
			FloatBuffer y = stack.floats(0.0f);

			STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

			ts.begin(GL_QUADS);
			for ( int i = 0; i < text.length(); i++ ) {
				char c = text.charAt(i);
				if ( c == '\n' ) {
					y.put(0, y.get(0) + size);
					x.put(0, 0.0f);
					continue;
				} else if ( c < 32 || 128 <= c )
					continue;
				stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, c - 32, x, y, q, true);

				ts.vertex(q.x0(), 1-q.y0(), 0, q.s0(), q.t0());
				
				ts.vertex(q.x1(), 1-q.y0(), 0, q.s1(), q.t0());

				ts.vertex(q.x1(), 1-q.y1(), 0, q.s1(), q.t1());

				ts.vertex(q.x0(), 1-q.y1(), 0, q.s0(), q.t1());
			}
			ts.end();
		}
	}
}

