package com.team.engine.demos;

import com.team.engine.AbstractGame;
import com.team.engine.Engine;
import com.team.engine.Input;
import com.team.engine.MazeGen;
import com.team.engine.Scene;
import com.team.engine.gameobject.Sprite;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;
import static org.lwjgl.glfw.GLFW.*;

public class AiDemo extends AbstractGame {
	public static void main(String[] args) {
		Engine.start(true, false, new AiDemo());
	}
	
	private Player player;
	
	private static final int LEVEL_WIDTH = 20;
	private static final int LEVEL_HEIGHT = 16;
	
	private char[][] level1 = new char[LEVEL_WIDTH][LEVEL_HEIGHT];

	@Override
	public void init() {
		Engine.loadTexture("block.png");
		Engine.loadTexture("block_solid.png");
		Engine.loadTexture("awesomeface.png");
		Engine.loadTexture("crate.png");
		
		
		
		Vec2 startingPos = new Vec2();
		level1 = MazeGen.generate(LEVEL_WIDTH, LEVEL_HEIGHT);
		
		for (int x = 0; x < 20; x++) {
			for (int y = 0; y < 16; y++) {
				if (level1[x][y] == '1') {
					Box box = new Box(false, new Vec3(1, 1, 1));
					Engine.scene.add(box);
					box.setPosition(new Vec2(-9.5f + x, -7.5f + y));
				}
				if (level1[x][y] == 'E') {
					Box box = new Box(true, new Vec3(1, 1, 0));
					Engine.scene.add(box);
					box.setPosition(new Vec2(-9.5f + x, -7.5f + y));
				}
				if (level1[x][y] == 'S') {
					startingPos = new Vec2(x, y);
				}
			}
		}
		
		player = new Player(startingPos);
		Engine.scene.add(player);
		
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}
	
	private class Box extends Sprite {
		public boolean solid;
		
		public Box(boolean solid, Vec3 color) {
			super(solid ? "block_solid.png" : "block.png", new Vec2(0.5f, 0.5f), false, false);
			this.overlayColor = color;
			this.tag = "box";
			this.solid = solid;
		}

		public void init(Scene scene) {
			super.init(scene);
			this.addCube(new Vec2(), new Vec2(0.5f, 0.5f), 0f, 1f, false);
		}
	}
	
	private class Player extends Sprite {
		private float acc = 0;
		private Vec2 pos;

		public Player(Vec2 pos) {
			super("awesomeface.png", new Vec2(0.5f, 0.5f), false, false);
			this.pos = pos;

		}

		public void init(Scene scene) {
			super.init(scene);

			this.setPosition(new Vec2(-9.5f + pos.x, -7.5f + pos.y));
			this.addSphere(new Vec2(), 0.5f, 0f);
		}

		public void update() {
			Vec2 realPos = this.getPosition();
			
			acc += Engine.deltaTime;
			
			if (acc > 0.1) {
				acc = 0;
				
				if (Input.isKeyDown(GLFW_KEY_RIGHT)) {
					if (pos.x + 1 < LEVEL_WIDTH) {
						if (level1[(int)pos.x + 1][(int)pos.y] != '1') {
							realPos.x += 1;
							pos.x +=1;
						}
					}
				}
				if (Input.isKeyDown(GLFW_KEY_LEFT)) {
					if (pos.x - 1 >= 0) {
						if (level1[(int)pos.x - 1][(int)pos.y] != '1') {
							realPos.x -=1;
							pos.x -= 1;
						}
					}
				}
				if (Input.isKeyDown(GLFW_KEY_UP)) {
					if (pos.y + 1 < LEVEL_HEIGHT) {
						if (level1[(int)pos.x][(int)pos.y + 1] != '1') {
							realPos.y +=1;
							pos.y += 1;
						}
					}
				}
				if (Input.isKeyDown(GLFW_KEY_DOWN)) {
					if (pos.y - 1 >= 0) {
						if (level1[(int)pos.x][(int)pos.y -1] != '1') {
							realPos.y -=1;
							pos.y -= 1;
						}
					}
				}
				
				this.setPosition(realPos);
			}
		}
	}
}
