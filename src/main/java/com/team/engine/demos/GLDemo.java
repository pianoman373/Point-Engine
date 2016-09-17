package com.team.engine.demos;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.team.engine.*;
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

	public static Material crateMaterial = new Material("container2.png", "container2_specular.png", null, 0.1f);
	public static Material groundMaterial = new Material("brickwall.jpg", 0.8f, "brickwall_normal.jpg", 0.0f);
	public static Material monkeyMaterial = new Material(new Vec3(0.8f, 0.8f, 0.8f), 0.4f, 1.0f);
	
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
		Engine.loadShader("pbr");
		
		//Create the cube mesh object from the primitive.
		cubeMesh = Mesh.raw(Primitives.cube(1.0f), false);
		groundMesh = Mesh.raw(Primitives.cube(16.0f), true);
		//load our monkey from disk
		objMesh = ObjLoader.loadFile("capsule.obj");
		
		Engine.scene.skybox = new Cubemap("skybox-2");
		
		for (int i = 0; i < cubePositions.length; i++) {
			float angle = 20.0f * i;
			Engine.scene.add(new MeshObject(cubePositions[i], new Quat4f(1.0f, 0.3f, 0.5f, (float)Math.toRadians(angle)), new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)), 1f, cubeMesh, 1f, crateMaterial));
		}
		Engine.scene.add(new MeshObject(new Vec3(), new Quat4f(), new CapsuleShape(1, 2), 0f, objMesh,1f,  monkeyMaterial));
		Engine.scene.add(new MeshObject(new Vec3(0, -60f, 0), new Quat4f(), new BoxShape(new Vector3f(50f, 50f, 50f)), 0f, groundMesh, 100f,  groundMaterial));
	}

	private static float accum;

	@Override
	public void tick() {
		accum += Engine.deltaTime;
		
		if (Input.isButtonDown(1) && accum > 0.1f) {
			FPSCamera cam = (FPSCamera)Engine.camera;
			MeshObject c = new MeshObject(cam.getPosition(), new Quat4f(1.0f, 0.3f, 0.5f, 0f), new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)), 1f, cubeMesh, 1f, crateMaterial);
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
		FontRenderer.draw(0, 0, 10, "Hello World!");
	}

	@Override
	public void postRenderUniforms(Shader shader) {}

	@Override
	public void kill() {
		
	}

	@Override
	public void renderShadow(Shader s) {
		
	}
}
