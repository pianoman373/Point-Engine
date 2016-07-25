package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
	private int VAO;
	private int EBO;
	private int VBO;
	
	private int length;
	private boolean indexed = false;
	
	/**
	 * Makes a mesh object from specified mesh data. Just like Texture and Shader, bind before rendering.
	 * 
	 * TODO: Currently positions, normals, and uv's are all in the verices array since OpenGL likes them in one big buffer.
	 * someday we should have them as seperate arrays and combine them in here.
	 * 
	 * The mesh is currently set up to ignore indices though, so don't use this yet.
	 */
	public Mesh(float[] vertices, int[] indices) {
		indexed = true;
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		
		EBO = glGenBuffers();
		VBO = glGenBuffers();
		
		length = vertices.length / 8;
		
		FloatBuffer vertexBuffer = GLBuffers.StaticBuffer(vertices);
		IntBuffer indexBuffer = GLBuffers.StaticBuffer(indices);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
		glEnableVertexAttribArray(2);
		
		glBindVertexArray(0);
	}
	
	public Mesh(float[] positions, float[] normals, float[] uvs, int[] indices) {
		int size = positions.length + normals.length + uvs.length;
		
		float[] vertices = new float[size];
		
		int j = 0;
		int k = 0;
		for (int i = 0; i < size; i += 8) {
			vertices[i] = positions[j];
			vertices[i + 1] = positions[j + 1];
			vertices[i + 2] = positions[j + 2];
			
			vertices[i + 3] = normals[j];
			vertices[i + 4] = normals[j + 1];
			vertices[i + 5] = normals[j + 2];
			
			vertices[i + 6] = uvs[k];
			vertices[i + 7] = uvs[k + 1];
			
			j += 3;
			k += 2;
		}
		
		
		
		indexed = true;
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		
		EBO = glGenBuffers();
		VBO = glGenBuffers();
		
		length = indices.length;
		
		FloatBuffer vertexBuffer = GLBuffers.StaticBuffer(vertices);
		IntBuffer indexBuffer = GLBuffers.StaticBuffer(indices);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
		glEnableVertexAttribArray(2);
		
		glBindVertexArray(0);
	}
	
	public Mesh(float[] positions, float[] normals, float[] uvs) {
		int size = positions.length + normals.length + uvs.length;
		
		float[] vertices = new float[size];
		
		int j = 0;
		int k = 0;
		for (int i = 0; i < size; i += 8) {
			vertices[i] = positions[j];
			vertices[i + 1] = positions[j + 1];
			vertices[i + 2] = positions[j + 2];
			
			vertices[i + 3] = normals[j];
			vertices[i + 4] = normals[j + 1];
			vertices[i + 5] = normals[j + 2];
			
			vertices[i + 6] = uvs[k];
			vertices[i + 7] = uvs[k + 1];
			
			j += 3;
			k += 2;
		}
		
		
		
		indexed = false;
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		
		VBO = glGenBuffers();
		
		length = positions.length;
		
		FloatBuffer vertexBuffer = GLBuffers.StaticBuffer(vertices);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
		glEnableVertexAttribArray(2);
		
		glBindVertexArray(0);
	}
	
	/**
	 * Same as first constructor except without indices.
	 */
	public Mesh(float[] vertices) {
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		
		EBO = glGenBuffers();
		VBO = glGenBuffers();
		
		length = vertices.length / 8;
		
		FloatBuffer vertexBuffer = GLBuffers.StaticBuffer(vertices);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
		glEnableVertexAttribArray(2);
		
		glBindVertexArray(0);
	}
	
	public void draw() {
		glBindVertexArray(VAO);
		if (indexed) {
			//System.out.println("rendering indexed");
			glDrawElements(GL_TRIANGLES, length, GL_UNSIGNED_INT, 0);
		}
		else {
			glDrawArrays(GL_TRIANGLES, 0, length);
		}
		glBindVertexArray(0);
	}
	
	public void delete() {
		glDeleteVertexArrays(VAO);
	    glDeleteBuffers(VBO);
	    glDeleteBuffers(EBO);
	}
}
