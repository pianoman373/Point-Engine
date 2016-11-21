package com.team.engine.gameobject;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.linearmath.Transform;
import com.team.engine.Input;
import com.team.engine.Scene;
import com.team.engine.rendering.Material;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.ObjLoader;
import com.team.engine.vecmath.Vec3;

public class FirstPersonController extends MeshObject {
	private float angle = 0;

	public FirstPersonController(Vec3 pos) {
		super(pos, new Quat4f(), new CapsuleShape(1, 2), 10, ObjLoader.loadFile("cool-dude.obj"), 1, new Material(vec3(0.7, 0.7, 0.7), 0.0f, 0.0f));
		this.rb.setFriction(100);
		this.rb.setAngularFactor(0);
		//this.rb.setDamping(0.1f, 10000f);
	}
	
	public void update() {
		Transform trans = new Transform();
		rb.getMotionState().getWorldTransform(trans);
		Quat4f rotation = new Quat4f();
		
		rotation.set(new Matrix4f(mat4().rotateY(-angle).getArray()));
		
		trans.setRotation(rotation);
		
		
		rb.setWorldTransform(trans);
		
		if (Input.isKeyDown(GLFW_KEY_UP)) {
			this.rb.setLinearVelocity(vec3(0, 0, -10).rotateYaw(angle).asv3f());
		}
		if (Input.isKeyDown(GLFW_KEY_DOWN)) {
			this.rb.setLinearVelocity(vec3(0, 0, -10).rotateYaw(angle).negate().asv3f());
		}
		
		if (Input.isKeyDown(GLFW_KEY_LEFT)) {
			angle += 2;
			
		}
		if (Input.isKeyDown(GLFW_KEY_RIGHT)) {
			angle -= 2;
		}
		
		this.rb.activate();
	}
}
