package com.team.engine;

import com.team.engine.vecmath.Vec2;

public class Tile2D {
	Vec2 pixelOffset;
	
	public Tile2D(Tileset2D tileset, int xCoord, int yCoord) {
		float pixelX = 1 / tileset.image.dimensions.x;
		float pixelY = 1 / tileset.image.dimensions.x;
		
		pixelOffset = new Vec2(tileset.gridX * pixelX * xCoord, tileset.gridY * pixelY * yCoord);
	}
}
