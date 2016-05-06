package com.team.engine;

import java.util.ArrayList;

public class Scene {
	public ArrayList<PointLight> lights = new ArrayList<PointLight>();
	public ArrayList<GameObject> objects = new ArrayList<GameObject>();
	public Camera camera;
	
	public Scene(Camera camera) {
		this.camera = camera;
	}
}
