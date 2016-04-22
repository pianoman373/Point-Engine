package com.team.engine.demos;

import com.team.engine.Engine;
import com.team.engine.Mesh;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.Texture;
import com.team.engine.Tileset2D;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL and lighting shaders.
 */
public class Demo2D extends Engine {
	private Shader spriteShader;
	private Texture mapTexture;
	private Mesh cubeMesh;
	private Tileset2D tileset;
	
	public static void main(String[] args) {
		new Demo2D().initialize(true);
	}

	@Override
	public void setupGame() {
		//Load all our shaders and textures from disk.
		mapTexture = new Texture("resources/textures/retro-map.png");
		spriteShader = new Shader("sprite");
		cubeMesh = new Mesh(Primitives.sprite(new Vec2(0, 0), new Vec2(1, 1)));
		this.background = new Vec3(0.0f, 0.5f, 1.0f);
		
		tileset = new Tileset2D("resources/retro-terrain.png", 16, 16);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		//cubeMesh.bind();
		spriteShader.bind();
		//mapTexture.bind();
		//spriteShader.uniformMat4("model", Mat4.scale(50.0f, 30.0f, 0.0f));
		
		spriteShader.uniformMat4("model", Mat4.scale(50.0f, 50.0f, 0.0f));
		tileset.image.bind();
		tileset.mesh.bind();
		tileset.mesh.draw();
		tileset.mesh.unBind();
		
		//cubeMesh.draw();
		
		//cubeMesh.unBind();
	}
}
