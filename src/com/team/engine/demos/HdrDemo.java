package com.team.engine.demos;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;

import com.team.engine.DirectionalLight;
import com.team.engine.Engine;
import com.team.engine.Framebuffer;
import com.team.engine.Mesh;
import com.team.engine.PointLight;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.Texture;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2i;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL and lighting shaders.
 */
public class HdrDemo extends Engine {
	private static Vec3 cubePositions[] = {
		new Vec3( 2.0f,  -4.5f, -15.0f), 
		new Vec3(-1.5f, -4.5f, -2.5f),  
		new Vec3( 2.4f, -4.5f, -3.5f),  
		new Vec3( 1.3f, -4.5f, -2.5f),  
		new Vec3(-1.3f, -4.5f, -1.2f)  
	};
	
	private static PointLight lights[] = {
		new PointLight(new Vec3(-3, 0, -3), new Vec3(10f, 10f, 10f), 0.09f, 0.032f),
		new PointLight(new Vec3(1.0f, 1.0f, -15.0f), new Vec3(0.5f, 1.0f, 0.5f), 0.09f, 0.032f)
	};
	
	private Shader standardShader;
	private Shader lightShader;
	private Shader hdrShader;
	private Texture containerTexture;
	private Texture containerSpecTexture;
	private Texture brickTexture;
	private Mesh cubeMesh;
	private Mesh planeMesh;
	private Mesh framebufferMesh;
	private Framebuffer fbuffer;
	
	
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
		
		this.background = new Vec3(0.1f, 0.1f, 0.1f);
		this.ambient = new Vec3(0.1f, 0.1f, 0.1f);
		
		//Create the cube mesh object with our vertices.
		cubeMesh = new Mesh(Primitives.cube(1.0f));
		planeMesh = new Mesh(Primitives.plane(20.0f));
		framebufferMesh = new Mesh(Primitives.framebuffer());
		fbuffer = new Framebuffer(new Vec2i(1000, 800));
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		fbuffer.bind();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		//Bind two textures in different indexes so the shader has both.
		containerTexture.bind(0);
		containerSpecTexture.bind(1);
		
		//Bind our shader.
		standardShader.bind();
		
		//Send material parameters and the global ambient as well.
		standardShader.uniformVec3("ambient", this.ambient);
		standardShader.uniformInt("material.diffuse", 0);
		standardShader.uniformInt("material.specular", 1);
		standardShader.uniformFloat("material.shininess", 16.0f);
		
		//Our shader currently only has one variable for directional lights. It's not like we would use a lot anyways
		//unless we're on tatooine.
		DirectionalLight dirLight = new DirectionalLight(new Vec3(-0.2f, -1.0f, -0.3f), new Vec3(1f, 0.9f, 0.8f));
		standardShader.uniformDirectionalLight("dirLight", dirLight);
		
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
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		framebufferMesh.bind();
		hdrShader.bind();
		//brickTexture.bind();
		fbuffer.tex.bind();
		
		framebufferMesh.draw();
		
		framebufferMesh.unBind();
	}
}
