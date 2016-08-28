package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

/**
 * The cubemap is simply an array of 6 textures stored in an object. You can specify 
 * a cubemap from an array of strings for the locations, or one string to use a single directory
 * containing the textures.
 */
public class Cubemap extends Texture {	
	public Cubemap(String[] images) {
		super(glGenTextures(), null);
		glBindTexture(GL_TEXTURE_CUBE_MAP, this.id);
		
		//iterate over all 6 textures and send their raw data to the cubemap
		for (int i = 0; i < 6; i++) {
			RawImage image = Texture.getRawImage(Constants.RESOURCE_PATH + "textures/" + images[i]);
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA8, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.data);
		}
		
		//interpolation settings and texture wrapping for the texture
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
	}

	/**
	 * Create a cube map by automatically searching the specified folder for cubemap textures.
	 */
	public Cubemap(String folder) {
		this(new String[] {
				folder + "/right.png",
				folder + "/left.png",
				folder + "/top.png",
				folder + "/bottom.png",
				folder + "/back.png",
				folder + "/front.png"
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
