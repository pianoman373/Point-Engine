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
import com.team.engine.rendering.Font;
import com.team.engine.vecmath.Vec2;

/**
 * Flappy bird. Fully complete with textures and everything.
 */
public class FlappyBirdDemo extends AbstractGame {
	public Bird player;
	Font font;

	//always needed for every runnable demo ever
	public static void main(String[] args) {
		Engine.start(true, false, new FlappyBirdDemo(), new String[] {"splash.png", "point-engine.png"});
	}

	@Override
	public void init() {
		Engine.scene.skyColor = vec3(0.0f, 0.5f, 1.0f);

		loadTexture("crate.png");
		loadTexture("pipe.png", true, false);
		loadTexture("bird.png", true, false);
		loadTexture("bg.png", true, false);
		
		loadAudio("breakout.wav");
		loadAudio("powerup.wav");
		
		//this actually messes up if your window isn't the right ratio, but whatever
		Engine.scene.backgroundImage = getTexture("bg.png");
		
		player = new Bird();
		Engine.scene.add(player);
		player.setPosition(vec2(-5.0f, 0.0f));
		
		//play the background music and set loop to true
		getAudio("breakout.wav").play(true, 0.7f);
		
		font = new Font("Arial.ttf", 32);
	}
	
	@Override
	public void postUpdate() {
		Engine.camera.setPosition(vec3(player.getPosition().x, 0, 0));
	}

	@Override
	public void render() {
		// 0, 0 is the very bottom left of the screen
		font.draw(0, 0, "Deaths: " + player.deaths);
	}
}

/**
 * Just a pipe that sits there and does nothing but have a collider.
 */
class Pipe extends Sprite {
	boolean top;
	
	/**
	 * @param top true if this is the pipe on top (which needs to be flipped upside down)
	 */
	public Pipe(boolean top) {
		super("pipe.png", vec2(1f, 4f), false, false);
		
		//totally needed for being identified by the Bird object
		this.tag = "pipe";
		
		this.top = top;
	}
	
	@Override
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
		
		//notice the collider is smaller than the actual sprite size in the constructor
		//this is to more closely align with the shape of the bird
		this.addCube(vec2(0, 0), vec2(0.7f, 0.6f), 0, true);
		
		body.setFixedRotation(true);
		body.setLinearDamping(0.5f);
	}

	@Override
	public void update() {
		//if dead restart at the beginning
		if (this.isDead) {
			this.setPosition(vec2(-5, 0));
			this.setVelocity(vec2(0, 0));
			
			this.isDead = false;
		}
		
		Vec2 vel = this.getVelocity();
		Vec2 pos = this.getPosition();
		
		this.setVelocity(vec2(3f, vel.y));
		
		//important thing here: we need to check when spacebar is let go to be able to flap again
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
		
		//rotate up and down based on vertical velocity
		this.setRotation(vel.y * 4);
		
		
		//places new pipes as we go along, all these magic numbers mainly came from tweaking
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
	
	@Override
	public void onContact(Fixture f, GameObject2D other) {
		if (other.tag.equals("pipe")) {
			this.isDead = true;
			deaths++;
			getAudio("powerup.wav").play(false, 1.0f);
			
			//kill all pipes we placed while moving forward since now we're gonna restart
			Engine.scene.killWithTag("pipe");
			pipesPlaced = 0;
		}
	}
	
	@Override
	public void endContact(Fixture f, GameObject2D other) {}
}
