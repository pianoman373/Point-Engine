package com.team.engine.demos;

import org.lwjgl.input.Keyboard;

import com.team.engine.Engine;
import com.team.engine.Mesh;
import com.team.engine.PointLight;
import com.team.engine.Primitives;
import com.team.engine.Scene;
import com.team.engine.Shader;
import com.team.engine.Texture;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off HDR and bloom effects.
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
	
	private Mesh cubeMesh;
	private Mesh planeMesh;
	private float exposure = 1.0f;
	
	private Scene scene;
	
	
	public static void main(String[] args) {
		new HdrDemo().initialize(false);
	}

	@Override
	public void setupGame() {
		//Load all our shaders and textures from disk.
		loadTexture("container2.png");
		loadTexture("container2_specular.png");
		loadTexture("brickwall.jpg");
		loadShader("standard");
		loadShader("hdr");
		
		this.background = new Vec3(0.0f, 0.0f, 0.0f);
		
		//Create the cube and plane mesh objects from primitives.
		cubeMesh = new Mesh(Primitives.cube(1.0f));
		planeMesh = Primitives.planeMesh(20.0f);
		
		//set the engines framebuffer shader to our hdr shader.
		this.setFramebuffer(getShader("hdr"));
		
		scene = new Scene();
		scene.lights.add(new PointLight(new Vec3(-3, 0, -3), new Vec3(4f, 4f, 4f), 0.09f, 0.032f));
		scene.lights.add(new PointLight(new Vec3(10.0f, -2.0f, -25.0f), new Vec3(0.5f, 1.0f, 0.5f), 0.09f, 0.032f));
		scene.lights.add(new PointLight(new Vec3(6, -2, 3), new Vec3(1f, 0.2f, 0.2f), 0.09f, 0.032f));
	}

	@Override
	public void tick() {
		if (Keyboard.isKeyDown(Keyboard.KEY_COMMA)) {
			exposure -= 0.01;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) {
			exposure += 0.01;
		}
		
		if (exposure < 0) exposure = 0;
	}

	@Override
	public void render() {		
		//Bind two textures in different indexes so the shader has both.
		getTexture("container2.png").bind(0);
		getTexture("container2_specular.png").bind(1);
		
		Shader s = getShader("standard");
		
		//bind our shader
		s.bind();
		
		//Send material parameters and ambient.
		s.uniformFloat("exposure", exposure);
		s.uniformFloat("ambient", 0.05f);
		s.uniformInt("material.diffuse", 0);
		s.uniformInt("material.specular", 1);
		s.uniformBool("material.diffuseTextured", true);
		s.uniformBool("material.specularTextured", true);
		s.uniformFloat("material.shininess", 64.0f);
		
		//send all the lights in the scene in
		s.uniformInt("pointLightCount", lights.length);
		for (int i = 0; i < lights.length; i++) {
			s.uniformPointLight("pointLights[" + i + "]", lights[i]);
		}
		
		//draw the same mesh with different model matrices each time
		for(int i = 0; i < cubePositions.length; i++)
		{
		  Mat4 model = new Mat4().translate(cubePositions[i]);
		  s.uniformMat4("model", model);

		  cubeMesh.draw();
		}
		
		//setup materials and then draw the floor
		getTexture("brickwall.jpg").bind(0);
		Texture.unBind(1);
		
		s.uniformMat4("model", new Mat4().translate(new Vec3(0, -5, 0)).scale(new Vec3(100, 100, 100)));
		s.uniformBool("material.specularTextured", false);
		s.uniformVec3("material.specularColor", new Vec3(0.6, 0.6, 0.6));
		planeMesh.draw();
		
		scene.render(Engine.instance.camera);
	}
	
	public void kill() {
		cubeMesh.delete();
		planeMesh.delete();
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		//Send our exposure uniform to the post processing shader.
		shader.uniformFloat("exposure", exposure);
	}
}
