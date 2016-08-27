package com.team.engine;

import java.io.InputStream;
import java.util.Iterator;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;

import tiled.core.Map;
import tiled.core.MapObject;
import tiled.core.ObjectGroup;
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
	
	public Grid2D(String tmxfile, World world) {
		Engine.loadTexture("retro-terrain.png", true);
		TMXMapReader reader = new TMXMapReader();
		
		width = 16;
		height = 16;
		
		try {
			Map map = reader.readMap(Constants.RESOURCE_PATH + tmxfile);
			
			TileLayer layer = (TileLayer) map.getLayer(0);
			
			mapWidth = layer.getWidth();
			mapHeight = layer.getHeight();
			
			ObjectGroup colliderLayer = (ObjectGroup) map.getLayer(2);
			
			Iterator<MapObject> iter = colliderLayer.getObjects();
			while (iter.hasNext()) {
				MapObject ob = iter.next();
				
				float rwidth = (float)ob.getWidth() / 16f;
				float rheight = (float)ob.getHeight() / 16f;
				
				//BodyFixture bf = body.addFixture(Geometry.createRectangle(rwidth, rheight));
				
				float xpos = ((float)ob.getX() / 16f) + (rwidth / 2f);
				float ypos = ((float)ob.getY() / 16f) + (rheight / 2f);
				
				System.out.println(rwidth / 2 + ", " + rheight / 2);
				
				//bf.getShape().translate(xpos - 0.5f, (mapHeight - 1) - ypos + 0.5f);
				
				BodyDef def = new BodyDef();
				def.type = BodyType.STATIC;
				Body box = world.createBody(def);
				
				PolygonShape poly = new PolygonShape();
				poly.setAsBox(rwidth / 2, rheight / 2);
				box.createFixture(poly, 1);
				box.setTransform(new Vector2(xpos, (mapHeight - 1) - ypos + 1), 0);
			}
			
			
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
	
	private Vec2 computeUV(int index) {
		return new Vec2((index - 1) % width, (index - 1) / height);
	}
	
	public void render() {
		Shader s = Engine.getShader("sprite");
		s.bind();
		s.uniformMat4("model", new Mat4());
		Engine.getTexture("retro-terrain.png").bind();
		this.mesh.draw();
		
	}
}
