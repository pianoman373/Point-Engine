package com.team.engine;

import java.util.ArrayList;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

import javax.vecmath.Vector3f;

/**
 * Scene is meant to make life easier by taking care of all the object handling, lights, and rendering on it's own.
 * Currently this is only designed for 3D. You MUST call setupPhysics if you want there to be physics. If you have a game object
 * that uses bullet physics and setupPhysics was not called you will likely crash.
 */
public class Scene {
	public ArrayList<PointLight> lights = new ArrayList<>();
	public ArrayList<GameObject> objects = new ArrayList<>();
	public DirectionalLight sun;
	public DiscreteDynamicsWorld dynamicsWorld;
	public float ambient = 0.5f;
	/** The skybox that will be automatically rendered in the background. 
	 * Can be null to use background color instead */
	public Cubemap skybox = null;
	/** Background color if skybox is null */
	public Vec3 skyColor = new Vec3(0.0f, 0.0f, 0.0f);
	
	public Scene() {
		sun = new DirectionalLight(new Vec3(-1.0f, -0.5f, 0.2f), new Vec3(2.5f, 2.3f, 2.0f), Graphics.ENABLE_SHADOWS, 30, Graphics.SHADOW_RESOLUTION);
	}

	/**
	 * Initializes the dynamicsWorld for this scene
	 */
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
			Engine.cubeMesh.draw();
		}
	}
	
	public void update() {
		dynamicsWorld.stepSimulation(Engine.deltaTime, 10);
	}
	
	public void renderShadow(Shader s) {
		for (GameObject obj: objects) {
			obj.renderShadow(s);
		}
	}

	public void add(GameObject o) {
		o.init(this);
		objects.add(o);
	}

	public void add(PointLight light) {
		lights.add(light);
	}
}
