package com.team.engine.demos;

import static org.lwjgl.glfw.GLFW.*;

import com.team.engine.Cubemap;
import com.team.engine.Engine;
import com.team.engine.Input;
import com.team.engine.Mesh;
import com.team.engine.PointLight;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.Texture;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL and lighting shaders.
 */
public class HdrDemo extends Engine {
	private static Vec3 cubePositions[] = {
		new Vec3( 2.0f, -4.5f, -15.0f), 
		new Vec3(-1.5f, -4.5f, -2.5f),  
		new Vec3( 2.4f, -4.5f, -3.5f),  
		new Vec3( 1.3f, -4.5f, -2.5f),  
		new Vec3(-1.3f, -4.5f, -1.2f)  
	};
	
	private static PointLight lights[] = {
		new PointLight(new Vec3(-3, 0, -3), new Vec3(4f, 4f, 4f), 0.09f, 0.032f),
		new PointLight(new Vec3(10.0f, -2.0f, -25.0f), new Vec3(0.5f, 1.0f, 0.5f), 0.09f, 0.032f),
		new PointLight(new Vec3(6, -2, 3), new Vec3(1f, 0.2f, 0.2f), 0.09f, 0.032f),
	};
	
	private Shader standardShader;
	private Shader lightShader;
	private Shader hdrShader;
	private Texture containerTexture;
	private Texture containerSpecTexture;
	private Texture brickTexture;
	private Mesh cubeMesh;
	private Mesh planeMesh;
	private float exposure = 1.0f;
	
	
	public static void main(String[] args) {
		new HdrDemo().initialize(false);
	}

	@Override
	public void setupGame() {
		//Load all our shaders and textures from disk.
		containerTexture = new Texture("resources/textures/container2.png");
		containerSpecTexture = new Texture("resources/textures/container2_specular.png");
		brickTexture = new Texture("resources/textures/brickwall.jpg");
		standardShader = new Shader("standard");
		lightShader = new Shader("light");
		hdrShader = new Shader("hdr");
		
		this.background = new Vec3(0.0f, 0.0f, 0.0f);
		this.ambient = new Vec3(0.0f, 0.0f, 0.0f);
		
		//Create the cube and plane mesh objects from primitives.
		cubeMesh = new Mesh(Primitives.cube(1.0f));
		planeMesh = new Mesh(Primitives.plane(20.0f));
		
		//set the engines framebuffer shader to our hdr shader.
		this.setFramebuffer(hdrShader);
	}

	@Override
	public void tick() {
		if (Input.isKeyDown(GLFW_KEY_COMMA)) {
			exposure -= 0.01;
		}
		if (Input.isKeyDown(GLFW_KEY_PERIOD)) {
			exposure += 0.01;
		}
		
		if (exposure < 0) exposure = 0;
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
		standardShader.uniformInt("material.specular", 1);
		standardShader.uniformFloat("material.shininess", 64.0f);
		
		//Our shader currently only has 2 spaces for point lights hardcoded in.
		standardShader.uniformInt("pointLightCount", lights.length);
		for (int i = 0; i < lights.length; i++) {
			standardShader.uniformPointLight("pointLights[" + i + "]", lights[i]);
		}
		
		//Bind the mesh and then draw it 10 times but with different model uniforms.
		cubeMesh.bind();
		for(int i = 0; i < cubePositions.length; i++)
		{
		  Mat4 model = new Mat4().translate(cubePositions[i]);
		  standardShader.uniformMat4("model", model);

		  cubeMesh.draw();
		}
		
		planeMesh.bind();
		
		brickTexture.bind(0);
		Texture.unBind(1);
		
		standardShader.uniformMat4("model", new Mat4().translate(new Vec3(0, -5, 0)).scale(new Vec3(100, 100, 100)));
		
		planeMesh.draw();
		
		cubeMesh.bind();
		lightShader.bind();
		
		for (PointLight light : lights) {
			lightShader.uniformMat4("model", new Mat4().translate(light.position).scale(0.2f));
			lightShader.uniformVec3("lightColor", light.color);
			cubeMesh.draw();
		}
		
		//Now we can unbind everything since we're done with the cube and the light shader.
		lightShader.unBind();
		cubeMesh.unBind();
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		//Send our exposure uniform to the post processing shader.
		shader.uniformFloat("exposure", exposure);
	}
}
