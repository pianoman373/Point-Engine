package com.team.engine.gameobject;

import com.team.engine.Camera;
import com.team.engine.Scene;
import com.team.engine.rendering.Shader;

public interface GameObject {
	/**
	 * Called when the object is added to the scene.
	 */
	public void init(Scene scene);
	
	public void update();
	
	public void render(Scene scene, Camera cam);
	
	public void renderShadow(Shader s);
}
