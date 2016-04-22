package com.team.engine.demos;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.team.engine.DirectionalLight;
import com.team.engine.Engine;
import com.team.engine.Mesh;
import com.team.engine.PointLight;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.Texture;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL and lighting shaders.
 */
public class Demo2D extends Engine {
	private Shader spriteShader;
	private Texture mapTexture;
	private Mesh cubeMesh;
	
	public static void main(String[] args) {
		new Demo2D().initialize(true);
	}

	@Override
	public void setupGame() {
		//Load all our shaders and textures from disk.
		mapTexture = new Texture("resources/textures/retro-map.png");
		spriteShader = new Shader("sprite");
		cubeMesh = new Mesh(Primitives.sprite(1.0f));
		this.background = new Vec3(0.0f, 0.5f, 1.0f);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		cubeMesh.bind();
		spriteShader.bind();
		mapTexture.bind();
		spriteShader.uniformMat4("model", Mat4.scale(50.0f, 30.0f, 0.0f));
		
		cubeMesh.draw();
		
		cubeMesh.unBind();
	}
}
