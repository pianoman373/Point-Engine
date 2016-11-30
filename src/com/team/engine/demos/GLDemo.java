package com.team.engine.demos;

import static com.team.engine.Globals.*;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.team.engine.*;
import com.team.engine.gameobject.FirstPersonController;
import com.team.engine.gameobject.MeshObject;
import com.team.engine.gui.GuiCalculator;
import com.team.engine.gui.GuiTest;
import com.team.engine.rendering.Cubemap;
import com.team.engine.rendering.Material;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.ObjLoader;
import com.team.engine.rendering.PointLight;
import com.team.engine.rendering.Primitives;
import com.team.engine.rendering.Shader;

import jdk.nashorn.internal.parser.JSONParser;

/**
 * A demo showing off 3D rendering with openGL, bullet physics, skyboxes, and lighting shaders.
 */
public class GLDemo extends AbstractGame {
	public static Mesh cubeMesh;
	public static Mesh groundMesh;
	
	public static Mesh mat1;
	public static Mesh mat2;
	
	private Mesh sphere;
	
	//private Model model;

	public static Material crateMaterial = new Material("container2.png", 0.8f, null, "container2_specular.png");
	public static Material groundMaterial = new Material("brickwall.jpg", 0.6f, "brickwall_normal.jpg", 0.3f);
	
	public static Material outsideMaterial = new Material("metal/albedo.png", "metal/roughness.png", "metal/normal.png", "metal/metallic.png");
	public static Material insideMaterial = new Material("plastic/albedo.png", "plastic/roughness.png", "plastic/normal.png", "plastic/metallic.png");
	
	
	private final GuiTest demo = new GuiTest();
	private final GuiCalculator calc = new GuiCalculator();
	
	FirstPersonController player;
	
	public static void main(String[] args) {
		Engine.start(false, false, new GLDemo());
	}

	@Override
	public void init() {
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
		
		loadShader("gui");
		
		cubeMesh = Mesh.raw(Primitives.cube(1.0f), false);
		groundMesh = Mesh.raw(Primitives.cube(16.0f), true);
		sphere = ObjLoader.loadFile("sphere.obj");
		mat1 = ObjLoader.loadFile("matmodel-1.obj");
		mat2 = ObjLoader.loadFile("matmodel-2.obj");
		
		//model = new Model("adam/adam.fbx", mat4().translate(vec3(0, -10, 0)).rotateX(-90).scale(0.053f), true);
		
		
		Engine.scene.skybox = new Cubemap("sunset");
		Engine.scene.irradiance = new Cubemap("sunset-irradiance");
		
		Engine.camera.setPosition(vec3(0, 0, 5));
		
		//Engine.scene.sun.color = vec3(5, 5, 5);
		//Engine.scene.sun.direction = vec3(-1, -0.8, -0.7f);
		
		Engine.scene.add(new MeshObject(vec3(-10, -10, -12), new Quat4f(), new BoxShape(new Vector3f(2f, 5f, 2f)), 0f, mat1,0.5f,  insideMaterial));
		Engine.scene.add(new MeshObject(vec3(-10, -10, -12), new Quat4f(), new SphereShape(0.5f), 0f, mat2,0.5f,  outsideMaterial));
		
		Engine.scene.add(new MeshObject(vec3(0, -60f, 0), new Quat4f(), new BoxShape(new Vector3f(50f, 50f, 50f)), 0f, groundMesh, 100f, groundMaterial));
		
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 7; y++) {
				Material mat = new Material("stone_tile.png", y / 7f, "stone_tile_normal.png", x / 7f);
				Engine.scene.add(new MeshObject(vec3(x * 3, y * 3, 0).add(vec3(0, -9, -15)), new Quat4f(), new SphereShape(1f), 1f, sphere, 1f, mat));
			}
		}
		
		player = new FirstPersonController(vec3(10, 0, 0));
		
		Engine.scene.add(player);
	}

	private static float accum;

	@Override
	public void update() {
		accum += Engine.deltaTime;
		
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
	
	public void postUpdate() {
		Engine.camera.setPosition(vec3(player.getTransform().origin) .add (vec3(0, 5, 0)) .subtract (player.getDirection().multiply(6)));
		Engine.camera.setDirection(player.getDirection().normalize() .add (vec3(0, -0.5f, 0)));
	}

	@Override
	public void render() {
		//model.render();
		
		demo.layout(NuklearManager.ctx, 50, 50);
		calc.layout(NuklearManager.ctx, 300, 50);
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		
	}

	@Override
	public void kill() {
		
	}

	@Override
	public void renderShadow(Shader s) {
		//model.renderShadow(s);
	}
}

interface SimpleEvent {
	public void call();
}
