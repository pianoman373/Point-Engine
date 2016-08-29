package com.team.engine.demos;

import com.team.engine.Engine;
import com.team.engine.PointLight;
import com.team.engine.Shader;
import com.team.engine.vecmath.Vec3;
import com.team.engine.AbstractGame;

/**
 * A demo utilizing sprite rendering, Grid2D's and dyn4j physics.
 */
public class Template extends AbstractGame {
	public static void main(String[] args) {
		Engine.start(false, new Template());
	}

	@Override
	public void init() {
		Engine.scene.add(new PointLight(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(1.0f, 1.0f, 1.0f), 0.42f, 0.2f));
	}
	
	@Override
	public void tick() {
		
	}

	@Override
	public void render() {

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
