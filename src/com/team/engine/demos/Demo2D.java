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
import com.team.engine.gameobject.Grid2D;
import com.team.engine.gameobject.Sprite;
import com.team.engine.rendering.Shader;

/**
 * A little platformer demo. Loads Tiled tmx files!
 */
public class Demo2D extends AbstractGame {
	private static Grid2D grid;

	public Player player;

	//always needed for every runnable demo ever
	public static void main(String[] args) {
		Engine.start(true, false, new Demo2D());
	}

	@Override
	public void init() {
		//                   psst this is sky blue \/
		Engine.scene.skyColor = vec3(0.0f, 0.5f, 1.0f);

		loadTexture("crate.png");

		grid = new Grid2D("retro.tmx");
		Engine.scene.add(grid);
		
		player = new Player();
		Engine.scene.add(player);
	}
	
	@Override
	public void postUpdate() {
		//we go ahead and do this in postUpdate because I don't have a better update system atm
		Engine.camera.setPosition(vec3(player.body.getPosition().x, player.body.getPosition().y, 0));
	}
}

/**
 * Pretty simple implementation of a platformer.
 * 
 * The general idea behind it is to have a main square collider for your normal collision stuff
 * (aka walls, ceilings, bullets, goombas, whatever) and a second circle collider just a tiny bit
 * below the square collider. The sphere collider has an immense amount of drag so stopping and going
 * is pretty much instant. Because only the sphere touches the ground and not anything else, we can determine
 * that the player is on the ground if the sphere is touching something since the ground is all it's able to
 * touch. If we're touching a wall the only thing colliding will be the cube because it extends further along X
 * than the radius of the sphere.
 * 
 * Theoretically we could have 4 spheres all offset in each direction so then we could have wall jumping and stuff.
 * 
 * I believe this approach is called the unicycle but don't quote me there.
 */
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
		
		/*
		 * note: The sphere could technically be a sensor, but then the cube would
		 * actually come in contact with the ground. Which is normally fine but
		 * in this implementation we rely on high drag between the player and the ground.
		 * We can't just give the cube high drag either or you would be able to stick
		 * to walls. So for now, the sphere collider is solid and the player actually
		 * floats above the ground a bit (not a problem if you stretch your sprite though).
		 */
		this.feet = this.addSphere(vec2(0.0f, -0.15f), 0.4f, 1000f, 0f, false);
		
		body.setTransform(new Vector2(10.0f, 20.0f), 0);
		body.setFixedRotation(true);
		body.setLinearDamping(0.5f);
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
	}
	
	//these last 2 functions toggle onGround whenever the feet come in contact or end contact with something
	
	@Override
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
