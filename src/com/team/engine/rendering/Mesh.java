package com.team.engine.rendering;

import static com.team.engine.Globals.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.team.engine.Util;
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
	private int VBO;
	private int EBO;
	private int length;
	private boolean indexed = false;
	
	private Mesh(int VAO, int VBO, int EBO, int length, boolean indexed) {
		this.VAO = VAO;
		this.VBO = VBO;
		this.EBO = EBO;
		this.indexed = indexed;
		this.length = length;
	}
	
	private static void setupAttribs(boolean tangents) {
		if (tangents) {
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 11 * 4, 0);
			glEnableVertexAttribArray(0);
			
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 11 * 4, 3 * 4);
			glEnableVertexAttribArray(1);
			
			glVertexAttribPointer(2, 2, GL_FLOAT, false, 11 * 4, 6 * 4);
			glEnableVertexAttribArray(2);
			
			glVertexAttribPointer(3, 3, GL_FLOAT, false, 11 * 4, 8 * 4);
			glEnableVertexAttribArray(3);
		} else {
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
			glEnableVertexAttribArray(0);
			
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
			glEnableVertexAttribArray(1);
			
			glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
			glEnableVertexAttribArray(2);
		}
	}
	
	//takes a normal buffer of vertices and returns the same one but with tangents
	private static float[] toTangentBuffer(float[] vertices) {
		ArrayList<Float> newVertices = new ArrayList<>();
		
		for (int i = 0; i < vertices.length; i += 8 * 3) {
			
			//make vectors out of the 3 vertices
			Vec3 pos1 = vec3(vertices[i], vertices[i+1], vertices[i+2]);
			Vec3 norm1 = vec3(vertices[i+3], vertices[i+4], vertices[i+5]);
			Vec2 uv1 = vec2(vertices[i+6], vertices[i+7]);
			
			Vec3 pos2 = vec3(vertices[i+8], vertices[i+9], vertices[i+10]);
			Vec3 norm2 = vec3(vertices[i+11], vertices[i+12], vertices[i+13]);
			Vec2 uv2 = vec2(vertices[i+14], vertices[i+15]);
			
			Vec3 pos3 = vec3(vertices[i+16], vertices[i+17], vertices[i+18]);
			Vec3 norm3 = vec3(vertices[i+19], vertices[i+20], vertices[i+21]);
			Vec2 uv3 = vec2(vertices[i+22], vertices[i+23]);

			//calculate tangent
			Vec3 edge1 = pos2.subtract(pos1);
			Vec3 edge2 = pos3.subtract(pos1);
			Vec2 deltaUV1 = uv2.subtract(uv1);
			Vec2 deltaUV2 = uv3.subtract(uv1);  

			float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);
			
			Vec3 tangent = vec3();
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
	 * 
	 * Indexed meshes can require less data for meshes than non-indexed meshes. Each vertex is regarded in a list with a certain index,
	 * the index array then contains a single number per vertex which points to the real vertex. This eliminates having to specify the same vertex twice.
	 * Indexing will have no affect on a mesh that does not have smoothed normals, e.g cubes.
	 * 
	 * tangents specifies whether tangents should be calculated for the mesh. Set it to false
	 * if you know your model will not be normal mapped. A large model with tangents will render significantly slower
	 * than one without tangents. If the model is normal mapped and tangents are disabled, unexpected results will happen.
	 */
	public static Mesh rawIndexed(float[] vertices, int[] indices, boolean tangents) {
		int VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		
		int EBO = glGenBuffers();
		int VBO = glGenBuffers();
		
		FloatBuffer vertexBuffer = Util.toBuffer(tangents ? toTangentBuffer(vertices) : vertices);
		IntBuffer indexBuffer = Util.toBuffer(indices);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		setupAttribs(tangents);
		
		glBindVertexArray(0);
		
		return new Mesh(VAO, VBO, EBO, vertices.length / 8, true);
	}
	
	/**
	 * Creates an indexed mesh out of the specified vertex data arrays and indices.
	 * 
	 * Indexed meshes can require less data for meshes than non-indexed meshes. Each vertex is regarded in a list with a certain index,
	 * the index array then contains a single number per vertex which points to the real vertex. This eliminates having to specify the same vertex twice.
	 * Indexing will have no affect on a mesh that does not have smoothed normals, e.g cubes.
	 * 
	 * tangents specifies whether tangents should be calculated for the mesh. Set it to false
	 * if you know your model will not be normal mapped. A large model with tangents will render significantly slower
	 * than one without tangents. If the model is normal mapped and tangents are disabled, unexpected results will happen.
	 */
	public static Mesh normalIndexed(float[] positions, float[] normals, float[] uvs, int[] indices, boolean tangents) {
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
		
		return rawIndexed(vertices, indices, tangents);
	}
	
	/**
	 * Creates a non-indexed mesh out of the specified vertex data arrays.
	 * 
	 * Non-indexed meshes require much more data than indexed meshes. If two vertices
	 * share the same point on a mesh, it will need to be included twice. However, they tend to be easier
	 * to make.
	 * 
	 * tangents specifies whether tangents should be calculated for the mesh. Set it to false
	 * if you know your model will not be normal mapped. A large model with tangents will render significantly slower
	 * than one without tangents. If the model is normal mapped and tangents are disabled, unexpected results will happen.
	 */
	public static Mesh normal(float[] positions, float[] normals, float[] uvs, boolean tangents) {
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
		
		return raw(vertices, tangents);
	}
	
	/**
	 * Creates a non-indexed mesh out of the specified vertex data array.
	 * 
	 * Non-indexed meshes require much more data than indexed meshes. If two vertices
	 * share the same point on a mesh, it will need to be included twice. However, they tend to be easier
	 * to make.
	 * 
	 * The mesh vertex data is in the form: x, y, z, nx, ny, nz, u, v
	 * 
	 * tangents specifies whether tangents should be calculated for the mesh. Set it to false
	 * if you know your model will not be normal mapped. A large model with tangents will render significantly slower
	 * than one without tangents. If the model is normal mapped and tangents are disabled, unexpected results will happen.
	 */
	public static Mesh raw(float[] vertices, boolean tangents) {
		
		int VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		
		int EBO = glGenBuffers();
		int VBO = glGenBuffers();
		
		FloatBuffer vertexBuffer = Util.toBuffer(tangents ? toTangentBuffer(vertices) : vertices);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		setupAttribs(tangents);
		
		glBindVertexArray(0);
		
		return new Mesh(VAO, VBO, EBO, vertices.length / 8, false);
	}
	
	/**
	 * Draws the mesh. The mesh will render either indexed or direct depending on 
	 * which constructor was used.
	 */
	public void draw() {
		draw(GL_TRIANGLES);
	}
	
	/**
	 * Draws the Mesh with the specified mode. Common modes are GL_TRIANGLES,
	 * GL_LINES, and GL_QUADS.
	 */
	public void draw(int mode) {
		glBindVertexArray(VAO);
		if (indexed) {
			glDrawElements(mode, length, GL_UNSIGNED_INT, 0);
		}
		else {
			glDrawArrays(mode, 0, length);
		}
		glBindVertexArray(0);
	}
	
	public void delete() {
		glDeleteVertexArrays(VAO);
	    glDeleteBuffers(VBO);
	    glDeleteBuffers(EBO);
	}
}
