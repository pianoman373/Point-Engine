package com.team.engine.demos;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import static org.lwjgl.glfw.GLFW.*;

import com.team.engine.AbstractGame;
import com.team.engine.Camera;
import com.team.engine.Engine;
import com.team.engine.GameObject;
import com.team.engine.Grid2D;
import com.team.engine.Input;
import com.team.engine.Mesh;
import com.team.engine.PhysicsObject2D;
import com.team.engine.Primitives;
import com.team.engine.Scene;
import com.team.engine.Shader;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

/**
 * A demo utilizing sprite rendering, Grid2D's and dyn4j physics.
 */
public class Demo2D extends AbstractGame implements ContactListener {
	private static Grid2D grid;

	public static Mesh sprite;
	public Player player;


	public static void main(String[] args) {
		Engine.start(true, new Demo2D());
	}

	@Override
	public void init() {
		Engine.scene.skyColor = new Vec3(0.0f, 0.5f, 1.0f);

		Engine.loadShader("sprite");
		Engine.loadTexture("crate.png");

		sprite = new Mesh(Primitives.sprite(new Vec2(0, 0), new Vec2(1, 1)));

		grid = new Grid2D("retro.tmx", Engine.scene.world2D);
		
		player = new Player();
		Engine.scene.add(player);
	}
	
	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		grid.render();
	}

	@Override
	public void postRenderUniforms(Shader shader) {

	}

	@Override
	public void kill() {

	}

	@Override
	public void renderShadow(Shader s) {
		
	}
	
	@Override
	public void beginContact(Contact contact) {
		
		if (contact.getFixtureA().getUserData() == "feet" || contact.getFixtureB().getUserData() == "feet") {
			System.out.println("on ground");
			player.onGround = true;
		}
	}

	@Override
	public void endContact(Contact contact) {
		if (contact.getFixtureA().getUserData() == "feet" || contact.getFixtureB().getUserData() == "feet") {
			System.out.println("off ground");
			player.onGround = false;
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
}

class Player extends PhysicsObject2D {
	public boolean onGround;
	
	private Fixture feet;
	
	public Player() {
		this.isDynamic = true;
	}

	@Override
	public void init(Scene scene) {
		super.init(scene);
		PolygonShape poly = new PolygonShape();		
		poly.setAsBox(0.5f, 0.5f);
		body.createFixture(poly, 1).setFriction(0);
		
		
		CircleShape shape2 = new CircleShape();
		shape2.m_p.set(new Vector2(0, -0.15f));
		shape2.setRadius(0.4f);
		feet = body.createFixture(shape2, 1);
		feet.setFriction(1000);
		feet.setSensor(false);
		
				
		
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
		
		Engine.camera.setPosition(new Vec3(body.getPosition().x, body.getPosition().y, 0));
	}

	@Override
	public void render(Scene scene, Camera cam) {
		Shader s = Engine.getShader("sprite");
		s.bind();
		s.uniformMat4("model", new Mat4().translate(new Vec3(body.getPosition().x, body.getPosition().y, 1)).rotate(new Vec4(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(body.getAngle()))));
		Engine.getTexture("crate.png").bind();
		Demo2D.sprite.draw();
	}
	
	public void onContact(Fixture f) {
		if (f == feet) {
			System.out.println("on ground");	
			this.onGround = true;
		}
	}
	
	@Override
	public void endContact(Fixture f) {
		if (f == feet) {
			System.out.println("off ground");
			this.onGround = false;
		}
	}
}
