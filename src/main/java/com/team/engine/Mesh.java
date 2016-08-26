package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * A Mesh object is used for keeping track of the vertex arrays used on GPU side.
 * When the mesh is constructed, the mesh data sent through is passed onto the GPU memory, and the mesh data
 * sent as the parameter is discarded. The Mesh object, as long as it is not discarded, will keep track of the pointers to the
 * mesh data on the GPU. Once it is time to draw the mesh, it simply tells the GPU to draw the mesh stored in memory. Mesh objects
 * work similar to Shader objects in terms of design. Currently mesh objects cannot be changed after constructed.
 */
public class Mesh {
	private int VAO;
	private int EBO;
	private int VBO;
	
	private int length;
	private boolean indexed = false;
	
	/**
	 * Creates an indexed mesh out of the specified vertex data array and indices.
	 * 
	 * The mesh vertex data is in the form: x, y, z, nx, ny, nz, u, v
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
	
	/**
	 * Creates an indexed mesh out of the specified vertex data arrays and indices.
	 */
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
	
	/**
	 * Creates a non-indexed mesh out of the specified vertex data arrays.
	 */
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
	 * Creates a non-indexed mesh out of the specified vertex data array.
	 * 
	 * The mesh vertex data is in the form: x, y, z, nx, ny, nz, u, v
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
	
	/**
	 * Draws the mesh. The mesh will render either indexed or direct depending on 
	 * which constructor was used.
	 */
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
