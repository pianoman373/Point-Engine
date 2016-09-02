package com.team.engine.demos;

import com.team.engine.Engine;
import com.team.engine.PointLight;
import com.team.engine.Shader;
import com.team.engine.Material;
import com.team.engine.Mesh;
import com.team.engine.MeshObject;
import com.team.engine.ObjLoader;
import com.team.engine.vecmath.Vec3;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.team.engine.AbstractGame;
import com.team.engine.Cubemap;
import com.team.engine.Primitives;

/**
 * A demo utilizing sprite rendering, Grid2D's and dyn4j physics.
 */
public class KDemo extends AbstractGame {
	public static Material groundMaterial = new Material("stone_tile.png", "stone_tile_specular.png", "stone_tile_normal.png", 0.2f);
	public static Material sphereMaterial = new Material(new Vec3(0.8f, 0.8f, 0.8f), 0f, 1.0f);

	
	private Mesh planeMesh;
	private Mesh sphereMesh;
	
	public static void main(String[] args) {
		Engine.start(false, new KDemo());
	}

	@Override
	public void init() {
		
		Engine.loadTexture("stone_tile.png");
		Engine.loadTexture("stone_tile_normal.png");
		Engine.loadTexture("stone_tile_specular.png");
		Engine.loadShader("standard");
		planeMesh = new Mesh(Primitives.plane(16.0f));
		
		sphereMesh = ObjLoader.loadFile("sphere.obj");

		Engine.scene.skybox = new Cubemap("skybox-k");
		
		Engine.scene.add(new PointLight(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(1.0f, 1.0f, 1.0f), 0.42f, 0.2f));
		
		Engine.scene.add(new MeshObject(new Vec3(), new Quat4f(), new SphereShape(1.0f), 1f, sphereMesh, 1f, sphereMaterial));
		Engine.scene.add(new MeshObject(new Vec3(0, -10f, 0), new Quat4f(1.0f, 0.0f, 0.0f, 60.0f), new BoxShape(new Vector3f(50f, 0f, 50f)), 0f, planeMesh, 100f,  groundMaterial));

	}
	
	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		Shader s = Engine.getShader("standard");
		s.bind();
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
