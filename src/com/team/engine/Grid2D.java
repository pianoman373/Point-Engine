package com.team.engine;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;

public class Grid2D {
	private byte[][] tiles;
	private Shader spriteShader;
	private Tileset2D tileset;
	private Vec2 dimensions;
	
	public Grid2D(byte[][] tiles, Tileset2D tileset, Vec2 dimensions) {
		this.tiles = tiles;
		this.spriteShader = new Shader("sprite");
		this.tileset = tileset;
		this.dimensions = dimensions;
	}
	
	public Grid2D(String tmxfile, Tileset2D tileset) {
		TMXMapReader reader = new TMXMapReader();
		
		try {
			Map map = reader.readMap(tmxfile);
			
			TileLayer layer = (TileLayer) map.getLayer(0);
			int width = layer.getWidth();
			int height = layer.getHeight();
			
			byte[][] layerTiles = new byte[width][height];
			
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					Tile tile = layer.getTileAt(x, y);
					
					if (tile != null) {
						layerTiles[x][y] = (byte)tile.getId();
					}
					else {
						layerTiles[x][y] = -1;
					}
				}
			}
			
			this.tiles = layerTiles;
			this.tileset = tileset;
			this.dimensions = new Vec2(width, height);
			this.spriteShader = new Shader("sprite");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void render() {
		spriteShader.bind();
		tileset.image.bind();
		tileset.mesh.bind();
		
		for (int x = 0; x < dimensions.x; x++) {
			for (int y = 0; y < dimensions.y; y++) {
				if (tiles[x][y] == -1)
					continue;
				
				Vec2 offset = idToOffset(tiles[x][y]);
				
				spriteShader.uniformMat4("model", new Mat4().translate(new Vec3(x, -y, 0.0f)));
				spriteShader.uniformVec2("uvOffset", new Vec2((1 / tileset.image.dimensions.x) * 16 * offset.x, (1 / tileset.image.dimensions.y) * 16 * offset.y));
				tileset.mesh.draw();
			}
		}
		
		tileset.mesh.unBind();
	}
	
	private Vec2 idToOffset(int id) {
		if (id >= tileset.maxX) {
			int x = id % (tileset.maxX);
			
			int y = (int)Math.floor(id / tileset.maxX);
			//System.out.println(id);
			return new Vec2(x, y);
		}
		else {
			return new Vec2(id, 0);
		}
	}
}
