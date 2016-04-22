package com.team.engine;

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
	
	public static float[] sprite(float uvScale) {
		return new float[] {
			-0.5f, -0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f,
		     0.5f, -0.5f, 0.5f,  0.0f,  0.0f, -1.0f,  uvScale, 0.0f,
		     0.5f,  0.5f, 0.5f,  0.0f,  0.0f, -1.0f,  uvScale, uvScale,
		     0.5f,  0.5f, 0.5f,  0.0f,  0.0f, -1.0f,  uvScale, uvScale,
		    -0.5f,  0.5f, 0.5f,  0.0f,  0.0f, -1.0f,  0.0f, uvScale,
		    -0.5f, -0.5f, 0.5f,  0.0f,  0.0f, -1.0f,  0.0f, 0.0f
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
}
