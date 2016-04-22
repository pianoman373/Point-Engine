package com.team.engine;

import com.team.engine.vecmath.Vec2;

public class Tileset2D {
	public Texture image;
	public int gridX;
	public int gridY;
	public Mesh mesh;
	
	public Tileset2D(String image, int gridX, int gridY) {
		this.image = new Texture(image);
		this.gridX = gridX;
		this.gridY = gridY;
		
		float offsetX = 1;
		float offsetY = 4;
		
		float pixelX = 1 / this.image.dimensions.x;
		float pixelY = 1 / this.image.dimensions.y;
		
		//this.mesh = new Mesh(Primitives.sprite(new Vec2(0f, 0f), new Vec2(0.5f, 0.5f)));
		System.out.println(pixelY * gridY);
		this.mesh = new Mesh(Primitives.sprite(new Vec2((offsetX * gridX * pixelX), (offsetY * gridY * pixelY)), new Vec2(pixelX * gridX + (offsetX * gridX * pixelX), pixelY * gridY + (offsetY * gridY * pixelY))));
	}
}
