package com.team.engine.demos.kdemo;

import javax.vecmath.Quat4f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.team.engine.gameobject.MeshObject;
import com.team.engine.rendering.Material;
import com.team.engine.rendering.Mesh;
import com.team.engine.vecmath.Vec3;

public class MeshSphere extends MeshObject {

	public MeshSphere(Vec3 pos, Quat4f rotation, CollisionShape bounds, float mass, Mesh mesh, float scale, Material material) {
		super(pos, rotation, bounds, mass, mesh, scale, material);
	
	}

	public void update() {
	}
	
}
