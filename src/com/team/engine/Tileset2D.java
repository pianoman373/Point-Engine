package com.team.engine;

import com.team.engine.vecmath.Vec2;

public class Tileset2D {
	public Texture image;
	public int gridX;
	public int gridY;
	public int maxX;
	public int maxY;
	public Mesh mesh;
	
	public Tileset2D(String image, int gridX, int gridY) {
		this.image = new Texture(image);
		this.gridX = gridX;
		this.gridY = gridY;
		
		float pixelX = 1 / this.image.dimensions.x;
		float pixelY = 1 / this.image.dimensions.y;
		
		maxX = (int)Math.floor(this.image.dimensions.x / gridX);
		maxY = (int)Math.floor(this.image.dimensions.y / gridX);
		
		System.out.println(maxX + ", " + maxY);
		
		//this.mesh = new Mesh(Primitives.sprite(new Vec2(0f, 0f), new Vec2(0.5f, 0.5f)));
		System.out.println(pixelY * gridY);
		this.mesh = new Mesh(Primitives.sprite(new Vec2(0, 0), new Vec2(pixelX * gridX, pixelY * gridY)));
	}
}
