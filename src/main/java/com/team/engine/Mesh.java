package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

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
	
	//takes a normal buffer of vertices and returns the same one but with tangents
	private float[] toTangentBuffer(float[] vertices) {
		ArrayList<Float> newVertices = new ArrayList<>();
		
		for (int i = 0; i < vertices.length; i += 8 * 3) {
			
			//make vectors out of the 3 vertices
			Vec3 pos1 = new Vec3(vertices[i], vertices[i+1], vertices[i+2]);
			Vec3 norm1 = new Vec3(vertices[i+3], vertices[i+4], vertices[i+5]);
			Vec2 uv1 = new Vec2(vertices[i+6], vertices[i+7]);
			
			Vec3 pos2 = new Vec3(vertices[i+8], vertices[i+9], vertices[i+10]);
			Vec3 norm2 = new Vec3(vertices[i+11], vertices[i+12], vertices[i+13]);
			Vec2 uv2 = new Vec2(vertices[i+14], vertices[i+15]);
			
			Vec3 pos3 = new Vec3(vertices[i+16], vertices[i+17], vertices[i+18]);
			Vec3 norm3 = new Vec3(vertices[i+19], vertices[i+20], vertices[i+21]);
			Vec2 uv3 = new Vec2(vertices[i+22], vertices[i+23]);

			//calculate tangent
			Vec3 edge1 = pos2.subtract(pos1);
			Vec3 edge2 = pos3.subtract(pos1);
			Vec2 deltaUV1 = uv2.subtract(uv1);
			Vec2 deltaUV2 = uv3.subtract(uv1);  

			float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);
			
			Vec3 tangent = new Vec3();
			tangent.x = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x);
			if (tangent.x == -0) tangent.x = 0;
			tangent.y = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y);
			if (tangent.y == -0) tangent.y = 0;
			tangent.z = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z);
			if (tangent.z == -0) tangent.z = 0;
			tangent = tangent.normalize();
			
			//add everything to new array
			newVertices.add(pos1.x); newVertices.add(pos1.y); newVertices.add(pos1.z);
			newVertices.add(norm1.x); newVertices.add(norm1.y); newVertices.add(norm1.z);
			newVertices.add(uv1.x); newVertices.add(uv1.y);
			newVertices.add(tangent.x); newVertices.add(tangent.y); newVertices.add(tangent.z);
			
			newVertices.add(pos2.x); newVertices.add(pos2.y); newVertices.add(pos2.z);
			newVertices.add(norm2.x); newVertices.add(norm2.y); newVertices.add(norm2.z);
			newVertices.add(uv2.x); newVertices.add(uv2.y);
			newVertices.add(tangent.x); newVertices.add(tangent.y); newVertices.add(tangent.z);
			
			newVertices.add(pos3.x); newVertices.add(pos3.y); newVertices.add(pos3.z);
			newVertices.add(norm3.x); newVertices.add(norm3.y); newVertices.add(norm3.z);
			newVertices.add(uv3.x); newVertices.add(uv3.y);
			newVertices.add(tangent.x); newVertices.add(tangent.y); newVertices.add(tangent.z);
		}
		
		//finally turn the array list into a normal array
		float[] finalVertices = new float[newVertices.size()];
		
		for (int i = 0; i < newVertices.size(); i++) {
			finalVertices[i] = newVertices.get(i);
		}
		
		return finalVertices;
	}
	
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
		
		FloatBuffer vertexBuffer = GLBuffers.StaticBuffer(toTangentBuffer(vertices));
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
		
		FloatBuffer vertexBuffer = GLBuffers.StaticBuffer(toTangentBuffer(vertices));
		IntBuffer indexBuffer = GLBuffers.StaticBuffer(indices);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 11 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 11 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 11 * 4, 6 * 4);
		glEnableVertexAttribArray(2);
		
		glVertexAttribPointer(3, 3, GL_FLOAT, false, 11 * 4, 8 * 4);
		glEnableVertexAttribArray(3);
		
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
		
		FloatBuffer vertexBuffer = GLBuffers.StaticBuffer(toTangentBuffer(vertices));
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 11 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 11 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 11 * 4, 6 * 4);
		glEnableVertexAttribArray(2);
		
		glVertexAttribPointer(3, 3, GL_FLOAT, false, 11 * 4, 8 * 4);
		glEnableVertexAttribArray(3);
		
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
		
		FloatBuffer vertexBuffer = GLBuffers.StaticBuffer(toTangentBuffer(vertices));
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 11 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 11 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 11 * 4, 6 * 4);
		glEnableVertexAttribArray(2);
		
		glVertexAttribPointer(3, 3, GL_FLOAT, false, 11 * 4, 8 * 4);
		glEnableVertexAttribArray(3);
		
		glBindVertexArray(0);
	}
	
	/**
	 * Draws the mesh. The mesh will render either indexed or direct depending on 
	 * which constructor was used.
	 */
	public void draw() {
		glBindVertexArray(VAO);
		if (indexed) {
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
