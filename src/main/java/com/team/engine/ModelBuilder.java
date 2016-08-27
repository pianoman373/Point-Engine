package com.team.engine;

import java.util.ArrayList;

/**
 * Model builder is used for easily creating Mesh objects without dealing with vertex arrays yourself.
 */
public class ModelBuilder {
	ArrayList<Float> positions;
	ArrayList<Float> normals;
	ArrayList<Float> uvs;
	
	public ModelBuilder() {
		positions = new ArrayList<Float>();
		normals = new ArrayList<Float>();
		uvs = new ArrayList<Float>();
	}
	
	/**
	 * Adds a raw vertex to the model.
	 */
	public void vertex(float x, float y, float z, float nx, float ny, float nz, float u, float v) {
		positions.add(x);
		positions.add(y);
		positions.add(z);
		
		normals.add(nx);
		normals.add(ny);
		normals.add(nz);
		
		uvs.add(u);
		uvs.add(v);
	}
	
	/**
	 * Adds a raw vertex to the model with no normals.
	 */
	public void vertex(float x, float y, float z, float u, float v) {
		vertex(x, y, z, 0f, 0f, 0f, u, v);
	}
	
	/**
	 * Creates a square out of the specified points and uvs.
	 */
	public void square(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2) {
		vertex(x1, y1, 0.0f, u1, v2);
		vertex(x2, y1, 0.0f, u2, v2);
	    vertex(x2, y2, 0.0f, u2, v1);
	    vertex(x2, y2, 0.0f, u2, v1);
	    vertex(x1, y2, 0.0f, u1, v1);
	    vertex(x1, y1, 0.0f, u1, v2);
	}
	
	/**
	 * Creates a Mesh object from this ModelBuilder object's data. If the ModelBuilder object is used again
	 * you will have to create a new mesh again for it to take effect.
	 */
	public Mesh toMesh() {
		float[] positionArray = new float[positions.size()];
		
		for (int i = 0; i < positions.size(); i++) {
			positionArray[i] = positions.get(i);
		}
		
		float[] normalArray = new float[normals.size()];
		
		for (int i = 0; i < normals.size(); i++) {
			normalArray[i] = normals.get(i);
		}
		
		float[] uvArray = new float[uvs.size()];
		
		for (int i = 0; i < uvs.size(); i++) {
			uvArray[i] = uvs.get(i);
		}
		
		return new Mesh(positionArray, normalArray, uvArray);
	}
}
