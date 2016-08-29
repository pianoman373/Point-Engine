package com.team.engine.demos;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.team.engine.*;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;


/**
 * A demo showing off 3D rendering with openGL, bullet physics, skyboxes, and lighting shaders.
 */
public class GLDemo extends AbstractGame {
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

	public static Mesh cubeMesh;
	public static Mesh groundMesh;
	private Mesh objMesh;

	public static Material crateMaterial = new Material("container2.png", "container2_specular.png", null, 256.0f);
	public static Material groundMaterial = new Material("brickwall.jpg", null, "brickwall_normal.jpg", 256.0f);
	public static Material monkeyMaterial = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.3f, 0.3f, 0.3f), 64.0f);
	
	public static void main(String[] args) {
		Engine.start(false, new GLDemo());
	}

	@Override
	public void init() {
		Engine.loadTexture("container2.png");
		Engine.loadTexture("container2_specular.png");
		Engine.loadTexture("brickwall.jpg");
		Engine.loadTexture("brickwall_normal.jpg");
		
		Engine.loadShader("standard");
		
		//Create the cube mesh object from the primitive.
		cubeMesh = new Mesh(Primitives.cube(1.0f));
		groundMesh = new Mesh(Primitives.cube(16.0f));
		//load our monkey from disk
		objMesh = ObjLoader.loadFile("torus.obj");
		
		Engine.scene.skybox = new Cubemap("skybox-2");
		
		CollisionShape groundShape = new BoxShape(new Vector3f(50f, 50f, 50f));

		// setup the motion state
		DefaultMotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, -60, 0), 1.0f))); 

		RigidBody groundRigidBody = new RigidBody(0, groundMotionState, groundShape, new Vector3f(0,0,0));

		Engine.scene.dynamicsWorld.addRigidBody(groundRigidBody); // add our ground to the dynamic world
		
		for (int i = 0; i < cubePositions.length; i++) {
			float angle = 20.0f * i;
			Engine.scene.add(new Crate(cubePositions[i], new Quat4f(1.0f, 0.3f, 0.5f, (float)Math.toRadians(angle)), Engine.scene.dynamicsWorld));
		}
	}

	private static float accum;

	@Override
	public void tick() {
		accum += Engine.deltaTime;
		
		if (Input.isButtonDown(1) && accum > 0.1f) {
			FPSCamera cam = (FPSCamera)Engine.camera;
			Crate c = new Crate(cam.getPosition(), new Quat4f(1.0f, 0.3f, 0.5f, 0f), Engine.scene.dynamicsWorld);
			c.rb.applyCentralForce(new Vector3f(0.0f, 100.0f, 0.0f));
			Engine.scene.add(c);
			accum = 0;
		}
		if (Input.isButtonDown(2) && accum > 1f) {
			PointLight p = new PointLight(Engine.camera.getPosition(), new Vec3(1.0f, 1.0f, 2.0f), 0.42f, 0.2f);
			Engine.scene.add(p);

			accum = 0;
		}
	}

	@Override
	public void render() {		
		//Bind our shader.
		Shader s = Engine.getShader("standard");
		s.bind();
		
		s.uniformMaterial(groundMaterial);
		
		//draw the ground
		s.uniformMat4("model", new Mat4().translate(new Vec3(0, -60f, 0)).scale(100f));
		groundMesh.draw();
		
		//setup new material parameters for the monkey
		s.uniformMat4("model", new Mat4());
		s.uniformMaterial(monkeyMaterial);
		//draw the monkey
		objMesh.draw();
	}

	@Override
	public void postRenderUniforms(Shader shader) {}

	@Override
	public void kill() {
		
	}

	@Override
	public void renderShadow(Shader s) {
		//draw the ground
		s.uniformMat4("model", new Mat4().translate(new Vec3(0, -60f, 0)).scale(100f));
		cubeMesh.draw();
		
		s.uniformMat4("model", new Mat4());
		//draw the monkey
		objMesh.draw();
	}
}

class Crate extends GameObject {
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
	
	public void render(Scene scene, Camera cam) {
		
		
		Shader s = Engine.getShader("standard");
		s.bind();
		
		s.uniformMaterial(GLDemo.crateMaterial);
		
		Transform trans = new Transform();
		Quat4f q = new Quat4f();
		rb.getMotionState().getWorldTransform(trans);
		trans.getRotation(q);
		
		Matrix4f mat = new Matrix4f();
		trans.getMatrix(mat);
		s.uniformMat4("model", new Mat4(mat));
		GLDemo.cubeMesh.draw();
	}

	@Override
	public void renderShadow(Shader s) {
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
