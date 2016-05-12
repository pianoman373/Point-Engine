package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

public class Cubemap extends Texture {
	
	public Cubemap(String[] images) {
		super(glGenTextures(), null);
		glBindTexture(GL_TEXTURE_CUBE_MAP, this.id);
		
		for (int i = 0; i < 6; i++) {
			RawImage image = Texture.getRawImage("resources/textures/skybox/" + images[i]);
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA8, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.data);
		}
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
	}
	
	/**
	 * Binds the texture for rendering in the specified texture location. Multiple textures can be sent
	 * to the same shader if they are in different locations.
	 */
	public void bind(int num) {
		glActiveTexture(GL_TEXTURE0 + num);
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
		glActiveTexture(GL_TEXTURE0);
	}
	
	public static void unBind(int num) {
		glActiveTexture(GL_TEXTURE0 + num);
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		glActiveTexture(GL_TEXTURE0);
	}
	
	/**
	 * Binds the texture for rendering in the first texture location.
	 */
	public void bind() {
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
	}
}
