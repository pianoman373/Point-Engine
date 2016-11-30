package com.team.engine.gameobject;

import static com.team.engine.Globals.*;
import static org.lwjgl.glfw.GLFW.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.linearmath.Transform;
import com.team.engine.Input;
import com.team.engine.rendering.Material;
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
	
	@Override
	public void update() {
		Transform trans = new Transform();
		rb.getMotionState().getWorldTransform(trans);
		Quat4f rotation = new Quat4f();
		
		rotation.set(new Matrix4f(mat4().rotateY(-angle).getArray()));
		
		trans.setRotation(rotation);
		
		Vector3f velocity = new Vector3f();
		rb.getLinearVelocity(velocity);
		
		
		rb.setWorldTransform(trans);
		
		Vec3 vel = vec3();
		
		if (Input.isKeyDown(GLFW_KEY_UP)) {
			vel = vec3(0, 0, -10).rotateYaw(angle);
		}
		if (Input.isKeyDown(GLFW_KEY_DOWN)) {
			vel = vec3(0, 0, -10).rotateYaw(angle).negate();
		}
		
		if (Input.isKeyDown(GLFW_KEY_LEFT)) {
			angle += 2;
			
		}
		if (Input.isKeyDown(GLFW_KEY_RIGHT)) {
			angle -= 2;
		}
		
		vel.y =velocity.y;
		
		if (Input.isKeyDown(GLFW_KEY_SPACE)) {
			vel.y = 10f;
		}
		
		this.rb.setLinearVelocity(vel.asv3f());
		
		this.rb.activate();
	}
	
	public Vec3 getDirection() {
		return vec3(0, 0, -1).rotateYaw(angle);
	}
}
