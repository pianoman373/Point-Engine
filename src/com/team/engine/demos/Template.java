package com.team.engine.demos;

import com.team.engine.Engine;
import com.team.engine.rendering.Shader;
import com.team.engine.AbstractGame;

/**
 * A template for any game ever.
 * Serves no purpose, just here for convenience.
 */
public class Template extends AbstractGame {
	public static void main(String[] args) {
		Engine.start(false, false, new Template());
	}

	@Override
	public void init() {
		
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void postUpdate() {
		
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
