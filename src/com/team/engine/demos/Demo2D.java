package com.team.engine.demos;

import com.team.engine.Engine;
import com.team.engine.Grid2D;
import com.team.engine.Shader;
import com.team.engine.Tileset2D;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL and lighting shaders.
 */
public class Demo2D extends Engine {
	private Tileset2D tileset;
	private Grid2D grid;
	
	public static void main(String[] args) {
		new Demo2D().initialize(true);
	}

	@Override
	public void setupGame() {
		this.background = new Vec3(0.0f, 0.5f, 1.0f);
		tileset = new Tileset2D("resources/retro-terrain.png", 16, 16);
		
		byte[][] map = new byte[][] {
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 1},
				{0, 0, 0, 0, 1},
				{0, 0, 0, 0, 1},
				{0, 0, 0, 0, 65}
		};
		
		grid = new Grid2D(map, tileset, new Vec2(5, 5));
		
		grid = new Grid2D("resources/retro.tmx", tileset);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		//cubeMesh.bind();
		grid.render();
		
		//cubeMesh.draw();
		
		//cubeMesh.unBind();
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		// TODO Auto-generated method stub
		
	}
}
