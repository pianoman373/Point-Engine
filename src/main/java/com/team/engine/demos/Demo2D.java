package com.team.engine.demos;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import static org.lwjgl.glfw.GLFW.*;

import com.team.engine.AbstractGame;
import com.team.engine.Engine;
import com.team.engine.Grid2D;
import com.team.engine.Input;
import com.team.engine.Mesh;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

/**
 * A demo utilizing sprite rendering, Grid2D's and dyn4j physics.
 */
public class Demo2D extends AbstractGame {
	private static Grid2D grid;

	private Mesh sprite;
	private static Body cube;
	private static World world;


	public static void main(String[] args) {
		Engine.start(true, new Demo2D());
	}

	@Override
	public void setupGame() {
		Engine.background = new Vec3(0.0f, 0.5f, 1.0f);

		Engine.loadShader("sprite");
		Engine.loadTexture("crate.png");

		sprite = new Mesh(Primitives.sprite(new Vec2(0, 0), new Vec2(1, 1)));
		
		world = new World(new Vector2(0, -9.8f));		

		//grid = new Grid2D(map, 16, 16, 5, 5);
		grid = new Grid2D("retro.tmx", world);

		setupPhysics();
	}
	
	@Override
	public void tick() {
		Vector2 vel = cube.getLinearVelocity();
		Vector2 pos = cube.getPosition();
		
		
		// cap max velocity on x		
		if(Math.abs(vel.x) > 4) {			
			vel.x = Math.signum(vel.x) * 4;
			cube.setLinearVelocity(new Vector2(vel.x, vel.y));
		}
		
		if (Input.isKeyDown(GLFW_KEY_LEFT) && vel.x > -4) {
			cube.applyLinearImpulse(new Vector2(-600 * Engine.deltaTime, 0), new Vector2(pos.x, pos.y));
			cube.setAwake(true);
		}
		
		if (Input.isKeyDown(GLFW_KEY_UP)) {
			cube.setLinearVelocity(new Vector2(cube.getLinearVelocity().x, 5));
			cube.setAwake(true);
		}
		//cube.setLinearVelocity(new Vector2(1, cube.getLinearVelocity().y));
		
		if (Input.isKeyDown(GLFW_KEY_RIGHT) && vel.x < 4) {
			cube.applyLinearImpulse(new Vector2(600 * Engine.deltaTime, 0), new Vector2(pos.x, pos.y));
			cube.setAwake(true);
			
		}
		world.step(Engine.deltaTime, 4, 4);
	}

	@Override
	public void render() {

		Shader s = Engine.getShader("sprite");
		s.bind();
		s.uniformMat4("model", new Mat4().translate(new Vec3(cube.getPosition().x, cube.getPosition().y, 1)).rotate(new Vec4(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(cube.getAngle()))));
		Engine.getTexture("crate.png").bind();
		sprite.draw();

		grid.render();
	}

	private static void setupPhysics() {
		// try a rectangle
		
		BodyDef def = new BodyDef();
		def.type = BodyType.DYNAMIC;
		cube = world.createBody(def);
		
		PolygonShape poly = new PolygonShape();		
		poly.setAsBox(0.5f, 0.5f);
		cube.createFixture(poly, 1);
		cube.setTransform(new Vector2(0.0f, 10.0f), 0);
		cube.setFixedRotation(true);		
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void kill() {

	}

	@Override
	public void renderShadow(Shader s) {
		// TODO Auto-generated method stub
		
	}
}
