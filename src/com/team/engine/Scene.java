package com.team.engine;

import java.util.ArrayList;
import java.util.Iterator;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.team.engine.vecmath.Mat4;

import javax.vecmath.Vector3f;

public class Scene {
	public ArrayList<PointLight> lights = new ArrayList<>();
	public ArrayList<GameObject> objects = new ArrayList<>();

	public static DiscreteDynamicsWorld dynamicsWorld;

	public void setupPhysics() {
		BroadphaseInterface broadphase = new DbvtBroadphase();
		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		// set the gravity of our world
		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
	}
	
	public void render(Camera cam) {
		for (GameObject obj: objects) {
			obj.render(this, cam);
		}
		
		Shader s = Engine.getShader("light");
		s.bind();
		
		for (PointLight light : lights) {
			s.uniformMat4("model", new Mat4().translate(light.position).scale(0.2f));
			s.uniformVec3("lightColor", light.color);
			Engine.instance.cubeMesh.draw();
		}
	}

	public void add(GameObject o) {
		objects.add(o);
	}

	public void add(PointLight light) {
		lights.add(light);
	}
}
