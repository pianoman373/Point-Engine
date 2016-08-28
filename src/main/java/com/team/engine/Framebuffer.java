package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import com.team.engine.vecmath.Vec2i;

public class Framebuffer {
	private int fbo;
	public Texture[] tex;
	
	private Framebuffer(Vec2i dimensions, int fbo, int[] textures) {
		this.tex = new Texture[textures.length];
		this.fbo = fbo;
		
		for (int i = 0; i < textures.length; i++) {
			this.tex[i] = new Texture(textures[i], dimensions);
		}
	}
	
	/**
	 * Creates a standard framebuffer.
	 * 
	 * doRbo should only be enabled if this framebuffer will be rendered to the final screen image.
	 */
	public static Framebuffer standard(Vec2i dimensions, int count, boolean doRbo) {
		int fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		
		int[] textures = new int[count];
		
		
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
		
		int[] attachments = new int[] { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2 };
		glDrawBuffers(GLBuffers.StaticBuffer(attachments));
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);  
		
		return new Framebuffer(dimensions, fbo, textures);
	}
	
	/**
	 * Creates a framebuffer for shadows.
	 */
	public static Framebuffer shadow(Vec2i dimensions) {
		int fbo = glGenFramebuffers();
		
		int depthMap;
		depthMap = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthMap);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, dimensions.x, dimensions.y, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer)null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); 
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);  
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		//glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, null); 
		float borderColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, GLBuffers.StaticBuffer(borderColor));
		
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap, 0);
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		glBindFramebuffer(GL_FRAMEBUFFER, 0); 
		
		return new Framebuffer(dimensions, fbo, new int[] {depthMap});
	}
	
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
	}
	
	public static void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
}
