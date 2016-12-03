package com.team.engine.demos;

import org.jbox2d.dynamics.Fixture;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;

import com.team.engine.AbstractGame;
import com.team.engine.Engine;
import com.team.engine.Input;
import com.team.engine.Scene;
import com.team.engine.gameobject.GameObject2D;
import com.team.engine.gameobject.Sprite;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

/**
 * Simple breakout game. Doesn't really work well as a game but it gets
 * the point across.
 */
public class BreakoutDemo extends AbstractGame {
	public Player player;
	public Paddle paddle;

	private int[] level1 = new int[] {
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
		4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4,
		4, 1, 4, 1, 4, 4, 4, 4, 0, 4, 4, 0, 4, 4, 4, 4, 1, 4, 1, 4,
		3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3,
		2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2
	};

	//always needed for every runnable demo ever
	public static void main(String[] args) {
		Engine.start(true, false, new BreakoutDemo(), null);
	}

	@Override
	public void init() {
		//load textures and sounds into memory at the very beginning
		loadTexture("crate.png");
		loadTexture("block.png");
		loadTexture("block_solid.png");
		loadTexture("awesomeface.png");
		loadTexture("paddle.png");
		loadTexture("background.jpg");

		loadAudio("breakout.wav");
		loadAudio("powerup.wav");

		player = new Player();
		Engine.scene.add(player);

		paddle = new Paddle();
		Engine.scene.add(paddle);
		Engine.scene.setGravity(vec2(0, 0));
		Engine.scene.backgroundImage = getTexture("background.jpg");
		paddle.setPosition(vec2(0.0f, -7.5f));

		//create all the blocks from the values stored in level1
		int i = 0;
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 20; x++) {
				if (level1[i] != 0) {
					Vec3 color = vec3(1, 1, 1);
					boolean solid = false;
					
					
					if (level1[i] == 1) {
						solid = true;
					}
					if (level1[i] == 2) {
						color = vec3(0.2f, 0.6f, 1.0f);
					}
					if (level1[i] == 3) {
						color = vec3(0.0f, 0.7f, 0.0f);
					}
					if (level1[i] == 4) {
						color = vec3(0.8f, 0.8f, 0.4f);
					}
					if (level1[i] == 5) {
						color = vec3(1.0f, 0.5f, 0.0f);
					}

					Box b = new Box(solid, color);
					Engine.scene.add(b);
					b.setPosition(vec2(-10 + 0.5f + x, 8 - 0.5f - y));
				}

				i++;
			}
		}
	}

	/**
	 * The paddle object you control.
	 */
	private class Paddle extends Sprite {
		public Paddle() {
			super("paddle.png", vec2(1.5f, 0.5f), false, false);

		}

		@Override
		public void init(Scene scene) {
			super.init(scene);
			this.addCube(vec2(), vec2(1.5f, 0.5f), 0f, 1f, false);
		}

		@Override
		public void update() {
			//get key events for movement
			Vec2 pos = this.getPosition();

			if (Input.isKeyDown(GLFW_KEY_LEFT)) {
				this.setPosition(vec2(pos.x - (Engine.deltaTime * 12), pos.y));
			}
			if (Input.isKeyDown(GLFW_KEY_RIGHT)) {
				this.setPosition(vec2(pos.x + (Engine.deltaTime * 12), pos.y));
			}
		}
	}

	/**
	 * One of the breakable (or unbreakable actually) boxes.
	 */
	private class Box extends Sprite {
		public boolean solid;
		
		public Box(boolean solid, Vec3 color) {
			super(solid ? "block_solid.png" : "block.png", vec2(0.5f, 0.5f), false, false);
			this.overlayColor = color;
			this.tag = "box";
			this.solid = solid;
		}

		@Override
		public void init(Scene scene) {
			super.init(scene);
			this.addCube(vec2(), vec2(0.5f, 0.5f), 0f, 1f, false);
		}
	}

	/**
	 * The happy smiley face dude.
	 */
	private class Player extends Sprite {

		public Player() {
			super("awesomeface.png", vec2(0.2f, 0.2f), true, true);
		}

		@Override
		public void init(Scene scene) {
			super.init(scene);
			
			this.addCube(vec2(), vec2(0.2f, 0.2f), 0f, false);
			
			body.setFixedRotation(true);
			this.setVelocity(vec2(8.0f, 8.0f));
		}

		@Override
		public void update() {
			//detect if outside of the box
			Vec2 pos = this.getPosition();
			Vec2 vel = this.getVelocity();

			if (Math.abs(pos.x) > 9.5f) {
				this.setVelocity(vec2(-vel.x, vel.y));
			}
			if (Math.abs(pos.y) > 7.5f) {
				this.setVelocity(vec2(vel.x, -vel.y));
			}
		}
		
		@Override
		public void endContact(Fixture f, GameObject2D other) {
			/* When the ball comes in contact with a block we want it to
			bounce off first, and then delete the cube it touched. 
			So we use endContact instead of onContact. Now then the ball
			hits a box it does collision first, bounces off, and as soon as it
			stops touching this functions gets called. Now we can destroy the box
			it came in contact with.
			*/
			if (other.tag.equals("box") && other instanceof Box) {
				//don't break the unbreakable blocks
				if (!((Box)other).solid) {
					Engine.scene.delete(other);
					getAudio("powerup.wav").play(false, 1.0f);
				}
			}
		}
	}
}
