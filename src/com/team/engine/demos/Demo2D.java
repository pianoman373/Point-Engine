package com.team.engine.demos;

import javax.vecmath.Vector3f;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.lwjgl.input.Keyboard;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.team.engine.Engine;
import com.team.engine.Grid2D;
import com.team.engine.Mesh;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.demos.Dyn4jDemo.GameObject;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

/**
 * A demo showing off 3D rendering with openGL and lighting shaders.
 */
public class Demo2D extends Engine {
	private Grid2D grid;

	private Mesh sprite;
	private static Body cube;
	private static Body cube2;
	private static World world;


	public static void main(String[] args) {
		new Demo2D().initialize(true);
	}

	@Override
	public void setupGame() {
		this.background = new Vec3(0.0f, 0.5f, 1.0f);

		loadShader("sprite");
		loadTexture("crate.png");

		byte[][] map = new byte[][] {
				{-1,  6,  3,  3,  0},
				{-1,  7,  4,  4,  1},
				{-1,  7,  4,  4,  1},
				{-1,  7,  4,  4,  1},
				{-1,  8,  5,  5,  2}
		};

		Vec2[] uvmap = new Vec2[] {
			new Vec2(0, 0),
			new Vec2(1, 0),
			new Vec2(2, 0),
			new Vec2(0, 1),
			new Vec2(1, 1),
			new Vec2(2, 1),
			new Vec2(0, 2),
			new Vec2(1, 2),
			new Vec2(2, 2)
		};

		sprite = new Mesh(Primitives.sprite(new Vec2(0, 0), new Vec2(1, 1)));

		grid = new Grid2D(map, uvmap, 16, 16, 5, 5);

		setupPhysics();
	}
	
	@Override
	public void tick() {
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			cube.applyForce(new Vector2(-1000 * Engine.instance.deltaTime, 0));
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			cube.setLinearVelocity(new Vector2(cube.getLinearVelocity().x, 5));
		}
		//cube.setLinearVelocity(new Vector2(1, cube.getLinearVelocity().y));
		
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			cube.applyForce(new Vector2(1000 * Engine.instance.deltaTime, 0));
			
		}
		
		world.update(Engine.instance.deltaTime);
	}

	@Override
	public void render() {

		Shader s = getShader("sprite");
		s.bind();
		s.uniformMat4("model", new Mat4().translate(new Vec3(cube.getTransform().getTranslationX(), cube.getTransform().getTranslationY(), 1)).rotate(new Vec4(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(cube.getTransform().getRotation()))));
		getTexture("crate.png").bind();
		sprite.draw();
		
		s.uniformMat4("model", new Mat4().translate(new Vec3(cube.getTransform().getTranslationX(), cube.getTransform().getTranslationY(), 1)).rotate(new Vec4(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(cube.getTransform().getRotation()))).translate(new Vec3(1, 1, 0)));
		sprite.draw();
		
		s.uniformMat4("model", new Mat4().translate(new Vec3(cube.getTransform().getTranslationX(), cube.getTransform().getTranslationY(), 1)).rotate(new Vec4(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(cube.getTransform().getRotation()))).translate(new Vec3(1, 0, 0)));
		sprite.draw();
		
		s.uniformMat4("model", new Mat4().translate(new Vec3(cube2.getTransform().getTranslationX(), cube2.getTransform().getTranslationY(), 1)).rotate(new Vec4(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(cube2.getTransform().getRotation()))));
		sprite.draw();

		grid.render();
	}

	private static void setupPhysics() {
		world = new World();
		world.setGravity(new Vector2(0.0, -9.81));

		// create the floor
		Body floor = new Body();
		floor.addFixture(Geometry.createRectangle(5, 4));
		floor.setMass(MassType.INFINITE);
		// move the floor down a bit
		floor.translate(2.5, 3.0);
		world.addBody(floor);


		// try a rectangle
		cube = new Body();
		cube.addFixture(Geometry.createSquare(1));
		BodyFixture bf = cube.addFixture(Geometry.createSquare(1));
		bf.getShape().translate(1, 1);
		BodyFixture bf2 = cube.addFixture(Geometry.createSquare(1));
		bf2.getShape().translate(1, 0);
		cube.setMass(MassType.NORMAL);
		cube.translate(2.5, 10.0);
		cube.getLinearVelocity().set(0.0, 0.0);
		world.addBody(cube);
		
		// try a rectangle
		cube2 = new Body();
		cube2.addFixture(Geometry.createSquare(1));
		cube2.setMass(MassType.NORMAL);
		cube2.translate(3, 12.0);
		cube2.getLinearVelocity().set(0.0, 0.0);
		//cube2.rotate(Math.toRadians(45));
		world.addBody(cube2);
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void kill() {

	}
}
