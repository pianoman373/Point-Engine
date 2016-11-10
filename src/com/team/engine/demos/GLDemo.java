package com.team.engine.demos;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;


import com.bulletphysics.collision.shapes.BoxShape;
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
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL, bullet physics, skyboxes, and lighting shaders.
 */
public class GLDemo extends AbstractGame {
	public static Mesh cubeMesh;
	public static Mesh groundMesh;
	
	public static Mesh mat1;
	public static Mesh mat2;
	
	private Mesh sphere;
	
	private Model model;

	public static Material crateMaterial = new Material("container2.png", 0.8f, null, "container2_specular.png");
	public static Material groundMaterial = new Material("brickwall.jpg", 0.6f, "brickwall_normal.jpg", 0.3f);
	
	public static Material outsideMaterial = new Material("metal/albedo.png", "metal/roughness.png", "metal/normal.png", "metal/metallic.png");
	public static Material insideMaterial = new Material("plastic/albedo.png", "plastic/roughness.png", "plastic/normal.png", "plastic/metallic.png");
	
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
		
		Engine.loadTexture("stone_tile.png", false, true);
		Engine.loadTexture("stone_tile_normal.png");
		Engine.loadTexture("stone_tile_specular.png");
		
		cubeMesh = Mesh.raw(Primitives.cube(1.0f), false);
		groundMesh = Mesh.raw(Primitives.cube(16.0f), true);
		sphere = ObjLoader.loadFile("sphere.obj");
		mat1 = ObjLoader.loadFile("matmodel-1.obj");
		mat2 = ObjLoader.loadFile("matmodel-2.obj");
		
		model = new Model("adam.fbx", new Mat4().translate(new Vec3(0, -10, 0)).rotateX(-90).scale(0.053f), true);
		
		
		Engine.scene.skybox = new Cubemap("sunset");
		Engine.scene.irradiance = new Cubemap("sunset-irradiance");
		
		Engine.camera.setPosition(new Vec3(0, 0, 5));
		
		Engine.scene.sun.color = new Vec3(5, 5, 5);
		Engine.scene.sun.direction = new Vec3(-1, -0.8, -0.7f);
		
		Engine.scene.add(new MeshObject(new Vec3(-10, -10, -12), new Quat4f(), new BoxShape(new Vector3f(2f, 5f, 2f)), 0f, mat1,0.5f,  insideMaterial));
		Engine.scene.add(new MeshObject(new Vec3(-10, -10, -12), new Quat4f(), new SphereShape(0.5f), 0f, mat2,0.5f,  outsideMaterial));
		
		Engine.scene.add(new MeshObject(new Vec3(0, -60f, 0), new Quat4f(), new BoxShape(new Vector3f(50f, 50f, 50f)), 0f, groundMesh, 100f, groundMaterial));
		
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 7; y++) {
				Material mat = new Material("stone_tile.png", y / 7f, "stone_tile_normal.png", x / 7f);
				Engine.scene.add(new MeshObject(new Vec3(x * 3, y * 3, 0).add(new Vec3(0, -9, -15)), new Quat4f(), null, 0f, sphere, 1f, mat));
			}
		}
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
		model.render();
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		
	}

	@Override
	public void kill() {
		
	}

	@Override
	public void renderShadow(Shader s) {
		model.renderShadow(s);
	}
}
