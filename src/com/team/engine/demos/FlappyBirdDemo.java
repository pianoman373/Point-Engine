package com.team.engine.demos;

import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Fixture;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

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
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

/**
 * A demo utilizing sprite rendering, Grid2D's and box2D physics.
 */
public class FlappyBirdDemo extends AbstractGame {
	private Globals globals;

	public Bird player;

	public static void main(String[] args) {
		Engine.start(true, false, new FlappyBirdDemo());
	}

	@Override
	public void init() {
		Engine.scene.skyColor = vec3(0.0f, 0.5f, 1.0f);

		Engine.loadShader("sprite");
		Engine.loadTexture("crate.png");
		Engine.loadTexture("awesomeface.png");
		
		Engine.loadAudio("breakout.wav");
		Engine.loadAudio("powerup.wav");
		
		player = new Bird();
		Engine.scene.add(player);
		player.setPosition(vec2(-5.0f, 0.0f));
		
		Engine.getAudio("breakout.wav").play(true, 0.7f);
		
		globals = JsePlatform.standardGlobals();
		LuaValue library = LuaValue.tableOf();
		globals.set("point", library);
		LuaValue chunk = globals.loadfile("resources/scripts/test.lua");
		chunk.call();
		
		//globals.get("point").get("init").call();
	}
	
	@Override
	public void tick() {
		//globals.get("point").get("tick").call();
	}

	@Override
	public void render() {
		//grid.render();
		FontRenderer.draw(-1f + 0.05f, 1f - 0.1f, 1, "Deaths: " + this.player.deaths);
		
		//globals.get("point").get("render").call();
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
	public Pipe() {
		super(null, vec2(1f, 4f), false, false);
		this.tag = "pipe";
	}
	
	public void init(Scene scene) {
		super.init(scene);
		this.addCube(vec2(0, 0), vec2(1f, 4f), 0, true);
	}
}

class Bird extends Sprite {
	private boolean readyToFlap = true;
	private boolean isDead = false;
	private int pipesPlaced = 0;
	
	public int deaths = 0;
	
	public Bird() {
		super("awesomeface.png", vec2(0.5f, 0.5f), true, true);
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
			Pipe pipe = new Pipe();
			Engine.scene.add(pipe);
			pipe.setPosition(vec2(pos.x + 15, -7 + offset));
		
			Pipe pipe2 = new Pipe();
			Engine.scene.add(pipe2);
			pipe2.setPosition(vec2(pos.x + 15, 7 + offset));
			pipesPlaced++;
		}
	}
	
	public void onContact(Fixture f, GameObject2D other) {
		if (other.tag.equals("pipe")) {
			this.isDead = true;
			deaths++;
			Engine.getAudio("powerup.wav").play(false, 1.0f);
			Engine.scene.killWithTag("pipe");
			pipesPlaced = 0;
		}
	}
	
	@Override
	public void endContact(Fixture f, GameObject2D other) {
		
	}
}
