package com.team.engine.gameobject;

import static com.team.engine.Globals.*;
import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.team.engine.Camera;
import com.team.engine.Engine;
import com.team.engine.Scene;
import com.team.engine.rendering.Material;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.Shader;
import com.team.engine.vecmath.Vec3;

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
	public MeshObject(Vec3 pos, Vec3 velocity, Quat4f rotation, CollisionShape bounds, float mass, Mesh mesh, float scale, Material material) {
		// setup the motion state for the crate
		DefaultMotionState fallMotionState = new DefaultMotionState(new Transform(new Matrix4f(rotation, new Vector3f(pos.x, pos.y, pos.z), 1.0f)));

		Vector3f fallInertia = new Vector3f(0,0,0);
		if (bounds != null)
			bounds.calculateLocalInertia(mass,fallInertia); 
		this.mesh = mesh;
		this.material = material;
		this.scale = scale;

		rb = new RigidBody(mass,fallMotionState,bounds,fallInertia); 
		
		rb.setLinearVelocity(velocity.asv3f());
	}
	
	public MeshObject(Vec3 pos, Quat4f rotation, CollisionShape bounds, float mass, Mesh mesh, float scale, Material material) {
		this(pos, vec3(), rotation, bounds, mass, mesh, scale, material);
	}
	
	public void init(Scene scene) {
		scene.world.addRigidBody(this.rb);
	}
	
	public void update() {
		
	}
	
	public void render(Scene scene, Camera cam) {
		Shader s = getShader("pbr");
		s.bind();
		
		s.uniformMaterial(this.material);
		
		Transform trans = new Transform();
		Quat4f q = new Quat4f();
		rb.getMotionState().getWorldTransform(trans);
		trans.getRotation(q);
		
		Matrix4f mat = new Matrix4f();
		trans.getMatrix(mat);
		s.uniformMat4("model", mat4(mat).scale(this.scale));
		mesh.draw();
		
		
		if (scene.debug) {
			CollisionShape shape = rb.getCollisionShape();
			
			Shader s2 = getShader("debug");
			s2.bind();
			s2.uniformVec3("color", vec3(5, 1, 1));
			s2.uniformMat4("model", mat4(mat).scale(this.scale * 1.0001f));
			glDisable(GL_CULL_FACE);
			
			if (shape instanceof BoxShape) {
				BoxShape bshape = (BoxShape)shape;
				
				Vec3 extents = vec3(bshape.getHalfExtentsWithMargin(new Vector3f()));
				print(extents);
				s2.uniformMat4("model", mat4(mat).scale(vec3(extents.x * 2, extents.y * 2, extents.z * 2)));
				Engine.debugCubeMesh.draw(GL_LINES);
			}
			if (shape instanceof SphereShape) {
				Engine.debugSphereMesh.draw(GL_LINES);
			}
			
			glEnable(GL_CULL_FACE);
		}
	}

	@Override
	public void renderShadow(Shader s) {
		Transform trans = new Transform();
		Quat4f q = new Quat4f();
		rb.getMotionState().getWorldTransform(trans);
		trans.getRotation(q);
		
		Matrix4f mat = new Matrix4f();
		trans.getMatrix(mat);
		
		s.uniformMat4("model", mat4(mat).scale(this.scale));
		mesh.draw();
	}
}