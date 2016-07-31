package com.team.engine.demos;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.team.engine.Engine;
import com.team.engine.Grid2D;
import com.team.engine.Mesh;
import com.team.engine.ModelBuilder;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.Texture;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL and lighting shaders.
 */
public class Demo2D extends Engine {
	private Grid2D grid;
	
	private Mesh sprite;
	
	private static RigidBody rigidBody;
	private static RigidBody rigidBody2;
	public static DiscreteDynamicsWorld dynamicsWorld;
	
	public static void main(String[] args) {
		new Demo2D().initialize(true);
	}

	@Override
	public void setupGame() {
		this.background = new Vec3(0.0f, 0.5f, 1.0f);
		
		loadShader("sprite");
		loadTexture("container.jpg");
		
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
		dynamicsWorld.stepSimulation(1 / 600.f, 10);
	}

	@Override
	public void render() {
		Transform trans = new Transform();
		rigidBody.getMotionState().getWorldTransform(trans);
		
		Shader s = getShader("sprite");
		s.bind();
		s.uniformMat4("model", new Mat4().translate(new Vec3(trans.origin.x, trans.origin.y, 1)));
		getTexture("container.jpg").bind();
		sprite.draw();
		
		rigidBody2.getMotionState().getWorldTransform(trans);
		s.uniformMat4("model", new Mat4().translate(new Vec3(trans.origin.x, trans.origin.y, 1)));
		sprite.draw();
		
		s.uniformMat4("model", new Mat4());
		sprite.draw();
		
		grid.render();
	}
	
	private static void setupPhysics() {
		//Not really sure what any of this does yet... I was mainly copying and pasting. What I do
		//know is that is sets up a good bullet simulation.
		
		BroadphaseInterface broadphase = new DbvtBroadphase();

		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		 
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher,broadphase,solver,collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0,-9.81f,0));
		
		CollisionShape boxCollisionShape = new BoxShape(new Vector3f(0.5f, 0.5f, 100f));
		CollisionShape boxCollisionShape2 = new BoxShape(new Vector3f(0.5f, 0.5f, 100f));
		
		//the little box
		Transform trans = new Transform();
		trans.origin.x = 0f;
		trans.origin.y = 4f;
		DefaultMotionState motionstate = new DefaultMotionState(trans);
		rigidBody = new RigidBody(1, motionstate, boxCollisionShape, new Vector3f(0,0,0));
		dynamicsWorld.addRigidBody(rigidBody);
		
		
		//the big box
		Transform trans2 = new Transform();
		trans2.origin.x = 0f;
		trans2.origin.y = 0f;
		DefaultMotionState motionstate2 = new DefaultMotionState(trans2);
		rigidBody2 = new RigidBody(0, motionstate2, boxCollisionShape2, new Vector3f(0, 0 ,0));
		dynamicsWorld.addRigidBody(rigidBody2);
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kill() {
		
	}
}
