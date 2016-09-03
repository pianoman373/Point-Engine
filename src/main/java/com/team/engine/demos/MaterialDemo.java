package com.team.engine.demos;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

import javax.vecmath.Quat4f;

import com.bulletphysics.collision.shapes.SphereShape;
import com.team.engine.*;
import com.team.engine.vecmath.Vec3;


/**
 * A demo showing off 3D rendering with openGL, bullet physics, skyboxes, and lighting shaders.
 */
public class MaterialDemo extends AbstractGame {
	private Mesh objMesh1;
	private Mesh objMesh2;

	public static Material outsideMaterial = new Material(new Vec3(1.0f, 0.2f, 0.0f), 0.0f, 1.0f);
	public static Material insideMaterial = new Material(new Vec3(0.3f, 0.3f, 0.3f), 0.3f, 0.0f);
	public static Material gunMaterial = new Material("Cerberus_A.png", "Cerberus_R.png", "Cerberus_N.png", "Cerberus_M.png");
	
	public static void main(String[] args) {
		Engine.start(false, new MaterialDemo());
	}

	@Override
	public void init() {
		
		Engine.loadShader("standard");
		
		Engine.loadTexture("Cerberus_A.png");
		Engine.loadTexture("Cerberus_N.png");
		Engine.loadTexture("Cerberus_M.png");
		Engine.loadTexture("Cerberus_R.png");
		
		objMesh1 = ObjLoader.loadFile("gun.obj");
		objMesh2 = ObjLoader.loadFile("matmodel-2.obj");
		
		Engine.scene.skybox = new Cubemap("skybox-2");
		//Engine.scene.sun.color = new Vec3(0.5, 0.5, 0.5);
		Engine.scene.sun.castShadow = false;
		Engine.scene.ambient = 0.1f;
		
		Engine.scene.add(new MeshObject(new Vec3(), new Quat4f(), null, 0f, objMesh1, 5f, gunMaterial));
		//Engine.scene.add(new MeshObject(new Vec3(), new Quat4f(), null, 0f, objMesh2, 1f, outsideMaterial));
	}

	private static float accum;

	@Override
	public void tick() {
		/*FloatBuffer axes = glfwGetJoystickAxes(GLFW_JOYSTICK_1);

		int axisID = 1;
		while (axes.hasRemaining()) {
		    float state = axes.get();
		    if (state < -0.95f || state > 0.95f) {
		        System.out.println("Axis " + axisID + " is at full-range!");
		    } else if (state < -0.5f || state > 0.5f) {
		        System.out.println("Axis " + axisID + " is at mid-range!");
		    }
		    axisID++;
		}*/
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
