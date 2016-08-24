package com.team.engine;

import java.util.ArrayList;

public class ModelBuilder {
	ArrayList<Float> positions;
	ArrayList<Float> normals;
	ArrayList<Float> uvs;
	
	public ModelBuilder() {
		positions = new ArrayList<Float>();
		normals = new ArrayList<Float>();
		uvs = new ArrayList<Float>();
	}
	
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
	
	public void vertex(float x, float y, float z, float u, float v) {
		vertex(x, y, z, 0f, 0f, 0f, u, v);
	}
	
	public void square(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2) {
		vertex(x1, y1, 0.0f, u1, v2);
		vertex(x2, y1, 0.0f, u2, v2);
	    vertex(x2, y2, 0.0f, u2, v1);
	    vertex(x2, y2, 0.0f, u2, v1);
	    vertex(x1, y2, 0.0f, u1, v1);
	    vertex(x1, y1, 0.0f, u1, v2);
	}
	
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
