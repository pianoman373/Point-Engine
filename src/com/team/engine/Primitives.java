package com.team.engine;

import com.team.engine.vecmath.Vec2;

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
	
	public static float[] sprite(Vec2 start, Vec2 end) {
		return new float[] {
			-0.5f, -0.5f, 0.0f,  0.0f,  0.0f, -1.0f,  start.x, end.y,
		     0.5f, -0.5f, 0.5f,  0.0f,  0.0f, -1.0f,  end.x, end.y,
		     0.5f,  0.5f, 0.5f,  0.0f,  0.0f, -1.0f,  end.x, start.y,
		     0.5f,  0.5f, 0.5f,  0.0f,  0.0f, -1.0f,  end.x, start.y,
		    -0.5f,  0.5f, 0.5f,  0.0f,  0.0f, -1.0f,  start.x, start.y,
		    -0.5f, -0.5f, 0.5f,  0.0f,  0.0f, -1.0f,  start.x, end.y
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
