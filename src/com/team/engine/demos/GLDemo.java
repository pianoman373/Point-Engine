package com.team.engine.demos;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.team.engine.Cubemap;
import com.team.engine.Engine;
import com.team.engine.Mesh;
import com.team.engine.ObjLoader;
import com.team.engine.PointLight;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL, bullet physics, skyboxes, and lighting shaders.
 */
public class GLDemo extends Engine {
	private static Vec3 cubePositions[] = {
		new Vec3( 2.0f,  5.0f, -15.0f), 
		new Vec3(-1.5f, -2.2f, -2.5f),  
		new Vec3(-3.8f, -2.0f, -12.3f),  
		new Vec3( 2.4f, -0.4f, -3.5f),  
		new Vec3(-1.7f,  3.0f, -7.5f),  
		new Vec3( 1.3f, -2.0f, -2.5f),  
		new Vec3( 1.5f,  2.0f, -2.5f), 
		new Vec3( 1.5f,  0.2f, -1.5f), 
		new Vec3(-1.3f,  1.0f, -1.2f)  
	};
	
	private static PointLight lights[] = {
		new PointLight(new Vec3(-1, 0, -3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f),
		new PointLight(new Vec3(1, 0, -3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f),
		new PointLight(new Vec3(-1, 0, 3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f),
		new PointLight(new Vec3(1, 0, 3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f)
	};
	
	public static DiscreteDynamicsWorld dynamicsWorld;
	
	private static Crate[] crates = new Crate[cubePositions.length];
	
	public static Mesh cubeMesh;
	private Mesh objMesh;
	
	public static void main(String[] args) {
		new GLDemo().initialize(false);
	}

	@Override
	public void setupGame() {
		loadTexture("container2.png");
		loadTexture("container2_specular.png");
		
		loadShader("standard");
		loadShader("light");
		
		this.setFramebuffer(new Shader("shaders/hdr"));
		
		//Create the cube mesh object from the primitive.
		cubeMesh = new Mesh(Primitives.cube(1.0f));
		//load our monkey from disk
		objMesh = ObjLoader.loadFile("resources/monkey.obj");
		
		this.skybox = new Cubemap(new String[] {
				"textures/skybox/right.jpg",
				"textures/skybox/left.jpg",
				"textures/skybox/top.jpg",
				"textures/skybox/bottom.jpg",
				"textures/skybox/back.jpg",
				"textures/skybox/front.jpg"
		});
		
		setupPhysics();
	}

	@Override
	public void tick() {
		//tell bullet to tick
		dynamicsWorld.stepSimulation(Engine.instance.deltaTime, 10);
	}

	@Override
	public void render() {
		//Bind two textures in different indexes so the shader has both.
		getTexture("container2.png").bind(0);
		getTexture("container2_specular.png").bind(1);
		this.skybox.bind(2);

		
		//Bind our shader.
		Shader s = getShader("standard");
		s.bind();
		
		//Send material parameters and ambient as well.
		s.uniformFloat("ambient", 0.2f);
		
		s.uniformInt("material.diffuse", 0);
		s.uniformVec3("material.diffuseColor", new Vec3(0.5, 0.5, 0.5));
		s.uniformBool("material.diffuseTextured", true);
		
		s.uniformInt("material.specular", 1);
		s.uniformVec3("material.specularColor", new Vec3(0.5, 0.5, 0.5));
		s.uniformBool("material.specularTextured", true);
		
		s.uniformFloat("material.shininess", 64.0f);
		s.uniformInt("skybox", 2);
		
		s.uniformInt("pointLightCount", lights.length);
		for (int i = 0; i < lights.length; i++) {
			s.uniformPointLight("pointLights[" + i + "]", lights[i]);
		}
		
		//draw the ground
		s.uniformMat4("model", new Mat4().translate(new Vec3(0, -60f, 0)).scale(100f));
		cubeMesh.draw();
		
		//setup new material parameters for the monkey
		s.uniformMat4("model", new Mat4());
		s.uniformVec3("material.diffuseColor", new Vec3(0.5, 0.5, 0.5));
		s.uniformBool("material.diffuseTextured", false);
		s.uniformVec3("material.specularColor", new Vec3(1, 1, 1));
		s.uniformBool("material.specularTextured", false);
		s.uniformFloat("material.shininess", 64.0f);
		//draw the monkey
		objMesh.draw();
		
		//Now we switch over to our light shader so we can draw each light.
		Shader s2 = getShader("light");
		s2.bind();
		
		for (PointLight light : lights) {
			s2.uniformMat4("model", new Mat4().translate(light.position).scale(0.2f));
			s2.uniformVec3("lightColor", light.color);
			cubeMesh.draw();
		}
		
		//draw all the crates, they handle everything from here
		for (Crate c : crates) {
			c.render(lights);
		}
	}
	
	private static void setupPhysics() {
		BroadphaseInterface broadphase = new DbvtBroadphase();
		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		// set the gravity of our world
		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
		
		CollisionShape groundShape = new BoxShape(new Vector3f(50f, 50f, 50f));

		// setup the motion state
		DefaultMotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, -60, 0), 1.0f))); 

		RigidBody groundRigidBody = new RigidBody(0, groundMotionState, groundShape, new Vector3f(0,0,0)); 

		dynamicsWorld.addRigidBody(groundRigidBody); // add our ground to the dynamic world.. 
		
		for (int i = 0; i < cubePositions.length; i++) {
			float angle = 20.0f * i;
			crates[i] = new Crate(cubePositions[i], new Quat4f(1.0f, 0.3f, 0.5f, (float)Math.toRadians(angle)), dynamicsWorld);
			crates[i].rb.applyCentralForce(new Vector3f((float)Math.random() * 50 - 25, (float)Math.random() * 50, (float)Math.random() * 50 - 25));
		}
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		//Send our exposure uniform to the post processing shader.
		shader.uniformFloat("exposure", 2.0f);
	}

