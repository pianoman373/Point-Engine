package com.team.engine;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.team.engine.demos.GLDemo;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

class MeshObject extends GameObject {
	private CollisionShape bounds = new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f));
	public RigidBody rb;
	private Mesh mesh;
	private Material material;
	
	public MeshObject(Vec3 pos, Quat4f rotation, DynamicsWorld dynamicsWorld, Mesh mesh, Material material) {
		// setup the motion state for the crate
		DefaultMotionState fallMotionState = new DefaultMotionState(new Transform(new Matrix4f(rotation, new Vector3f(pos.x, pos.y, pos.z), 1.0f)));

		//This we're going to give mass so it responds to gravity 
		int mass = 1;

		Vector3f fallInertia = new Vector3f(0,0,0); 
		bounds.calculateLocalInertia(mass,fallInertia); 

		rb = new RigidBody(mass,fallMotionState,bounds,fallInertia); 
		

		dynamicsWorld.addRigidBody(rb);
	}
	
	public void update() {
		
	}
	
	public void render(Scene scene, Camera cam) {
		
		
		Shader s = Engine.getShader("standard");
		s.bind();
		
		s.uniformMaterial(this.material);
		
		Transform trans = new Transform();
		Quat4f q = new Quat4f();
		rb.getMotionState().getWorldTransform(trans);
		trans.getRotation(q);
		
		Matrix4f mat = new Matrix4f();
		trans.getMatrix(mat);
		s.uniformMat4("model", new Mat4(mat));
		mesh.draw();
	}

	@Override
	public void renderShadow(Shader s) {
		Transform trans = new Transform();
		Quat4f q = new Quat4f();
		rb.getMotionState().getWorldTransform(trans);
		trans.getRotation(q);
		
		Matrix4f mat = new Matrix4f();
		trans.getMatrix(mat);
		
		s.uniformMat4("model", new Mat4(mat));
		mesh.draw();
	}
}