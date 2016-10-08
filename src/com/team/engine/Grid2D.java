package com.team.engine;

import java.util.Iterator;

import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import com.team.engine.gameobject.PhysicsObject2D;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.ModelBuilder;
import com.team.engine.rendering.Shader;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;

import tiled.core.Map;
import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;

/**
 * Grid2D is meant to act as a simple 2D voxel renderer.
 * Currently it is just a support for tiled maps.
 */
public class Grid2D extends PhysicsObject2D {
	private byte[][] tiles;
	private int width;
	private int height;
	private int mapWidth;
	private int mapHeight;
	private Mesh mesh;
	private Map map;
	
	public Grid2D(String tmxfile) {
		super(false, false);
		Engine.loadTexture("retro-terrain.png", true, false);
		TMXMapReader reader = new TMXMapReader();
		
		width = 16;
		height = 16;
		
		try {
			map = reader.readMap(Settings.RESOURCE_PATH + tmxfile);
			
			TileLayer layer = (TileLayer) map.getLayer(0);
			mapWidth = layer.getWidth();
			mapHeight = layer.getHeight();
			
			byte[][] layerTiles = new byte[mapWidth][mapHeight];
			
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					Tile tile = layer.getTileAt(x, y);
					
					if (tile != null) {
						layerTiles[x][(mapHeight - 1) - y] = (byte)(tile.getId() + 1);
					}
					else {
						layerTiles[x][y] = 0;
					}
				}
			}
			
			this.tiles = layerTiles;
		} catch (Exception e) {
			e.printStackTrace();
		}
		buildMesh();
	}
	
	/**
	 * Uses ModelBuilder to make one mesh for the entire map.
	 */
	private void buildMesh() {
		ModelBuilder mb = new ModelBuilder();
	    
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				if (tiles[x][y] != 0) {
					byte value = tiles[x][y];
					
					Vec2 uv = computeUV(value);
					
					mb.square(x, y, x + 1f, y + 1f, uv.x / width, uv.y / height, (uv.x + 1f) / width, (uv.y + 1f) / height);
				}
			}
		}
		this.mesh = mb.toMesh();
	}
	
	/**
	 * Computes the uv coordinate for the given integer index;
	 */
	private Vec2 computeUV(int index) {
		return new Vec2((index - 1) % width, (index - 1) / height);
	}
	
	@Override
	public void init(Scene scene) {
		super.init(scene);
		ObjectGroup colliderLayer = (ObjectGroup) map.getLayer(2);
		BodyDef de = new BodyDef();
		de.type = BodyType.STATIC;
		
		Iterator<MapObject> iter = colliderLayer.getObjects();
		while (iter.hasNext()) {
			MapObject ob = iter.next();
			
			float rwidth = (float)ob.getWidth() / 16f;
			float rheight = (float)ob.getHeight() / 16f;
			
			float xpos = ((float)ob.getX() / 16f) + (rwidth / 2f);
			float ypos = ((float)ob.getY() / 16f) + (rheight / 2f);
			
			this.addCube(new Vec2(xpos, (mapHeight - 1) - ypos + 1), new Vec2(rwidth / 2, rheight / 2), 0.2f, false);
		}
	}

	@Override
	public void update() {}

	@Override
	public void render(Scene scene, Camera cam) {
		Shader s = Engine.getShader("sprite");
		s.bind();
		s.uniformMat4("model", new Mat4());
		Engine.getTexture("retro-terrain.png").bind();
		this.mesh.draw();
	}
}
