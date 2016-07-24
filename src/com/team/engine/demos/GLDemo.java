package com.team.engine.demos;

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
import com.team.engine.Cubemap;
import com.team.engine.Engine;
import com.team.engine.Mesh;
import com.team.engine.ObjLoader;
import com.team.engine.PointLight;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.Texture;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

/**
 * A demo showing off 3D rendering with openGL and lighting shaders.
 */
public class GLDemo extends Engine {
	private static Vec3 cubePositions[] = {
		new Vec3( 2.0f,  5.0f, -15.0f), 
		new Vec3(-1.5f, -2.2f, -2.5f),  
		new Vec3(-3.8f, -2.0f, -12.3f),  
		new Vec3( 2.4f, -0.4f, -3.5f),  
		new Vec3(-1.7f,  3.0f, -7.5f),  
		new Vec3( 1.3f, -2.0f, -2.5f),  
		new Vec3( 1.5f,  2.0f, -2.5f), 
		new Vec3( 1.5f,  0.2f, -1.5f), 
		new Vec3(-1.3f,  1.0f, -1.2f)  
	};
	
	private static PointLight lights[] = {
		new PointLight(new Vec3(-1, 0, -3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f),
		new PointLight(new Vec3(1, 0, -3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f),
		new PointLight(new Vec3(-1, 0, 3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f),
		new PointLight(new Vec3(1, 0, 3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f)
	};
	
	private static RigidBody rigidBody;
	public static DiscreteDynamicsWorld dynamicsWorld;
	
	private Shader standardShader;
	private Shader lightShader;
	private Texture containerTexture;
	private Texture containerSpecTexture;
	private Mesh cubeMesh;
	private Mesh objMesh;
	
	public static void main(String[] args) {
		new GLDemo().initialize(false);
	}

	@Override
	public void setupGame() {
		//Load all our shaders and textures from disk.
		containerTexture = new Texture("resources/textures/container2.png");
		containerSpecTexture = new Texture("resources/textures/container2_specular.png");
		
		
		standardShader = new Shader("standard");
		lightShader = new Shader("light");
		this.ambient = new Vec3(0.6f, 0.6f, 0.7f);
		
		this.setFramebuffer(new Shader("hdr"));
		
		//Create the cube mesh object from the primitive.
		cubeMesh = new Mesh(Primitives.cube(1.0f));
		objMesh = ObjLoader.loadFile("resources/monkey.obj");
		
		
		this.skybox = new Cubemap(new String[] {
				"right.jpg",
				"left.jpg",
				"top.jpg",
				"bottom.jpg",
				"back.jpg",
				"front.jpg"
		});
		
		setupPhysics();
	}

	@Override
	public void tick() {
		//tell bullet to tick
		dynamicsWorld.stepSimulation(1 / 100.f, 10);
	}

	@Override
	public void render() {
		//Bind two textures in different indexes so the shader has both.
		containerTexture.bind(0);
		containerSpecTexture.bind(1);
		this.skybox.bind(2);
		
		//The transform of the falling cube.
		Transform trans = new Transform();
		rigidBody.getMotionState().getWorldTransform(trans);
		
		//Bind our shader.
		standardShader.bind();
		
		//Send material parameters and the global ambient as well.
		standardShader.uniformVec3("ambient", this.ambient);
		
		standardShader.uniformInt("material.diffuse", 0);
		standardShader.uniformVec3("material.diffuseColor", new Vec3(0.5, 0.5, 0.5));
		standardShader.uniformBool("material.diffuseTextured", true);
		
		standardShader.uniformInt("material.specular", 1);
		standardShader.uniformVec3("material.specularColor", new Vec3(0.5, 0.5, 0.5));
		standardShader.uniformBool("material.specularTextured", true);
		
		standardShader.uniformFloat("material.shininess", 64.0f);
		standardShader.uniformInt("skybox", 2);
		
		standardShader.uniformInt("pointLightCount", lights.length);
		for (int i = 0; i < lights.length; i++) {
			standardShader.uniformPointLight("pointLights[" + i + "]", lights[i]);
		}
		
		//draw the same mesh with different model matrices each time
		for(int i = 0; i < cubePositions.length; i++)
		{
		  Mat4 model = new Mat4().translate(cubePositions[i]);
		  float angle = 20.0f * i;
		  model = model.rotate(new Vec4(1.0f, 0.3f, 0.5f, angle));
		  standardShader.uniformMat4("model", model);

		  cubeMesh.draw();
		}
		standardShader.uniformMat4("model", new Mat4());
		objMesh.draw();
		
		//Draw the falling one.
		standardShader.uniformMat4("model", new Mat4().translate(new Vec3(trans.origin.x, trans.origin.y, trans.origin.z)));
		cubeMesh.draw();
		
		//Now we switch over to our light shader so we can draw each light. Notice we still don't need to unbind the cubemesh.
		lightShader.bind();
		
		for (PointLight light : lights) {
			lightShader.uniformMat4("model", new Mat4().translate(light.position).scale(0.2f));
			lightShader.uniformVec3("lightColor", light.color);
			cubeMesh.draw();
		}
		
		//Now we can unbind everything since we're done with the cube and the light shader.
		lightShader.unBind();
	}
	
	private static void setupPhysics() {
		//Not really sure what any of this does yet... I was mainly copying and pasting. What I do
		//know is that is sets up a good bullet simulation.
		
		BroadphaseInterface broadphase = new DbvtBroadphase();

		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		 
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher,broadphase,solver,collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0,-9.81f,0));
		
		CollisionShape boxCollisionShape = new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f));
		CollisionShape boxCollisionShape2 = new BoxShape(new Vector3f(100.0f, 0.5f, 100.0f));
		
		//the little box
		Transform trans = new Transform();
		//trans.origin.x = 0.48f;
		DefaultMotionState motionstate = new DefaultMotionState(trans);
		rigidBody = new RigidBody(1, motionstate, boxCollisionShape, new Vector3f(1,100,0));
		dynamicsWorld.addRigidBody(rigidBody);
		
		
		//the big box
		Transform trans2 = new Transform();
		trans2.origin.y = -5f;
		DefaultMotionState motionstate2 = new DefaultMotionState(trans2);
		RigidBody rigidBody2 = new RigidBody(0, motionstate2, boxCollisionShape2, new Vector3f(0, 0 ,0));
		dynamicsWorld.addRigidBody(rigidBody2);
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		//Send our exposure uniform to the post processing shader.
		shader.uniformFloat("exposure", 2.0f);
	}

	@Override
	public void kill() {
		
	}
}
