package com.team.engine;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;

import java.nio.ByteBuffer;

import com.team.engine.vecmath.Vec2i;

public class Framebuffer {
	private int fbo;
	private int[] textures;
	public Texture[] tex;
	
	public Framebuffer(Vec2i dimensions, int count, boolean doRbo) {
		fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		
		textures = new int[count];
		tex = new Texture[count];
		
		for (int i = 0; i < count; i++) {
			textures[i] = glGenTextures();
		}
		
		for (int i = 0; i < count; i++) {
			glBindTexture(GL_TEXTURE_2D, textures[i]);
			  
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, dimensions.x, dimensions.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
	
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); 
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glBindTexture(GL_TEXTURE_2D, 0);
			
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, textures[i], 0);
			
			tex[i] = new Texture(textures[i], dimensions);
		}
		
		if (doRbo) {
			int rbo = glGenRenderbuffers();
			glBindRenderbuffer(GL_RENDERBUFFER, rbo); 
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, dimensions.x, dimensions.y);  
			glBindRenderbuffer(GL_RENDERBUFFER, 0);
			
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);
			
			if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
				System.out.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
			}
		}
		
		int[] attachments = new int[] { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1 };
		glDrawBuffers(GLBuffers.StaticBuffer(attachments));
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);  
	}
	
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
	}
	
	public static void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
}