	@Override
	public void kill() {
		
	}
}

class Crate {
	private CollisionShape bounds = new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f));
	public RigidBody rb;
	
	public Crate(Vec3 pos, Quat4f rotation, DynamicsWorld dynamicsWorld) {
		// setup the motion state for the crate
		DefaultMotionState fallMotionState = new DefaultMotionState(new Transform(new Matrix4f(rotation, new Vector3f(pos.x, pos.y, pos.z), 1.0f)));

		//This we're going to give mass so it responds to gravity 
		int mass = 1;

		Vector3f fallInertia = new Vector3f(0,0,0); 
		bounds.calculateLocalInertia(mass,fallInertia); 

		rb = new RigidBody(mass,fallMotionState,bounds,fallInertia); 
		

		dynamicsWorld.addRigidBody(rb);
	}
	
	public void update() {
		
	}
	
	public void render(PointLight[] lights) {
		Engine.getTexture("container2.png").bind(0);
		Engine.getTexture("container2_specular.png").bind(1);
		Engine.instance.skybox.bind(2);
		
		Shader s = Engine.getShader("standard");
		s.bind();
		
		//Send material parameters and ambient as well.
		s.uniformFloat("ambient", 0.2f);
		
		s.uniformInt("material.diffuse", 0);
		s.uniformVec3("material.diffuseColor", new Vec3(0.5, 0.5, 0.5));
		s.uniformBool("material.diffuseTextured", true);
		
		s.uniformInt("material.specular", 1);
		s.uniformVec3("material.specularColor", new Vec3(0.5, 0.5, 0.5));
		s.uniformBool("material.specularTextured", true);
		
		s.uniformFloat("material.shininess", 64.0f);
		s.uniformInt("skybox", 2);
		
		s.uniformInt("pointLightCount", lights.length);
		for (int i = 0; i < lights.length; i++) {
			s.uniformPointLight("pointLights[" + i + "]", lights[i]);
		}
		
		Transform trans = new Transform();
		Quat4f q = new Quat4f();
		rb.getMotionState().getWorldTransform(trans);
		trans.getRotation(q);
		
		Matrix4f mat = new Matrix4f();
		trans.getMatrix(mat);
		s.uniformMat4("model", new Mat4(mat));
		GLDemo.cubeMesh.draw();
	}
}
