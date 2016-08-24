package com.team.engine.demos;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import com.team.engine.Engine;
import com.team.engine.Shader;

public class InputDemo extends Engine {
	public static void main(String[] args) {
		new InputDemo().initialize(false);
	}
	
	private static Controller controller;

	@Override
	public void setupGame() {
		if (Controllers.getControllerCount() > 0) {
			controller = Controllers.getController(0);
		}
	}
	
	@Override
	public void tick() {
		float lookX = controller.getAxisValue(4);
		float lookY = controller.getAxisValue(5);
		
		if (lookX > 0.3f || lookX < -0.3f || lookY > 0.3f || lookY < -0.3f) {
			System.out.println("send packet: ("+lookX+","+lookY+")");
		}
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
