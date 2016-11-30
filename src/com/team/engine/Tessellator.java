package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Tessellator {
	private int VBO;
	private int VAO;
	ArrayList<Float> vertices;
	private int drawMode;
	private boolean drawing = false;
	
	public Tessellator() {
		VBO = glGenBuffers();
		
		VAO = glGenVertexArrays();
		
		vertices = new ArrayList<>();
	}
	
	private void setupAttribs() {
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
		glEnableVertexAttribArray(2);
	}
	
	public void begin(int drawMode) {
		if (!drawing) {
			vertices.clear();
			this.drawMode = drawMode;
			this.drawing = true;
		}
		else {
			throw new IllegalStateException("Already drawing. Call end() to end the draw call.");
		}
	}
	
	public void vertex(float x, float y, float z, float nx, float ny, float nz, float u, float v) {
		if (drawing) {
			vertices.add(x); vertices.add(y); vertices.add(z);
			vertices.add(nx); vertices.add(ny); vertices.add(nz);
			vertices.add(u); vertices.add(v);
		}
		else {
			throw new IllegalStateException("Cannot add vertex data when not drawing. Call begin() first.");
		}
	}
	
	public void vertex(float x, float y, float z) {
		vertex(x, y, z, 0, 0, 0, 0, 0);
	}
	
	public void vertex(float x, float y, float z, float nx, float ny, float nz) {
		vertex(x, y, z, nx, ny, nz, 0, 0);
	}
	
	public void end() {
		if (drawing) {
			glBindVertexArray(VAO);
			FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.size());
			
			vertices.forEach((value) -> buffer.put(value));
			
			buffer.flip();
			
			glBindBuffer(GL_ARRAY_BUFFER, VBO);
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
			
			setupAttribs();
			
			glDrawArrays(this.drawMode, 0, vertices.size() / 8);
			
			glBindVertexArray(0);
			
			drawing = false;
		}
		else {
			throw new IllegalStateException("Cannot call end when not drawing. Call begin() first.");
		}
	}
}
