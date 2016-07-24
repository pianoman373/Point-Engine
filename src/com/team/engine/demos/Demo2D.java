package com.team.engine.demos;

import com.team.engine.Engine;
import com.team.engine.Grid2D;
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
	private Tileset2D tileset;
	private Grid2D grid;
	
	private Mesh sprite;
	private Texture tex;
	private Shader spriteShader;
	
	public static void main(String[] args) {
		new Demo2D().initialize(true);
	}

	@Override
	public void setupGame() {
		this.background = new Vec3(0.0f, 0.5f, 1.0f);
		tileset = new Tileset2D("resources/retro-terrain.png", 16, 16);
		spriteShader = new Shader("sprite");
		
		byte[][] map = new byte[][] {
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 1},
				{0, 0, 0, 0, 1},
				{0, 0, 0, 0, 1},
				{0, 0, 0, 0, 65}
		};
		
		sprite = new Mesh(Primitives.sprite(new Vec2(0, 0), new Vec2(1, 1)));
		tex = new Texture("resources/textures/container.jpg");
		
		grid = new Grid2D(map, tileset, new Vec2(5, 5));
		
		//grid = new Grid2D("resources/retro.tmx", tileset);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		//grid.render();
		
		spriteShader.bind();
		spriteShader.uniformMat4("model", new Mat4());
		tex.bind(0);
		sprite.draw();
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kill() {
		
	}
}
