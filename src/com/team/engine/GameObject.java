package com.team.engine;

public class GameObject {
	public Transform transform;
	public Mesh mesh = null;
	public Shader shader;
	
	public GameObject() {
		this.transform = new Transform();
		this.shader = new Shader("standard");
	}
	
	public void update() {
	}
	
	public void render(Scene scene) {
		if (mesh != null) {
			
		}
	}
}
