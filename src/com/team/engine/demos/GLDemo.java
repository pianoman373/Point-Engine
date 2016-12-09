package com.team.engine.demos;

import static com.team.engine.Globals.*;
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

/**
 * Mainly a showcase of the engine's graphical capabilities. Also has a (buggy) 3rd person player test.
 */
public class GLDemo extends AbstractGame {
	public static Mesh cubeMesh;
	public static Mesh groundMesh;
	
	public static Mesh blender1;
	public static Mesh blender2;
	
	private Mesh sphere;
	
	public static Material crateMaterial = new Material("container2.png", 0.8f, null, "container2_specular.png");
	public static Material groundMaterial = new Material("brickwall.jpg", 0.6f, "brickwall_normal.jpg", 0.3f);
	
	public static Material outsideMaterial = new Material("metal/albedo.png", "metal/roughness.png", "metal/normal.png", "metal/metallic.png");
	public static Material insideMaterial = new Material("plastic/albedo.png", "plastic/roughness.png", "plastic/normal.png", "plastic/metallic.png");
	
	ThirdPersonController player;
	
	//always needed for every runnable demo ever
	public static void main(String[] args) {
		Engine.start(false, false, new GLDemo(), null);
	}

	@Override
	public void init() {
		//lotta textures to load
		loadTexture("container2.png", false, true);
		loadTexture("container2_specular.png");
		loadTexture("brickwall.jpg", false, true);
		loadTexture("brickwall_normal.jpg");
		
		loadTexture("metal/albedo.png", false, true);
		loadTexture("metal/normal.png");
		loadTexture("metal/metallic.png");
		loadTexture("metal/roughness.png");
		
		loadTexture("plastic/albedo.png", false, true);
		loadTexture("plastic/normal.png");
		loadTexture("plastic/metallic.png");
		loadTexture("plastic/roughness.png");
		
		loadTexture("stone_tile.png", false, true);
		loadTexture("stone_tile_normal.png");
		loadTexture("stone_tile_specular.png");
		
		//all our meshes, some created in-engine and others imported via obj
		cubeMesh = Mesh.raw(Primitives.cube(1.0f), false);
		groundMesh = Mesh.raw(Primitives.cube(16.0f), true);
		sphere = ObjLoader.loadFile("sphere.obj");
		blender1 = ObjLoader.loadFile("matmodel-1.obj");
		blender2 = ObjLoader.loadFile("matmodel-2.obj");		
		
		//sets the skybox, renders as an actual skybox, and also used for reflections
		Engine.scene.skybox = new Cubemap("sunset");
		//think of this as an ambient map
		Engine.scene.irradiance = new Cubemap("sunset-irradiance");
		
		Engine.camera.setPosition(vec3(0, 0, 5));
		
		Engine.scene.sun.color = vec3(5, 5, 5);
		Engine.scene.sun.direction = vec3(-1, -0.8, -0.7f);
		
		Engine.scene.add(new MeshObject(vec3(-10, -10, -12), new Quat4f(), new BoxShape(new Vector3f(2f, 5f, 2f)), 0f, blender1,0.5f,  insideMaterial));
		Engine.scene.add(new MeshObject(vec3(-10, -10, -12), new Quat4f(), new SphereShape(0.5f), 0f, blender2,0.5f,  outsideMaterial));
		Engine.scene.add(new MeshObject(vec3(0, -60f, 0), new Quat4f(), new BoxShape(new Vector3f(50f, 50f, 50f)), 0f, groundMesh, 100f, groundMaterial));
		
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 7; y++) {
				Material mat = new Material("stone_tile.png", y / 7f, "stone_tile_normal.png", x / 7f);
				Engine.scene.add(new MeshObject(vec3(x * 3, y * 3, 0).add(vec3(0, -9, -15)), new Quat4f(), new SphereShape(1f), 1f, sphere, 1f, mat));
			}
		}
		
		player = new ThirdPersonController(vec3(10, 0, 0));
		
		Engine.scene.add(player);
	}

	//accumulator of time, useful for limiting how many times per second something can be done
	private static float accum;

	@Override
	public void update() {
		accum += Engine.deltaTime;
		
		//spawns cubes and lights, test if accum is a certain length
		if (Input.isButtonDown(1) && accum > 0.1f) {
			SpaceCamera cam = (SpaceCamera)Engine.camera;
			MeshObject c = new MeshObject(cam.getPosition(), cam.front.multiply(30), new Quat4f(1.0f, 0.3f, 0.5f, 0f), new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)), 1f, cubeMesh, 1f, crateMaterial);
			Engine.scene.add(c);
			accum = 0;
		}
		if (Input.isButtonDown(2) && accum > 1f) {
			PointLight p = new PointLight(Engine.camera.getPosition(), vec3(1.0f, 1.0f, 2.0f), 5f, 10f);
			Engine.scene.add(p);

			accum = 0;
		}
	}
	
	@Override
	public void postUpdate() {
		Engine.camera.setPosition(vec3(player.getTransform().origin) .add (vec3(0, 5, 0)) .subtract (player.getDirection().multiply(6)));
		Engine.camera.setDirection(player.getDirection().normalize() .add (vec3(0, -0.5f, 0)));
	}
}