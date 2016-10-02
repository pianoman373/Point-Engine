package com.team.engine.demos;

import com.team.engine.Engine;
import com.team.engine.vecmath.Vec3;
import com.team.rendering.Material;
import com.team.rendering.Mesh;
import com.team.rendering.PointLight;
import com.team.rendering.Primitives;
import com.team.rendering.Shader;
import com.team.engine.AbstractGame;

/**
 * A demo utilizing sprite rendering, Grid2D's and dyn4j physics.
 */
public class Template extends AbstractGame {
	private Material planeMaterial = new Material(new Vec3(0.5f, 0.5f, 0.5f), 0.1f, 16f);
	private Mesh planeMesh;
	
	public static void main(String[] args) {
		Engine.start(false, new Template());
	}

	@Override
	public void init() {
		Engine.loadShader("standard");
		planeMesh = Mesh.raw(Primitives.cube(1.0f), true);
		
		Engine.scene.add(new PointLight(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(1.0f, 1.0f, 1.0f), 0.42f, 0.2f));
	}
	
	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		Shader s = Engine.getShader("standard");
		s.bind();

		s.uniformMaterial(planeMaterial);
		planeMesh.draw();
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
