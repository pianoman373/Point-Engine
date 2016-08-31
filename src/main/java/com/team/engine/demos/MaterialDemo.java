package com.team.engine.demos;

import javax.vecmath.Quat4f;

import com.bulletphysics.collision.shapes.SphereShape;
import com.team.engine.*;
import com.team.engine.vecmath.Vec3;
import com.team.engine.MeshObject;


/**
 * A demo showing off 3D rendering with openGL, bullet physics, skyboxes, and lighting shaders.
 */
public class MaterialDemo extends AbstractGame {
	private Mesh objMesh1;
	private Mesh objMesh2;

	public static Material outsideMaterial = new Material(new Vec3(0.8f, 0.4f, 0.4f), new Vec3(0.7f, 0.7f, 0.7f), 64.0f);
	public static Material insideMaterial = new Material(new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.0f, 0.0f, 0.0f), 64.0f);
	
	public static void main(String[] args) {
		Engine.start(false, new MaterialDemo());
	}

	@Override
	public void init() {
		
		Engine.loadShader("standard");
		
		objMesh1 = ObjLoader.loadFile("matmodel-1.obj");
		objMesh2 = ObjLoader.loadFile("matmodel-2.obj");
		
		Engine.scene.skybox = new Cubemap("skybox-3");
		//Engine.scene.sun.color = new Vec3(0.5, 0.5, 0.5);
		Engine.scene.sun.castShadow = false;
		
		Engine.scene.add(new MeshObject(new Vec3(), new Quat4f(), null, 0f, objMesh1, 1f, insideMaterial));
		Engine.scene.add(new MeshObject(new Vec3(), new Quat4f(), null, 0f, objMesh2, 1f, outsideMaterial));
	}

	private static float accum;

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {		
		
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
