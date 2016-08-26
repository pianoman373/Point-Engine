package com.team.engine;

public abstract class AbstractGame {
	//these are all classes that the main game must inherit
	public abstract void setupGame();

	public abstract void tick();

	public abstract void render();
		
	public abstract void renderShadow(Shader s);

	public abstract void kill();

	/**
	 * Add any uniforms you want to the currently bound framebuffer shader before rendering.
	 */
	public abstract void postRenderUniforms(Shader shader);
}
