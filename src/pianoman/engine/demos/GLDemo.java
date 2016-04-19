package pianoman.engine.demos;

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

import pianoman.engine.DirectionalLight;
import pianoman.engine.Engine;
import pianoman.engine.Mat4;
import pianoman.engine.Mesh;
import pianoman.engine.PointLight;
import pianoman.engine.Primitives;
import pianoman.engine.Shader;
import pianoman.engine.Texture;
import pianoman.engine.vecmath.Vec3;

public class GLDemo extends Engine {
	private static Vec3 cubePositions[] = {
			  new Vec3( 0.0f,  0.0f,  0.0f), 
			  new Vec3( 2.0f,  5.0f, -15.0f), 
			  new Vec3(-1.5f, -2.2f, -2.5f),  
			  new Vec3(-3.8f, -2.0f, -12.3f),  
			  new Vec3( 2.4f, -0.4f, -3.5f),  
			  new Vec3(-1.7f,  3.0f, -7.5f),  
			  new Vec3( 1.3f, -2.0f, -2.5f),  
			  new Vec3( 1.5f,  2.0f, -2.5f), 
			  new Vec3( 1.5f,  0.2f, -1.5f), 
			  new Vec3(-1.3f,  1.0f, -1.5f)  
	};
	
	private static RigidBody rigidBody;
	public static DiscreteDynamicsWorld dynamicsWorld;
	
	private Shader standardShader;
	private Shader lightShader;
	private Texture containerTexture;
	private Texture containerSpecTexture;
	private Mesh cubeMesh;
	private Vec3 lightPos = new Vec3(0.0f, 1.0f, 0.0f);
	
	public static void main(String[] args) {
		new GLDemo().initialize();
	}

	@Override
	public void setupGame() {
		containerTexture = new Texture("resources/textures/container2.png");
		containerSpecTexture = new Texture("resources/textures/container2_specular.png");
		standardShader = new Shader("standard");
		lightShader = new Shader("light");
		
		cubeMesh = new Mesh(Primitives.cubeVertices);
		
		setupPhysics();
	}

	@Override
	public void tick() {
		lightPos.x = (float) (Math.sin(glfwGetTime()) * 10);
		lightPos.z = (float) (Math.cos(glfwGetTime()) * 5);
		dynamicsWorld.stepSimulation(1 / 100.f, 10);
	}

	@Override
	public void render() {
		containerTexture.bind(0);
		containerSpecTexture.bind(1);
		
		Transform trans = new Transform();
		rigidBody.getMotionState().getWorldTransform(trans);
		
		standardShader.bind();
		
		standardShader.uniformVec3("ambient", ambient);
		standardShader.uniformInt("material.diffuse", 0);
		standardShader.uniformInt("material.specular", 1);
		standardShader.uniformFloat("material.shininess", 16.0f);
		
		DirectionalLight dirLight = new DirectionalLight(new Vec3(-0.2f, -1.0f, -0.3f), new Vec3(1f, 0.9f, 0.8f));
		standardShader.uniformDirectionalLight("dirLight", dirLight);
		
		standardShader.uniformPointLight("pointLights[0]", new PointLight(lightPos, new Vec3(1f, 1f, 1f), 0.09f, 0.032f));
		standardShader.uniformPointLight("pointLights[1]", new PointLight(new Vec3(3.0f, 1.0f, -4.0f), new Vec3(1f, 0f, 0f), 0.09f, 0.032f));
		
		cubeMesh.bind();
		for(int i = 0; i < 10; i++)
		{
		  Mat4 model = Mat4.translate(cubePositions[i].x, cubePositions[i].y, cubePositions[i].z);
		  float angle = 20.0f * i;
		  model = model.multiply(Mat4.rotate(angle, 1.0f, 0.3f, 0.5f));
		  standardShader.uniformMat4("model", model);

		  cubeMesh.draw();
		}
		
		standardShader.uniformMat4("model", Mat4.translate(0, -5, 0).multiply(Mat4.scale(3, 3, 3)));
		cubeMesh.draw();
		
		standardShader.uniformMat4("model", Mat4.translate(trans.origin.x, trans.origin.y, trans.origin.z));
		cubeMesh.draw();
		
		lightShader.bind();
		lightShader.uniformMat4("model", Mat4.translate(lightPos.x, lightPos.y, lightPos.z).multiply(Mat4.scale(0.2f, 0.2f, 0.2f)));
		lightShader.uniformVec3("lightColor", new Vec3(1, 1, 1));
		cubeMesh.draw();
		
		lightShader.uniformMat4("model", Mat4.translate(3.0f, 1.0f, -4.0f).multiply(Mat4.scale(0.2f, 0.2f, 0.2f)));
		lightShader.uniformVec3("lightColor", new Vec3(1, 0, 0));
		cubeMesh.draw();
		
		lightShader.unBind();
		
		cubeMesh.unBind();
		
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
		BroadphaseInterface broadphase = new DbvtBroadphase();

		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		 
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher,broadphase,solver,collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0,-9.81f,0));
		
		CollisionShape boxCollisionShape = new BoxShape(new Vector3f(1.0f, 1.0f, 1.0f));
		CollisionShape boxCollisionShape2 = new BoxShape(new Vector3f(3.0f, 3.0f, 3.0f));
		DefaultMotionState motionstate = new DefaultMotionState(new Transform());
		
		rigidBody = new RigidBody(1, motionstate, boxCollisionShape, new Vector3f(0,0,0));
		dynamicsWorld.addRigidBody(rigidBody);
		
		
		
		Transform trans = new Transform();
		trans.origin.y = -4f;
		DefaultMotionState motionstate2 = new DefaultMotionState(trans);
		RigidBody rigidBody2 = new RigidBody(0, motionstate2, boxCollisionShape2, new Vector3f(0, 0 ,0));
		dynamicsWorld.addRigidBody(rigidBody2);
	}
}
