package com.team.engine.demos;

import javax.vecmath.Quat4f;

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
public class MaterialDemo extends AbstractGame {
	private Mesh objMesh1;
	private Mesh objMesh2;
	
	private Mesh planeMesh;
	
	public static void main(String[] args) {
		Engine.start(false, false, new MaterialDemo());
	}

	@Override
	public void init() {
		
		Engine.loadShader("pbr");
		
		Engine.loadTexture("Cerberus_A.png");
		Engine.loadTexture("Cerberus_M.png");
		Engine.loadTexture("Cerberus_N.png");
		Engine.loadTexture("Cerberus_R.png");
		
		Engine.loadTexture("stone_tile.png");
		Engine.loadTexture("stone_tile_normal.png");
		Engine.loadTexture("stone_tile_specular.png");
		
		objMesh1 = ObjLoader.loadFile("sphere.obj");
		objMesh2 = ObjLoader.loadFile("gun.obj");
		planeMesh = Mesh.raw(Primitives.plane(10), true);
		
		Engine.scene.skybox = new Cubemap("skybox-4");
		Engine.scene.irradiance = new Cubemap("skybox-4-irradiance");
		Engine.scene.sun.color = new Vec3(0.6, 0.5, 0.5);
		Engine.scene.sun.direction = new Vec3(-0.3, -1.0, -1.0);
		Engine.scene.add(new PointLight(new Vec3(-1, 4.7, -3), new Vec3(0.3, 0.3f, 0.7f), 20f, 5));
		
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 7; y++) {
				Material mat = new Material("stone_tile.png", y / 7f, "stone_tile_normal.png", x / 7f);
				Engine.scene.add(new MeshObject(new Vec3(x * 3, y * 3, 0), new Quat4f(), null, 0f, objMesh1, 1f, mat));
			}
		}
		
		
		
		Material mat = new Material("stone_tile.png", 0.6f, "stone_tile_normal.png", "stone_tile_specular.png");
		Engine.scene.add(new MeshObject(new Vec3(0, -1, 0), new Quat4f(), null, 0f, planeMesh, 100f, mat));
		Material mat2 = new Material("Cerberus_A.png", "Cerberus_R.png", "Cerberus_N.png", "Cerberus_M.png");
		Engine.scene.add(new MeshObject(new Vec3(0, 5, -5), new Quat4f(0, 0, 0, (float)Math.toRadians(45)), null, 0f, objMesh2, 5f, mat2));
	}

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
