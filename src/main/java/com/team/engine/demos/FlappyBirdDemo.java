package com.team.engine.demos;

import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Fixture;

import static org.lwjgl.glfw.GLFW.*;

import com.team.engine.AbstractGame;
import com.team.engine.Audio;
import com.team.engine.Engine;
import com.team.engine.GameObject2D;
import com.team.engine.Input;
import com.team.engine.Scene;
import com.team.engine.Shader;
import com.team.engine.Sprite;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

/**
 * A demo utilizing sprite rendering, Grid2D's and box2D physics.
 */
public class FlappyBirdDemo extends AbstractGame {
	private Audio audio;

	public Bird player;


	public static void main(String[] args) {
		Engine.start(true, new FlappyBirdDemo());
	}

	@Override
	public void init() {
		Engine.scene.skyColor = new Vec3(0.0f, 0.5f, 1.0f);

		Engine.loadShader("sprite");
		Engine.loadTexture("crate.png");

		//grid = new Grid2D("retro.tmx");
		//1Engine.scene.add(grid);
		
		player = new Bird();
		Engine.scene.add(player);
		player.setPosition(new Vec2(-5.0f, 0.0f));
		
		for (int i = 0; i < 20; i++) {
			float offset = (((float)Math.random() * 6.0f) - 3.0f);
			Pipe pipe = new Pipe();
			Engine.scene.add(pipe);
			pipe.setPosition(new Vec2(i * 5, -7 + offset));
			
			Pipe pipe2 = new Pipe();
			Engine.scene.add(pipe2);
			pipe2.setPosition(new Vec2(i * 5, 7 + offset));
		}
		
		audio = new Audio();
		audio.execute();
	}
	
	@Override
	public void tick() {}

	@Override
	public void render() {
		//grid.render();
	}

	@Override
	public void postRenderUniforms(Shader shader) {}

	@Override
	public void kill() {
		audio.killALData();
	}

	@Override
	public void renderShadow(Shader s) {}
}

class Pipe extends Sprite {
	public Pipe() {
		super(null, new Vec2(1f, 4f), false, false);
		this.tag = "pipe";
	}
	
	public void init(Scene scene) {
		super.init(scene);
		this.addCube(new Vec2(0, 0), new Vec2(1f, 4f), 0, true);
	}
}

class Bird extends Sprite {
	private boolean readyToFlap = true;
	private boolean isDead = false;
	
	public Bird() {
		super(null, new Vec2(0.5f, 0.5f), true, true);
	}

	@Override
	public void init(Scene scene) {
		super.init(scene);
		this.addCube(new Vec2(0, 0), new Vec2(0.5f, 0.5f), 0, true);
		
		body.setFixedRotation(true);
		body.setLinearDamping(0.5f);
	}

	@Override
	public void update() {
		if (this.isDead) {
			this.setPosition(new Vec2(-5, 0));
			this.setVelocity(new Vec2(0, 0));
			
			this.isDead = false;
		}
		
		Vector2 vel = body.getLinearVelocity();
		Vector2 pos = body.getPosition();
		
		body.setLinearVelocity(new Vector2(3f, vel.y));
		
		if (Input.isKeyDown(GLFW_KEY_SPACE)) {
			if (readyToFlap) {
				body.setLinearVelocity(new Vector2(vel.x, 7.0f));
				body.setAwake(true);
				readyToFlap = false;
			}
		}
		else {
			readyToFlap = true;
		}
		
		body.setTransform(pos, vel.y * (3.14f / 180) * 4);
		
		Engine.camera.setPosition(new Vec3(body.getPosition().x, 0, 0));
	}
	
	public void onContact(Fixture f, GameObject2D other) {
		if (other.tag.equals("pipe")) {
			System.out.println("pipe collision!!!!!");
			this.isDead = true;
		}
	}
	
	@Override
	public void endContact(Fixture f, GameObject2D other) {
		
	}
}
