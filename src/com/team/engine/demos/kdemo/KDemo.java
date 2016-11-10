package com.team.engine.demos.kdemo;

import com.team.engine.Engine;
import com.team.engine.gameobject.MeshObject;
import com.team.engine.rendering.Cubemap;
import com.team.engine.rendering.Material;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.ObjLoader;
import com.team.engine.rendering.PointLight;
import com.team.engine.rendering.Primitives;
import com.team.engine.rendering.Shader;
import com.team.engine.vecmath.Vec3;

import static com.team.engine.Globals.*;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.team.engine.AbstractGame;

/**
 * A demo utilizing sprite rendering, Grid2D's and dyn4j physics.
 */
public class KDemo extends AbstractGame {
	public static Material groundMaterial = new Material("stone_tile.png", "stone_tile_specular.png", "stone_tile_normal.png", 0.2f);
	public static Material sphereMaterial = new Material(vec3(1, 1, 1), 0.0f, 1.0f);
	public static Material boxMaterial = new Material("planks.jpg", 0.9f, null, "planks_specular.jpg");
	
	private Mesh planeMesh;
	private Mesh sphereMesh;
	private Mesh boxMesh;
	
	public static void main(String[] args) {
		Engine.start(false, false, new KDemo());
	}

	@Override
	public void init() {
		
		Engine.scene.sun.direction = vec3(0.0f, -1.0f, 0.0f);
		Engine.scene.sun.color = vec3(1.2f, 1.2f, 1.2f);
		
		Engine.loadTexture("stone_tile.png", false, true);
		Engine.loadTexture("stone_tile_normal.png");
		Engine.loadTexture("stone_tile_specular.png");
		Engine.loadTexture("planks.jpg", false, true);
		Engine.loadTexture("planks_specular.jpg");
		
		Engine.loadShader("pbr");
		planeMesh = Mesh.raw(Primitives.plane(16.0f), true);
		boxMesh = Mesh.raw(Primitives.cube(16.0f), true);
		sphereMesh = ObjLoader.loadFile("sphere.obj");

		Engine.scene.skybox = new Cubemap("sunset");
		Engine.scene.irradiance = new Cubemap("sunset-irradiance");
		
		Engine.scene.add(new PointLight(vec3(0.0f, 0.0f, 0.0f), vec3(1.0f, 1.0f, 1.0f), 0.42f, 0.2f));
		
		Engine.scene.add(new MeshSphere(vec3(), new Quat4f(), new SphereShape(1.0f), 1f, sphereMesh, 1f, sphereMaterial));
		Engine.scene.add(new MeshObject(vec3(0.0f, 5.0f, 0.0f), new Quat4f(), new BoxShape(new Vector3f(0.0f, 0.0f, 0.0f)), 0f, boxMesh, 1f, boxMaterial));
		Engine.scene.add(new MeshObject(vec3(0.0f, -10f, 0.0f), new Quat4f(1.0f, 0.0f, 0.0f, 60.0f), new BoxShape(new Vector3f(50f, 0f, 50f)), 0f, planeMesh, 100f,  groundMaterial));

	}
	
	@Override
	public void tick() {
		
		
		
	}

	@Override
	public void render() {
		//Shader s = Engine.getShader("standard");
		//s.bind();
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
