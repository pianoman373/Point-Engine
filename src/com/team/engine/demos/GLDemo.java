package com.team.engine.demos;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;


import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.team.engine.*;
import com.team.engine.gameobject.MeshObject;
import com.team.engine.rendering.Cubemap;
import com.team.engine.rendering.Material;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.ObjLoader;
import com.team.engine.rendering.PointLight;
import com.team.engine.rendering.Primitives;
import com.team.engine.rendering.Shader;
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
	
	public static Mesh mat1;
	public static Mesh mat2;
	
	private Mesh objMesh;
	

	public static Material crateMaterial = new Material("container2.png", 0.8f, null, "container2_specular.png");
	public static Material groundMaterial = new Material("brickwall.jpg", 0.6f, "brickwall_normal.jpg", 0.3f);
	public static Material monkeyMaterial = new Material(new Vec3(1, 1, 1), 0.0f, 1.0f);
	
	public static Material outsideMaterial = new Material("metal/albedo.png", "metal/roughness.png", "metal/normal.png", "metal/metallic.png");
	public static Material insideMaterial = new Material("plastic/albedo.png", "plastic/roughness.png", "plastic/normal.png", "plastic/metallic.png");
	
	public static Material gravelMaterial = new Material("gravel/albedo.png", 0.9f, 0f);
	
	
	
	public static void main(String[] args) {
		Engine.start(false, false, new GLDemo());
	}

	@Override
	public void init() {
		Engine.loadTexture("container2.png", false, true);
		Engine.loadTexture("container2_specular.png");
		Engine.loadTexture("brickwall.jpg", false, true);
		Engine.loadTexture("brickwall_normal.jpg");
		
		Engine.loadTexture("metal/albedo.png", false, true);
		Engine.loadTexture("metal/normal.png");
		Engine.loadTexture("metal/metallic.png");
		Engine.loadTexture("metal/roughness.png");
		
		Engine.loadTexture("plastic/albedo.png", false, true);
		Engine.loadTexture("plastic/normal.png");
		Engine.loadTexture("plastic/metallic.png");
		Engine.loadTexture("plastic/roughness.png");
		
		Engine.loadTexture("gravel/albedo.png", false, true);
		Engine.loadTexture("gravel/normal.png");
		Engine.loadTexture("gravel/metallic.png");
		Engine.loadTexture("gravel/roughness.png");
		
		Engine.loadShader("pbr");

		
		//Create the cube mesh object from the primitive.
		cubeMesh = Mesh.raw(Primitives.cube(1.0f), false);
		groundMesh = Mesh.raw(Primitives.cube(16.0f), true);
		//load our monkey from disk
		objMesh = ObjLoader.loadFile("sphere.obj");
		mat1 = ObjLoader.loadFile("matmodel-1.obj");
		mat2 = ObjLoader.loadFile("matmodel-2.obj");
		
		Engine.scene.skybox = new Cubemap("papermill");
		Engine.scene.irradiance = new Cubemap("papermill-irradiance");
		
		Engine.camera.setPosition(new Vec3(0, 0, 5));
		
		Engine.scene.ambient = new Vec3(0.4, 0.4, 0.4);
		Engine.scene.sun.color = new Vec3(0, 0, 0);
		Engine.scene.sun.direction = new Vec3(-1, -0.8, 0.7f);
		Engine.scene.skyColor = new Vec3(0, 0, 0.5);
		
		for (int i = 0; i < cubePositions.length; i++) {
			float angle = 20.0f * i;
			//Engine.scene.add(new MeshObject(cubePositions[i], new Quat4f(1.0f, 0.3f, 0.5f, (float)Math.toRadians(angle)), new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)), 1f, cubeMesh, 1f, crateMaterial));
		}
		Engine.scene.add(new MeshObject(new Vec3(-10, -10, -12), new Quat4f(), new BoxShape(new Vector3f(2f, 5f, 2f)), 0f, mat1,0.5f,  insideMaterial));
		Engine.scene.add(new MeshObject(new Vec3(-10, -10, -12), new Quat4f(), new SphereShape(0.5f), 0f, mat2,0.5f,  outsideMaterial));
		
		Engine.scene.add(new MeshObject(new Vec3(0, -60f, 0), new Quat4f(), new BoxShape(new Vector3f(50f, 50f, 50f)), 0f, groundMesh, 100f, groundMaterial));
		
		//Engine.scene.add(new PointLight(new Vec3(20, 0, 0), new Vec3(1, 1, 1), 100f, 100f));
	}

	private static float accum;

	@Override
	public void tick() {
		accum += Engine.deltaTime;
		
		if (Input.isButtonDown(1) && accum > 0.1f) {
			FPSCamera cam = (FPSCamera)Engine.camera;
			MeshObject c = new MeshObject(cam.getPosition(), new Quat4f(1.0f, 0.3f, 0.5f, 0f), new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)), 1f, cubeMesh, 1f, crateMaterial);
			Engine.scene.add(c);
			c.rb.applyCentralForce(new Vector3f(0.0f, 100.0f, 0.0f));
			accum = 0;
		}
		if (Input.isButtonDown(2) && accum > 1f) {
			PointLight p = new PointLight(Engine.camera.getPosition(), new Vec3(1.0f, 1.0f, 2.0f), 5f, 10f);
			Engine.scene.add(p);

			accum = 0;
		}	
	}

	@Override
	public void render() {	
		
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
}
