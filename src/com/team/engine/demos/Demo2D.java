package com.team.engine.demos;

import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Fixture;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;

import com.team.engine.AbstractGame;
import com.team.engine.Engine;
import com.team.engine.Grid2D;
import com.team.engine.Input;
import com.team.engine.Scene;
import com.team.engine.gameobject.GameObject2D;
import com.team.engine.gameobject.Sprite;
import com.team.engine.rendering.Shader;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

/**
 * A demo utilizing sprite rendering, Grid2D's and box2D physics.
 */
public class Demo2D extends AbstractGame {
	private static Grid2D grid;

	public Player player;


	public static void main(String[] args) {
		Engine.start(true, false, new Demo2D());
	}

	@Override
	public void init() {
		Engine.scene.skyColor = vec3(0.0f, 0.5f, 1.0f);

		Engine.loadShader("sprite");
		Engine.loadTexture("crate.png");

		grid = new Grid2D("retro.tmx");
		Engine.scene.add(grid);
		
		player = new Player();
		Engine.scene.add(player);
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
	public void kill() {}

	@Override
	public void renderShadow(Shader s) {}
}

class Player extends Sprite {
	public boolean onGround;
	private Fixture feet;
	
	public Player() {
		super(null, vec2(0.5f, 0.5f), true, true);
	}

	@Override
	public void init(Scene scene) {
		super.init(scene);
		this.addCube(vec2(0, 0), vec2(0.5f, 0.5f), 0, false);
		this.feet = this.addSphere(vec2(0.0f, -0.15f), 0.4f, 1000);
		
		body.setTransform(new Vector2(10.0f, 20.0f), 0);
		body.setFixedRotation(true);	
	}

	@Override
	public void update() {
		Vector2 vel = body.getLinearVelocity();
		Vector2 pos = body.getPosition();
		
		// cap max velocity on x		
		if(Math.abs(vel.x) > 8) {			
			vel.x = Math.signum(vel.x) * 8;
			body.setLinearVelocity(new Vector2(vel.x, vel.y));
		}
		
		if (Input.isKeyDown(GLFW_KEY_LEFT) && vel.x > -4) {
			body.applyLinearImpulse(new Vector2(-600 * Engine.deltaTime, 0), new Vector2(pos.x, pos.y));
			body.setAwake(true);
		}
		
		if (Input.isKeyDown(GLFW_KEY_UP)) {
			if (this.onGround) {
				body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 10));
				body.setAwake(true);
			}
		}
		
		if (Input.isKeyDown(GLFW_KEY_RIGHT) && vel.x < 4) {
			body.applyLinearImpulse(new Vector2(600 * Engine.deltaTime, 0), new Vector2(pos.x, pos.y));
			body.setAwake(true);
		}
		
		Engine.camera.setPosition(vec3(body.getPosition().x, body.getPosition().y, 0));
	}
	
	public void onContact(Fixture f, GameObject2D other) {
		if (f == feet) {
			print(other);	
			this.onGround = true;
		}
	}
	
	@Override
	public void endContact(Fixture f, GameObject2D other) {
		if (f == feet) {
			print(other);
			this.onGround = false;
		}
	}
}
