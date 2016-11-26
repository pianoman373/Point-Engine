package com.team.engine.rendering;

import static com.team.engine.Globals.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import com.team.engine.Settings;
import com.team.engine.vecmath.Vec2i;

public class Texture {
	public int id;
	public Vec2i dimensions;
	
	public Texture(String path) {
		this(path, false, false);
	}
	
	public Texture(int id, Vec2i dimensions) {
		this.id = id;
		this.dimensions = dimensions;
	}
	
	/**
	 * Creates a new texture object that can be bound later, from the specified path.
	 * 
	 * It is advised to use the texture management system in Engine rather than handle texture objects yourself.
	 */
	public Texture(String path, boolean pixelated, boolean srgb) {
		id = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, id);
		
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		IntBuffer comp = BufferUtils.createIntBuffer(1);
		
		STBImage.stbi_set_flip_vertically_on_load(true);
		boolean hdr = STBImage.stbi_is_hdr(Settings.RESOURCE_PATH + path);
		print("loading texture: " + path);
		if (hdr) {
			FloatBuffer buffer = STBImage.stbi_loadf(Settings.RESOURCE_PATH + path, w, h, comp, 0);
			
			int width = w.get(0);
			int height = h.get(0);
			print(buffer.remaining());
			
			dimensions = new Vec2i(width, height);
			
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, width, height, 0, GL_RGB, GL_FLOAT, buffer);
			
			//STBImage.stbi_image_free(buffer);
		}
		else {
			ByteBuffer buffer = STBImage.stbi_load(Settings.RESOURCE_PATH + path, w, h, comp, 4);
			int width = w.get(0);
			int height = h.get(0);
		
			dimensions = new Vec2i(width, height);
	
			glTexImage2D(GL_TEXTURE_2D, 0, srgb ? GL_SRGB_ALPHA : GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			
			//STBImage.stbi_image_free(buffer);
		}
		
		glGenerateMipmap(GL_TEXTURE_2D);
		
		if (pixelated) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		}
		else {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 8); 
		}
	
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	/**
	 * Binds the texture for rendering in the first texture location.
	 */
	public void bind() {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	/**
	 * Binds the texture for rendering in the specified texture location. Multiple textures can be sent
	 * to the same shader if they are in different locations.
	 */
	public void bind(int num) {
		glActiveTexture(GL_TEXTURE0 + num);
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public static void unBind(int num) {
		glActiveTexture(GL_TEXTURE0 + num);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
}
