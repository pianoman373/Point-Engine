package com.team.engine.rendering;

import com.team.engine.vecmath.Vec2;

/**
 * Basically one big collection of meshes. Think of this class as one big obj file.
 */
public class Primitives {
	public static float[] cube(float uvScale) {
		return new float[] {
				 // Positions           // Normals         // Texture Coords
				0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  uvScale, uvScale,
			    0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  uvScale, 0.0f,
			    -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,
			    -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,
			    -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f, uvScale,
			    0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  uvScale, uvScale,

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

			    0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, uvScale,
			    0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  uvScale, uvScale,
			    0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  uvScale, 0.0f,
			    0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  uvScale, 0.0f,
			    0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
			    0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, uvScale,

			    -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, uvScale,
			     0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  uvScale, uvScale,
			     0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  uvScale, 0.0f,
			     0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  uvScale, 0.0f,
			    -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 0.0f,
			    -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, uvScale,

			    0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  uvScale, 0.0f,
			    0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  uvScale, uvScale,
			    -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, uvScale,
			    -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, uvScale,
			    -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 0.0f,
			    0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  uvScale, 0.0f,
		};
	}
	
	public static Mesh debugSphere(int segments) {
		ModelBuilder mb = new ModelBuilder();
		
//		for (float i = 0; i < Math.PI*2; i += (Math.PI*2 / segments)) {
//			for (float j = 0; j < Math.PI*2; j += (Math.PI*2 / segments)) {
//				float lastj = (float)(j -(Math.PI*2 / segments));
//				float lasti = (float)(i -(Math.PI*2 / segments));
//				
//				//mb.vertex(0, (float)Math.sin(lasti), (float)Math.cos(lasti));
//				//mb.vertex(0, (float)Math.sin(lasti), (float)Math.cos(i));
//				
//				mb.vertex((float)(Math.sin(lastj)) * (float)Math.sin(i), (float)Math.cos(i), (float)Math.cos(lastj) * (float)Math.sin(i));
//				mb.vertex((float)Math.sin(j) * (float)Math.sin(i), (float)Math.cos(i), (float)Math.cos(j) * (float)Math.sin(i));
//				
//				mb.vertex((float)Math.sin(j) * (float)Math.sin(lasti), (float)Math.cos(lasti), (float)Math.cos(j) * (float)Math.sin(lasti));
//				mb.vertex((float)Math.sin(j) * (float)Math.sin(i), (float)Math.cos(i), (float)Math.cos(j) * (float)Math.sin(i));
//			}
//		}
		
		for (float i = 0; i < Math.PI*2; i += (Math.PI*2 / segments)) {
			float lasti = (float)(i -(Math.PI*2 / segments));
			
			mb.vertex(0, (float)Math.sin(lasti), (float)Math.cos(lasti));
			mb.vertex(0, (float)Math.sin(i), (float)Math.cos(i));
			
			mb.vertex((float)Math.sin(lasti), (float)Math.cos(lasti), 0);
			mb.vertex((float)Math.sin(i), (float)Math.cos(i), 0);
			
			mb.vertex((float)Math.sin(lasti), 0, (float)Math.cos(lasti));
			mb.vertex((float)Math.sin(i), 0, (float)Math.cos(i));
		}
		
		return mb.toMesh();
	}

	public static Mesh debugCube() {
		ModelBuilder mb = new ModelBuilder();
		float i = 0.5f;
		
		//bottom face face
		mb.vertex(-i, -i, -i);
		mb.vertex(i, -i, -i);
		
		mb.vertex(-i, -i, -i);
		mb.vertex(-i, -i, i);
		
		mb.vertex(-i, -i, i);
		mb.vertex(i, -i, i);
		
		mb.vertex(i, -i, i);
		mb.vertex(i, -i, -i);
		
		//top face
		mb.vertex(-i, i, -i);
		mb.vertex(i, i, -i);
		
		mb.vertex(-i, i, -i);
		mb.vertex(-i, i, i);
		
		mb.vertex(-i, i, i);
		mb.vertex(i, i, i);
		
		mb.vertex(i, i, i);
		mb.vertex(i, i, -i);
		
		//connectors
		mb.vertex(i, -i, i);
		mb.vertex(i, i, i);
		
		mb.vertex(-i, -i, -i);
		mb.vertex(-i, i, -i);
		
		mb.vertex(-i, -i, i);
		mb.vertex(-i, i, i);
		
		mb.vertex(i, -i, -i);
		mb.vertex(i, i, -i);
		
		return mb.toMesh();
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
			-0.5f, -0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  start.x, start.y,
		     0.5f, -0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  end.x, start.y,
		     0.5f,  0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  end.x, end.y,
		     0.5f,  0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  end.x, end.y,
		    -0.5f,  0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  start.x, end.y,
		    -0.5f, -0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  start.x, start.y
		};
	}

	public static float[] plane(float uvScale) {
		return new float[] {
		     0.5f,  0.0f, -0.5f,  0.0f,  1.0f,  0.0f,  uvScale, uvScale,
			-0.5f,  0.0f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, uvScale,
		     0.5f,  0.0f,  0.5f,  0.0f,  1.0f,  0.0f,  uvScale, 0.0f,
		     -0.5f,  0.0f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, uvScale,
		    -0.5f,  0.0f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 0.0f,
		    0.5f,  0.0f,  0.5f,  0.0f,  1.0f,  0.0f,  uvScale, 0.0f
		};
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
