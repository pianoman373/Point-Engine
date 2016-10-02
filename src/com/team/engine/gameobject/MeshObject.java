package com.team.engine.gameobject;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.team.engine.Camera;
import com.team.engine.Engine;
import com.team.engine.Scene;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
import com.team.rendering.Material;
import com.team.rendering.Mesh;
import com.team.rendering.Shader;

/**
 * MeshObject is a quick and easy to use implementation of GameObject. It handles simple physics and mesh rendering.
 */
public class MeshObject implements GameObject {
	public RigidBody rb;
	private Mesh mesh;
	private Material material;
	private float scale;
	
	/**
	 * Create a MeshObject, you still have to add it to a scene using Engine.scene.add(thisObject) for it to work.
	 * 
	 * Position and rotation are simple to use, bounds uses a CollisionShape from bullet. A common collision shape is BoxShape. 
	 * See com.bulletphysics.collision.shapes for a full list of CollisionShapes.
	 * CollisionShape may also be null for the object to act in a noClip sort of way.
	 * 
	 * mass can be set to 0 for it to be an immovable object such as the ground.
	 * 
	 * The mesh parameter is simply the mesh it will render from it's current position and scale it according to the parameter.
	 * 
	 * Material is just the set of textures, and colors to use during rendering.
	 * 
	 * This object is hardcoded to use the standard shader. If you need to use a custom shader you will have to render manually or make
	 * your own game object.
	 */
	public MeshObject(Vec3 pos, Quat4f rotation, CollisionShape bounds, float mass, Mesh mesh, float scale, Material material) {
		// setup the motion state for the crate
		DefaultMotionState fallMotionState = new DefaultMotionState(new Transform(new Matrix4f(rotation, new Vector3f(pos.x, pos.y, pos.z), 1.0f)));

		Vector3f fallInertia = new Vector3f(0,0,0);
		if (bounds != null)
			bounds.calculateLocalInertia(mass,fallInertia); 
		this.mesh = mesh;
		this.material = material;
		this.scale = scale;

		rb = new RigidBody(mass,fallMotionState,bounds,fallInertia); 
	}
	
	public void init(Scene scene) {
		scene.world.addRigidBody(this.rb);
	}
	
	public void update() {
		
	}
	
	public void render(Scene scene, Camera cam) {
		Shader s = Engine.getShader("pbr");
		s.bind();
		
		s.uniformMaterial(this.material);
		
		Transform trans = new Transform();
		Quat4f q = new Quat4f();
		rb.getMotionState().getWorldTransform(trans);
		trans.getRotation(q);
		
		Matrix4f mat = new Matrix4f();
		trans.getMatrix(mat);
		s.uniformMat4("model", new Mat4(mat).scale(this.scale));
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
		
		s.uniformMat4("model", new Mat4(mat).scale(this.scale));
		mesh.draw();
	}
}