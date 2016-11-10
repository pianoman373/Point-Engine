package com.team.engine.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import com.team.engine.Settings;

/**
 * The cubemap is simply an array of 6 textures stored in an object. You can specify 
 * a cubemap from an array of strings for the locations, or one string to use a single directory
 * containing the textures.
 */
public class Cubemap {	
	public int id;
	
	public Cubemap(String[] images) {
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, this.id);
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_BASE_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_LEVEL, 10);
		
		STBImage.stbi_set_flip_vertically_on_load(false);
		
		//iterate over all 6 textures and send their raw data to the cubemap
		for (int i = 0; i < 6; i++) {
			IntBuffer w = BufferUtils.createIntBuffer(1);
			IntBuffer h = BufferUtils.createIntBuffer(1);
			IntBuffer comp = BufferUtils.createIntBuffer(1);
			
			FloatBuffer buffer = STBImage.stbi_loadf(Settings.RESOURCE_PATH + "textures/" + images[i], w, h, comp, 3);
			int width = w.get(0);
			int height = w.get(0);
			
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, width, height, 0, GL_RGB, GL_FLOAT, buffer);
		}
		
		//interpolation settings and texture wrapping for the texture
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
	}

	/**
	 * Create a cube map by automatically searching the specified folder for cubemap textures.
	 */
	public Cubemap(String folder) {
		this(new String[] {
				folder + "/posx.hdr",
				folder + "/negx.hdr",
				folder + "/posy.hdr",
				folder + "/negy.hdr",
				folder + "/posz.hdr",
				folder + "/negz.hdr"
		});
	}
	
	/**
	 * Binds the texture for rendering in the specified texture location. Multiple textures can be sent
	 * to the same shader if they are in different locations.
	 */
	public void bind(int num) {
		glActiveTexture(GL_TEXTURE0 + num);
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
	}
	
	public static void unBind(int num) {
		glActiveTexture(GL_TEXTURE0 + num);
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
	}
	
	/**
	 * Binds the texture for rendering in texture location 0.
	 */
	public void bind() {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
	}
	
	public static void unBind() {
		unBind(0);
	}
}
