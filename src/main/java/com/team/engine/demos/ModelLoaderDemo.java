package com.team.engine.demos;

import com.team.engine.AbstractGame;
import com.team.engine.Cubemap;
import com.team.engine.Engine;
import com.team.engine.Mesh;
import com.team.engine.ObjLoader;
import com.team.engine.PointLight;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D model loading and texture mapping
 */
public class ModelLoaderDemo extends AbstractGame {
	private static PointLight lights[] = {
		new PointLight(new Vec3(-1, 4.7, -3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f),
		new PointLight(new Vec3(1, 4.7, -3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f),
		new PointLight(new Vec3(-1, 4.7, 3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f),
		new PointLight(new Vec3(1, 4.7, 3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f)
	};
	
	private Mesh cubeMesh;
	private Mesh objMesh;
	private Mesh objMesh2;
	private Mesh objMesh3;
	
	public static void main(String[] args) {
		Engine.start(false, new ModelLoaderDemo());
	}

	@Override
	public void init() {
		Engine.loadTexture("HallwayFloorAlbedo.png");
		Engine.loadTexture("HallwayFloorSpecularGloss.png");
		
		Engine.loadTexture("HallwayWallsAlbedo.png");
		Engine.loadTexture("HallwayWallsSpecularGloss.png");
		
		Engine.loadTexture("HallwayRoofAlbedo.png");
		Engine.loadTexture("HallwayRoofSpecularGloss.png");
		
		Engine.loadShader("standard");
		Engine.loadShader("light");
		
		Engine.setFramebuffer(new Shader("shaders/hdr"));
		
		cubeMesh = new Mesh(Primitives.cube(1.0f));
		
		objMesh = ObjLoader.loadFile("hallway_floor.obj");
		objMesh2 = ObjLoader.loadFile("hallway_walls.obj");
		objMesh3 = ObjLoader.loadFile("hallway_roof.obj");
		
		Engine.scene.skybox = new Cubemap("skybox/");
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		//Bind our shader.
		Shader s = Engine.getShader("standard");
		s.bind();
		Engine.scene.skybox.bind(2);
		
		//Send material parametersand ambient.
		s.uniformFloat("ambient", 0.2f);
		s.uniformInt("material.diffuse", 0);
		s.uniformBool("material.diffuseTextured", true);
		
		s.uniformInt("material.specular", 1);
		s.uniformBool("material.specularTextured", true);
		s.uniformVec3("material.specularColor", new Vec3(0.5,0.5,0.5));
		s.uniformFloat("material.shininess", 64.0f);
		s.uniformInt("skybox", 2);
		s.uniformFloat("exposure", 1.0f);
		
		s.uniformInt("pointLightCount", lights.length);
		for (int i = 0; i < lights.length; i++) {
			s.uniformPointLight("pointLights[" + i + "]", lights[i]);
		}
		
		//draw the hallway 10 times
		for (int i = 0; i < 10; i++) {
			s.uniformMat4("model", new Mat4().translate(new Vec3(0, 0, i * 8)));
			
			Engine.getTexture("HallwayFloorAlbedo.png").bind(0);
			Engine.getTexture("HallwayFloorSpecularGloss.png").bind(1);
			objMesh.draw();
			
			Engine.getTexture("HallwayWallsAlbedo.png").bind(0);
			Engine.getTexture("HallwayWallsSpecularGloss.png").bind(1);
			objMesh2.draw();
			
			Engine.getTexture("HallwayRoofAlbedo.png").bind(0);
			Engine.getTexture("HallwayRoofSpecularGloss.png").bind(1);
			objMesh3.draw();
		}
		
		//Now we switch over to our light shader so we can draw each light.
		Shader s2 = Engine.getShader("light");
		s2.bind();
		
		for (PointLight light : lights) {
			s2.uniformMat4("model", new Mat4().translate(light.position).scale(0.2f));
			s2.uniformVec3("lightColor", light.color);
			cubeMesh.draw();
		}
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		//Send our exposure uniform to the post processing shader.
		shader.uniformFloat("exposure", 1.0f);
	}

	@Override
	public void kill() {
		
	}

	@Override
	public void renderShadow(Shader s) {
		// TODO Auto-generated method stub
		
	}
}