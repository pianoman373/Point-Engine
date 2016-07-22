package com.team.engine;

import java.util.ArrayList;
import java.util.Iterator;

import com.team.engine.vecmath.Mat4;

public class Scene {
	public ArrayList<PointLight> lights = new ArrayList<PointLight>();
	public ArrayList<GameObject> objects = new ArrayList<GameObject>();
	
	public void render(Camera cam) {
		Iterator<GameObject> i = objects.iterator();
		
		while (i.hasNext()) {
			i.next().render(this, cam);
		}
		
		Engine.instance.lightShader.bind();
		
		for (PointLight light : lights) {
			Engine.instance.lightShader.uniformMat4("model", new Mat4().translate(light.position).scale(0.2f));
			Engine.instance.lightShader.uniformVec3("lightColor", light.color);
			Engine.instance.cubeMesh.draw();
		}
		
		//Now we can unbind everything since we're done with the cube and the light shader.
		Engine.instance.lightShader.unBind();
	}
}
