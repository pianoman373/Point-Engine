package com.team.engine;

import com.team.engine.vecmath.Vec2;

/**
 * Basically one big collection of meshes. Think of this class as one big obj file.
 */
public class Primitives {	
	public static float[] cube(float uvScale) {
		return new float[] {
				 // Positions           // Normals         // Texture Coords
			    -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,
			     0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  uvScale, 0.0f,
			     0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  uvScale, uvScale,
			     0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  uvScale, uvScale,
			    -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, uvScale,
			    -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,

			    -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 0.0f,
			     0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   uvScale, 0.0f,
			     0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   uvScale, uvScale,
			     0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   uvScale, uvScale,
			    -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, uvScale,
			    -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 0.0f,

			    -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  uvScale, 0.0f,
			    -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  uvScale, uvScale,
			    -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, uvScale,
			    -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, uvScale,
			    -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
			    -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  uvScale, 0.0f,

			     0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  uvScale, 0.0f,
			     0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  uvScale, uvScale,
			     0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, uvScale,
			     0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, uvScale,
			     0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
			     0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  uvScale, 0.0f,

			    -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, uvScale,
			     0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  uvScale, uvScale,
			     0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  uvScale, 0.0f,
			     0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  uvScale, 0.0f,
			    -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 0.0f,
			    -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, uvScale,

			    -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, uvScale,
			     0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  uvScale, uvScale,
			     0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  uvScale, 0.0f,
			     0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  uvScale, 0.0f,
			    -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 0.0f,
			    -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, uvScale
		};
	}
	
	public static float[] skybox() {
		return new float[] {
				// Positions         // Normals        // Texture Coords
				-1.0f,  1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f,  1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f,  1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,

			    -1.0f, -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f,  1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f,  1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f,  1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f, -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,

			     1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f, -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f,  1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f,  1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f,  1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,

			    -1.0f, -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f,  1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f,  1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f,  1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f, -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f, -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,

			    -1.0f,  1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f,  1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f,  1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f,  1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f,  1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f,  1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,

			    -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f, -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			    -1.0f, -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f, -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f
		};
	}
	
	public static float[] sprite(Vec2 start, Vec2 end) {
		return new float[] {
			-0.5f, -0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  start.x, end.y,
		     0.5f, -0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  end.x, end.y,
		     0.5f,  0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  end.x, start.y,
		     0.5f,  0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  end.x, start.y,
		    -0.5f,  0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  start.x, start.y,
		    -0.5f, -0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  start.x, end.y
		};
	}
	
	public static float[] plane(float uvScale) {
		return new float[] {
		     0.5f,  0.0f, -0.5f,  0.0f,  1.0f,  0.0f,  uvScale, uvScale,
			-0.5f,  0.0f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, uvScale,
		     0.5f,  0.0f,  0.5f,  0.0f,  1.0f,  0.0f,  uvScale, 0.0f,
		     0.5f,  0.0f,  0.5f,  0.0f,  1.0f,  0.0f,  uvScale, 0.0f,
		    -0.5f,  0.0f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 0.0f,
		    -0.5f,  0.0f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, uvScale
		};
	}
	
	public static Mesh planeMesh(float uvScale) {
		float[] positions = new float[] {
			0.5f,  0.0f, 0.5f,
		     0.5f, 0.0f, -0.5f,
		    -0.5f, 0.0f, -0.5f,
		    -0.5f,  0.0f, 0.5f
		};
		
		float[] normals = new float[] {
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f
		};
		
		float[] uvs = new float[] {
				uvScale, 0.0f,
				uvScale, uvScale,
				0.0f, uvScale,
				0.0f, 0.0f
		};
		
		int[] indices = new int[] {
				0, 1, 3,   // First Triangle
			    1, 2, 3    // Second Triangle
		};
		
		return new Mesh(positions, normals, uvs, indices);
	}
	
	public static float[] framebuffer() {
		return new float[] {  
			    // Positions   		//normals		  // TexCoords
			    -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
			    -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
			     1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,

			    -1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
			     1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			     1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f
		};	
	}
}
