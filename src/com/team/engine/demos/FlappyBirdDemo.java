package com.team.engine.demos;

import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Fixture;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;

import com.team.engine.AbstractGame;
import com.team.engine.Engine;
import com.team.engine.Input;
import com.team.engine.Scene;
import com.team.engine.gameobject.GameObject2D;
import com.team.engine.gameobject.Sprite;
import com.team.engine.rendering.FontRenderer;
import com.team.engine.rendering.Shader;

/**
 * A demo utilizing sprite rendering, Grid2D's and box2D physics.
 */
public class FlappyBirdDemo extends AbstractGame {
	public Bird player;

	public static void main(String[] args) {
		Engine.start(true, false, new FlappyBirdDemo());
	}

	@Override
	public void init() {
		Engine.scene.skyColor = vec3(0.0f, 0.5f, 1.0f);

		loadShader("sprite");
		loadTexture("crate.png");
		loadTexture("pipe.png", true, false);
		loadTexture("bird.png", true, false);
		loadTexture("bg.png", true, false);
		
		loadAudio("breakout.wav");
		loadAudio("powerup.wav");
		
		Engine.scene.backgroundImage = getTexture("bg.png");
		
		player = new Bird();
		Engine.scene.add(player);
		player.setPosition(vec2(-5.0f, 0.0f));
		
		getAudio("breakout.wav").play(true, 0.7f);
	}
	
	@Override
	public void update() {
	}

	@Override
	public void render() {
		//grid.render();
		FontRenderer.draw(-1f + 0.05f, 1f - 0.1f, 2, "Deaths: " + this.player.deaths);
		
	}

	@Override
	public void postRenderUniforms(Shader shader) {}

	@Override
	public void kill() {
		
	}

	@Override
	public void renderShadow(Shader s) {}
}

class Pipe extends Sprite {
	boolean top;
	
	public Pipe(boolean top) {
		super("pipe.png", vec2(1f, 4f), false, false);
		this.tag = "pipe";
		this.top = top;
	}
	
	public void init(Scene scene) {
		super.init(scene);
		this.addCube(vec2(0, 0), vec2(1f, 4f), 0, true);
		if (top) {
			this.setRotation(180);
		}
	}
}

class Bird extends Sprite {
	private boolean readyToFlap = true;
	private boolean isDead = false;
	private int pipesPlaced = 0;
	
	public int deaths = 0;
	
	public Bird() {
		super("bird.png", vec2(1f, 1f), true, true);
	}

	@Override
	public void init(Scene scene) {
		super.init(scene);
		this.addCube(vec2(0, 0), vec2(0.5f, 0.5f), 0, true);
		
		body.setFixedRotation(true);
		body.setLinearDamping(0.5f);
	}

	@Override
	public void update() {
		if (this.isDead) {
			this.setPosition(vec2(-5, 0));
			this.setVelocity(vec2(0, 0));
			
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
		
		Engine.camera.setPosition(vec3(body.getPosition().x, 0, 0));
		
		
		if (pos.x > pipesPlaced * 5) {
			float offset = (((float)Math.random() * 6.0f) - 3.0f);
			Pipe pipe = new Pipe(false);
			Engine.scene.add(pipe);
			pipe.setPosition(vec2(pos.x + 15, -7 + offset));
		
			Pipe pipe2 = new Pipe(true);
			Engine.scene.add(pipe2);
			pipe2.setPosition(vec2(pos.x + 15, 7 + offset));
			pipesPlaced++;
		}
	}
	
	public void onContact(Fixture f, GameObject2D other) {
		if (other.tag.equals("pipe")) {
			this.isDead = true;
			deaths++;
			getAudio("powerup.wav").play(false, 1.0f);
			Engine.scene.killWithTag("pipe");
			pipesPlaced = 0;
		}
	}
	
	@Override
	public void endContact(Fixture f, GameObject2D other) {
		
	}
}
