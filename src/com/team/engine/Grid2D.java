package com.team.engine;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;

import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;

public class Grid2D {
	private byte[][] tiles;
	private int width;
	private int height;
	private int mapWidth;
	private int mapHeight;
	private Mesh mesh;
	public Body body;
	
	public Grid2D(byte[][] tiles, int width, int height, int mapWidth, int mapHeight) {
		this.tiles = tiles;
		this.width = width;
		this.height = height;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		Engine.loadTexture("retro-terrain.png", true);
		buildMesh();
	}
	
	public Grid2D(String tmxfile) {
		Engine.loadTexture("retro-terrain.png", true);
		TMXMapReader reader = new TMXMapReader();
		
		width = 16;
		height = 16;
		
		try {
			Map map = reader.readMap(Constants.RESOURCE_PATH + tmxfile);
			
			TileLayer layer = (TileLayer) map.getLayer(0);
			mapWidth = layer.getWidth();
			mapHeight = layer.getHeight();
			
			body = new Body();
			body.setMass(MassType.INFINITE);
			body.translate(0.5, 0.5);
			
			byte[][] layerTiles = new byte[mapWidth][mapHeight];
			
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					Tile tile = layer.getTileAt(x, y);
					
					if (tile != null) {
						layerTiles[x][(mapHeight - 1) - y] = (byte)(tile.getId() + 1);
						
						System.out.println(tile.getId());
						
						BodyFixture bf = body.addFixture(Geometry.createSquare(1));
						bf.getShape().translate(x, (mapHeight - 1) - y);
					}
					else {
						layerTiles[x][y] = 0;
					}
				}
			}
			
			this.tiles = layerTiles;
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buildMesh();
	}
	
	private void buildMesh() {
		ModelBuilder mb = new ModelBuilder();
	    
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				if (tiles[x][y] != 0) {
					byte value = tiles[x][y];
					
					Vec2 uv = computeUV(value);
					
					mb.square(x, y, x + 1f, y + 1f, uv.x / width, uv.y / height, (uv.x + 1f) / width, (uv.y + 1f) / height);
					//mb.square(x, y, x + 1f, y + 1f, 1f / width, 0f / height, 2f / width, 1f / height);
				}
			}
		}
		this.mesh = mb.toMesh();
	}
	
	private Vec2 computeUV(int index) {
		return new Vec2((index - 1) % width, (index - 1) / height);
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
		
		Shader s = Engine.getShader("sprite");
		s.bind();
		s.uniformMat4("model", new Mat4());
		Engine.getTexture("retro-terrain.png").bind();
		this.mesh.draw();
		
	}
}
