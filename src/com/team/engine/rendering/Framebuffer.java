package com.team.engine.rendering;

import static com.team.engine.Globals.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import com.team.engine.GLBuffers;
import com.team.engine.vecmath.Vec2i;

public class Framebuffer {
	public int fbo;
	private int rbo;
	public Texture[] tex;
	public Vec2i dimensions;
	
	private Framebuffer(Vec2i dimensions, int fbo, int rbo, int[] textures) {
		this.tex = new Texture[textures.length];
		this.fbo = fbo;
		this.dimensions = dimensions;
		
		for (int i = 0; i < textures.length; i++) {
			this.tex[i] = new Texture(textures[i], dimensions);
		}
	}
	
	/**
	 * Creates a standard framebuffer.
	 * 
	 * doRbo should only be enabled if this framebuffer will be rendered to the final screen image.
	 */
	public static Framebuffer standard(Vec2i dimensions, boolean depth) {
		int fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		
		int[] textures = new int[1];
		
		textures[0] = glGenTextures();
		
		//texture
		glBindTexture(GL_TEXTURE_2D, textures[0]);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, dimensions.x, dimensions.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); 
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	    glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textures[0], 0);
		
		int rbo = 0;
		if (depth) {
			//depth
			rbo = glGenRenderbuffers();
			glBindRenderbuffer(GL_RENDERBUFFER, rbo); 
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, dimensions.x, dimensions.y);  
			glBindRenderbuffer(GL_RENDERBUFFER, 0);
			
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);
		}
		
		//check for errors
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			print("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
		}
		
		
		int[] attachments = new int[] { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2 };
		glDrawBuffers(GLBuffers.StaticBuffer(attachments));
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);  
		
		return new Framebuffer(dimensions, fbo, rbo, textures);
	}
	
	
	/**
	 * Creates a standard framebuffer.
	 * 
	 * doRbo should only be enabled if this framebuffer will be rendered to the final screen image.
	 */
	public static Framebuffer HdrWithBloom(Vec2i dimensions) {
		int fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		
		int[] textures = new int[2];
		
		textures[0] = glGenTextures();
		textures[1] = glGenTextures();
		
		
		//main HDR texture
		glBindTexture(GL_TEXTURE_2D, textures[0]);
		  
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, dimensions.x, dimensions.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); 
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	    glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textures[0], 0);
		
		glGenerateMipmap(GL_TEXTURE_2D);
		
		//bloom texture
		glBindTexture(GL_TEXTURE_2D, textures[1]);
		  
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, dimensions.x, dimensions.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); 
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	    glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, textures[1], 0);
		
		//depth map
		int rbo = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, rbo); 
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, dimensions.x, dimensions.y);  
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
		
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);
		
		//check for errors
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			print("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
		}
		
		int[] attachments = new int[] { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2 };
		glDrawBuffers(GLBuffers.StaticBuffer(attachments));
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);  
		
		return new Framebuffer(dimensions, fbo, rbo, textures);
	}
	
	/**
	 * Creates a framebuffer for shadows.
	 */
	public static Framebuffer shadow(Vec2i dimensions) {
		int fbo = glGenFramebuffers();
		
		int depthMap;
		depthMap = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthMap);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 10);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, dimensions.x, dimensions.y, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer)null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); 
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);  
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		
		glGenerateMipmap(GL_TEXTURE_2D);
		//glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, null); 
		float borderColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, GLBuffers.StaticBuffer(borderColor));
		
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap, 0);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0); 
		
		return new Framebuffer(dimensions, fbo, 0, new int[] {depthMap});
	}
	
	/**
	 * Applies the specified shader to perform operations on this framebuffer's texture. Results are
	 * written to the currently bound framebuffer. Calling this with no framebuffer bound will draw it to the
	 * screen.
	 */
	public void passthrough(Shader s) {
		
	}
	
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
	}
	
	public static void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void delete() {
		glDeleteFramebuffers(fbo);
        glDeleteTextures(tex[0].id);
        glDeleteRenderbuffers(rbo);
	}
}
