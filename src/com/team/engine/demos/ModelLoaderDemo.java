package com.team.engine.demos;

import com.team.engine.Engine;
import com.team.engine.Mesh;
import com.team.engine.ObjLoader;
import com.team.engine.PointLight;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.Texture;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL and lighting shaders.
 */
public class ModelLoaderDemo extends Engine {
	private static PointLight lights[] = {
		new PointLight(new Vec3(-1, 4.7, -3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f),
		new PointLight(new Vec3(1, 4.7, -3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f),
		new PointLight(new Vec3(-1, 4.7, 3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f),
		new PointLight(new Vec3(1, 4.7, 3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f)
	};
	
	private Shader standardShader;
	private Shader lightShader;
	private Texture containerTexture;
	private Texture containerSpecTexture;
	private Mesh cubeMesh;
	private Mesh objMesh;
	
	public static void main(String[] args) {
		new ModelLoaderDemo().initialize(false);
	}

	@Override
	public void setupGame() {
		//Load all our shaders and textures from disk.
		containerTexture = new Texture("resources/textures/HallwayWallsAlbedo.png");
		containerSpecTexture = new Texture("resources/textures/HallwayWallsSpecularGloss.png");
		
		
		standardShader = new Shader("standard");
		lightShader = new Shader("light");
		this.ambient = new Vec3(0.3f, 0.3f, 0.3f);
		
		this.setFramebuffer(new Shader("hdr"));
		
		//Create the cube mesh object from the primitive.
		cubeMesh = new Mesh(Primitives.cube(1.0f));
		
		objMesh = ObjLoader.loadFile("resources/hallway.obj");
		
		this.background = new Vec3(0, 0, 0);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		//Bind two textures in different indexes so the shader has both.
		containerTexture.bind(0);
		containerSpecTexture.bind(1);
		
		//Bind our shader.
		standardShader.bind();
		
		//Send material parameters and the global ambient as well.
		standardShader.uniformVec3("ambient", this.ambient);
		standardShader.uniformInt("material.diffuse", 0);
		standardShader.uniformInt("material.useTex", 1);
		standardShader.uniformInt("material.specular", 1);
		standardShader.uniformFloat("material.shininess", 64.0f);
		standardShader.uniformInt("skybox", 2);
		
		standardShader.uniformInt("pointLightCount", lights.length);
		for (int i = 0; i < lights.length; i++) {
			standardShader.uniformPointLight("pointLights[" + i + "]", lights[i]);
		}
		
		//Draw the falling one.
		for (int i = 0; i < 10; i++) {
			standardShader.uniformMat4("model", new Mat4().translate(new Vec3(0, 0, i * 7.8)));
			objMesh.draw();
		}
		
		//Now we switch over to our light shader so we can draw each light. Notice we still don't need to unbind the cubemesh.
		lightShader.bind();
		
		for (PointLight light : lights) {
			lightShader.uniformMat4("model", new Mat4().translate(light.position).scale(0.2f));
			lightShader.uniformVec3("lightColor", light.color);
			cubeMesh.draw();
		}
		
		//Now we can unbind everything since we're done with the cube and the light shader.
		lightShader.unBind();
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		//Send our exposure uniform to the post processing shader.
		shader.uniformFloat("exposure", 1.0f);
	}

	@Override
	public void kill() {
		
	}
}
