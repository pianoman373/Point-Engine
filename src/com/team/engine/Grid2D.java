package com.team.engine;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;

import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;

public class Grid2D {
	private byte[][] tiles;
	private Shader spriteShader;
	private int width;
	private int height;
	private int mapWidth;
	private int mapHeight;
	private Vec2[] uvs;
	private Texture tex;
	private Mesh mesh;
	
	public Grid2D(byte[][] tiles, Vec2[] uvs, int width, int height, int mapWidth, int mapHeight) {
		this.tiles = tiles;
		this.spriteShader = new Shader("sprite");
		this.width = width;
		this.height = height;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.uvs = uvs;
		this.tex = new Texture("resources/retro-terrain.png", true);
		buildMesh();
	}
	
	public Grid2D(String tmxfile) {
		TMXMapReader reader = new TMXMapReader();
		
		try {
			Map map = reader.readMap(tmxfile);
			
			TileLayer layer = (TileLayer) map.getLayer(0);
			width = layer.getWidth();
			height = layer.getHeight();
			
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
			this.spriteShader = new Shader("sprite");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void buildMesh() {
		ModelBuilder mb = new ModelBuilder();
	    
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				if (tiles[x][y] != -1) {
					byte value = tiles[x][y];
					
					Vec2 uv = uvs[value];
					
					mb.square(x, y, x + 1f, y + 1f, uv.x / width, uv.y / height, (uv.x + 1f) / width, (uv.y + 1f) / height);
					//mb.square(x, y, x + 1f, y + 1f, 1f / width, 0f / height, 2f / width, 1f / height);
				}
			}
		}
		this.mesh = mb.toMesh();
	}
	
	public void render() {
		/*spriteShader.bind();
		tileset.image.bind();
		
		for (int x = 0; x < dimensions.x; x++) {
			for (int y = 0; y < dimensions.y; y++) {
				if (tiles[x][y] == -1)
					continue;
				
				Vec2 offset = idToOffset(tiles[x][y]);
				
				spriteShader.uniformMat4("model", new Mat4().translate(new Vec3(x, -y, 0.0f)));
				spriteShader.uniformVec2("uvOffset", new Vec2((1 / tileset.image.dimensions.x) * 16 * offset.x, (1 / tileset.image.dimensions.y) * 16 * offset.y));
				tileset.mesh.draw();
			}
		}*/
		
		
		spriteShader.bind();
		spriteShader.uniformMat4("model", new Mat4());
		this.tex.bind(0);
		this.mesh.draw();
		
	}
}
