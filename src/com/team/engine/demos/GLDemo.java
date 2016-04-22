package com.team.engine.demos;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import java.util.Random;

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
		new PointLight(new Vec3(-1, 0, -3), new Vec3(1f, 1f, 1f), 0.09f, 0.032f),
		new PointLight(new Vec3(3.0f, 1.0f, -4.0f), new Vec3(1f, 0f, 0f), 0.09f, 0.032f),
		new PointLight(new Vec3(-1.0f, -3.0f, 4.0f), new Vec3(1f, 0.5f, 0f), 0.09f, 0.032f),
		new PointLight(new Vec3(5.0f, 1.0f, 2.0f), new Vec3(0f, 0.5f, 1f), 0.09f, 0.032f),
		new PointLight(new Vec3(1.0f, 1.0f, -15.0f), new Vec3(0.5f, 1.0f, 0.5f), 0.09f, 0.032f)
	};
	
	private static RigidBody rigidBody;
	public static DiscreteDynamicsWorld dynamicsWorld;
	
	private Shader standardShader;
	private Shader lightShader;
	private Texture containerTexture;
	private Texture containerSpecTexture;
	private Texture brickTexture;
	private Mesh cubeMesh;
	private Mesh planeMesh;
	private Vec3 lightPos = new Vec3(0.0f, 1.0f, 0.0f);
	
	public static void main(String[] args) {
		new GLDemo().initialize(false);
	}

	@Override
	public void setupGame() {
		//Load all our shaders and textures from disk.
		containerTexture = new Texture("resources/textures/container2.png");
		containerSpecTexture = new Texture("resources/textures/container2_specular.png");
		brickTexture = new Texture("resources/textures/brickwall.jpg");
		standardShader = new Shader("standard");
		lightShader = new Shader("light");
		
		this.background = new Vec3(0.1f, 0.1f, 0.1f);
		this.ambient = new Vec3(0.1f, 0.1f, 0.1f);
		
		//Create the cube mesh object with our vertices.
		cubeMesh = new Mesh(Primitives.cube(1.0f));
		planeMesh = new Mesh(Primitives.plane(20.0f));
		
		setupPhysics();
	}

	@Override
	public void tick() {
		//Move the light around and tell bullet to tick.
		lightPos.x = (float) (Math.sin(glfwGetTime()) * 10);
		lightPos.z = (float) (Math.cos(glfwGetTime()) * 5);
		dynamicsWorld.stepSimulation(1 / 100.f, 10);
	}

	@Override
	public void render() {
		//Bind two textures in different indexes so the shader has both.
		containerTexture.bind(0);
		containerSpecTexture.bind(1);
		
		//The transform of the falling cube.
		Transform trans = new Transform();
		rigidBody.getMotionState().getWorldTransform(trans);
		
		//Bind our shader.
		standardShader.bind();
		
		//Send material parameters and the global ambient as well.
		standardShader.uniformVec3("ambient", this.ambient);
		standardShader.uniformInt("material.diffuse", 0);
		standardShader.uniformInt("material.specular", 1);
		standardShader.uniformFloat("material.shininess", 16.0f);
		
		//Our shader currently only has one variable for directional lights. It's not like we would use a lot anyways
		//unless we're on tatooine.
		DirectionalLight dirLight = new DirectionalLight(new Vec3(-0.2f, -1.0f, -0.3f), new Vec3(1f, 0.9f, 0.8f));
		standardShader.uniformDirectionalLight("dirLight", dirLight);
		
		//Our shader currently only has 2 spaces for point lights hardcoded in.
		standardShader.uniformInt("pointLightCount", lights.length);
		for (int i = 0; i < lights.length; i++) {
			standardShader.uniformPointLight("pointLights[" + i + "]", lights[i]);
		}
		
		//Bind the mesh and then draw it 10 times but with different model uniforms.
		cubeMesh.bind();
		for(int i = 0; i < cubePositions.length; i++)
		{
		  Mat4 model = Mat4.translate(cubePositions[i].x, cubePositions[i].y, cubePositions[i].z);
		  float angle = 20.0f * i;
		  model = model.multiply(Mat4.rotate(angle, 1.0f, 0.3f, 0.5f));
		  standardShader.uniformMat4("model", model);

		  cubeMesh.draw();
		}
		
		//Draw the falling one.
		standardShader.uniformMat4("model", Mat4.translate(trans.origin.x, trans.origin.y, trans.origin.z));
		cubeMesh.draw();
		
		//Now we switch over to our light shader so we can draw each light. Notice we still don't need to unbind the cubemesh.
		lightShader.bind();
		
		for (PointLight light : lights) {
			lightShader.uniformMat4("model", Mat4.translate(light.position.x, light.position.y, light.position.z).multiply(Mat4.scale(0.2f, 0.2f, 0.2f)));
			lightShader.uniformVec3("lightColor", light.color);
			cubeMesh.draw();
		}
		
		//Now we can unbind everything since we're done with the cube and the light shader.
		lightShader.unBind();
		cubeMesh.unBind();
		
		planeMesh.bind();
		//Draw the big cube in the middle with a new mesh.
		
		//Bind our shader.
		/*standardShader.bind();
		
		brickTexture.bind(0);
		containerSpecTexture.unBind(1);
		
		//Send material parameters and the global ambient as well.
		standardShader.uniformVec3("ambient", this.ambient);
		standardShader.uniformInt("material.diffuse", 0);
		standardShader.uniformInt("material.specular", 1);
		standardShader.uniformFloat("material.shininess", 16.0f);
		standardShader.uniformMat4("model", Mat4.translate(0, -5, 0).multiply(Mat4.scale(100, 1, 100)));
		planeMesh.draw();
		planeMesh.unBind();*/
		
		
		//some old debug rendering
		/*GL11.glMatrixMode(GL_MODELVIEW);
		GL11.glLoadMatrixf(view.getBuffer());
		
		GL11.glMatrixMode(GL_PROJECTION);
		GL11.glLoadMatrixf(projection.getBuffer());
		
		GL11.glBegin(GL_LINES);
		GL11.glVertex3f(ray_start.x, ray_start.y, ray_start.z);
		GL11.glVertex3f(ray_end.x, ray_end.y, ray_end.z);
		GL11.glEnd();*/
		
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
}
