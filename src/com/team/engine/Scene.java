package com.team.engine;

import java.util.ArrayList;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.team.engine.gameobject.GameObject;
import com.team.engine.gameobject.GameObject2D;
import com.team.engine.rendering.Cubemap;
import com.team.engine.rendering.DirectionalLight;
import com.team.engine.rendering.ModelBuilder;
import com.team.engine.rendering.PointLight;
import com.team.engine.rendering.Shader;
import com.team.engine.rendering.Tessellator;
import com.team.engine.rendering.Texture;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

import static com.team.engine.Globals.*;
import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Vector3f;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Scene is meant to make life easier by taking care of all the object handling, lights, and rendering on it's own.
 * Currently this is only designed for 3D. You MUST call setupPhysics if you want there to be physics. If you have a game object
 * that uses bullet physics and setupPhysics was not called you will likely crash.
 */
public class Scene implements ContactListener {
	public ArrayList<PointLight> lights = new ArrayList<>();
	public ArrayList<GameObject> objects = new ArrayList<>();
	public ArrayList<GameObject2D> objects2D = new ArrayList<>();
	
	public ArrayList<GameObject> objectsWaiting = new ArrayList<>();
	public ArrayList<GameObject2D> objects2DWaiting = new ArrayList<>();
	
	public ArrayList<GameObject2D> objects2DDelete = new ArrayList<>();
	public ArrayList<GameObject> objectsDelete = new ArrayList<>();
	
	public DirectionalLight sun;
	public DiscreteDynamicsWorld world;
	
	public World world2D;
	
	public Vec3 ambient = vec3(0.3f, 0.3f, 0.3f);
	/** The skybox that will be automatically rendered in the background. 
	 * Can be null to use background color instead */
	public Cubemap skybox = null;
	public Cubemap irradiance = null;
	
	public boolean debug = false;
	
	/** Background color if skybox is null */
	public Vec3 skyColor = vec3(0.0f, 0.0f, 0.0f);
	public Texture backgroundImage;
	
	public Scene() {
		sun = new DirectionalLight(vec3(-1.0f, -1.0f, 0.2f), vec3(2.0f, 2.0f, 2.0f), Settings.ENABLE_SHADOWS, 30, Settings.SHADOW_RESOLUTION);
		
		loadShader("color");
	}

	/**
	 * Initializes the dynamicsWorld for this scene
	 */
	public void setupPhysics() {
		//setup bullet
		BroadphaseInterface broadphase = new DbvtBroadphase();
		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		// set the gravity of our world
		world.setGravity(new Vector3f(0, -9.8f, 0));
		
		//setup jbox2D
		world2D = new World(new Vector2(0, -9.8f));	
		world2D.setContactListener(this);
	}
	
	public void setGravity(Vec2 grav) {
		world2D.setGravity(new Vector2(grav.x, grav.y));
	}
	
	public void render(Camera cam) {
		glDepthMask(false);
		if (backgroundImage != null) {
			getShader("framebuffer").bind();
			backgroundImage.bind();
			Engine.framebufferMesh.draw();
		}
		
		for (GameObject obj: objects) {
			obj.render(this, cam);
		}
		for (GameObject2D obj: objects2D) {
			obj.render(this, cam);
		}
		
		Shader s = getShader("light");
		s.bind();
		
		for (PointLight light : lights) {
			s.uniformMat4("model", mat4().translate(light.position).scale(0.2f));
			s.uniformVec3("lightColor", light.color);
			Engine.cubeMesh.draw();
		}
		
		//debug drawing
		if (debug)
			debugDraw2D();
		
		glDepthMask(true);
	}
	
	private void debugDraw2D() {
		glLineWidth(3);
		Shader s = getShader("color");
		s.bind();
		s.uniformMat4("model", mat4());
		s.uniformVec3("color", vec3(1, 0, 0));
		
		Tessellator ts = Engine.tessellator;
		ts.begin(GL_LINES);
		
		Body b = world2D.getBodyList();
		do {
			Vector2 pos = b.getTransform().p;
			Mat4 bodySpace = mat4().translate(vec3(b.getPosition().x, b.getPosition().y, 1)).rotate(vec4(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(b.getAngle())));
			
			float i = 0.05f;
			
			//draw the center of the body
			ts.vertex(pos.x-i, pos.y+i , 0, 0, 0, 1);
			ts.vertex(pos.x+i, pos.y+i, 0, 0, 0, 1);
			
			ts.vertex(pos.x-i, pos.y-i , 0, 0, 0, 1);
			ts.vertex(pos.x+i, pos.y-i, 0, 0, 0, 1);
			
			ts.vertex(pos.x-i, pos.y+i , 0, 0, 0, 1);
			ts.vertex(pos.x-i, pos.y-i, 0, 0, 0, 1);
			
			ts.vertex(pos.x+i, pos.y+i , 0, 0, 0, 1);
			ts.vertex(pos.x+i, pos.y-i, 0, 0, 0, 1);
			
			Fixture f = b.getFixtureList();
			do {
				Shape shape = f.m_shape;
				
				//draw raw polygons
				if (shape.getType() == ShapeType.POLYGON) {
					PolygonShape pShape = (PolygonShape)shape;
					
					Vector2[] vertices = pShape.m_vertices;
					
					boolean first = true;
					for (int j = 0; j < pShape.m_count; j++) {
						Vector2 v = vertices[j];
						Vec4 vOut = bodySpace.multiply(vec4(v.x, v.y, 0, 1));
						
						
						ts.vertex(vOut.x, vOut.y, 0, 1, 0, 0);
						if (!first)
							//if not first then we need to repeat the same vertex for the next line
							ts.vertex(vOut.x, vOut.y, 0, 1, 0, 0);
						
						first = false;
					}
					//the first vertex needs to be pushed again to close the loop
					Vec4 vOut = bodySpace.multiply(vec4(vertices[0].x, vertices[0].y, 0, 1));
					ts.vertex(vOut.x, vOut.y, 0, 1, 0, 0);
				}
				
				//draw circle
				if (shape.getType() == ShapeType.CIRCLE) {
					float radius = shape.m_radius;
					
					for (int j = 0; j < 20; j++) {
						Vec2 localVertex = vec2(0, radius).rotate((j / 20f) * 360);
						Vec4 vertex = bodySpace.multiply(vec4(localVertex.x, localVertex.y, 0, 1));
						ts.vertex(vertex.x, vertex.y, 0, 1, 0, 0);
						
						Vec2 localVertex2 = vec2(0, radius).rotate(((j + 2) / 20f) * 360);
						Vec4 vertex2 = bodySpace.multiply(vec4(localVertex2.x, localVertex2.y, 0, 1));
						ts.vertex(vertex2.x, vertex2.y, 0, 1, 0, 0);
					}
					//draw line to center
					Vec2 localVertex = vec2(0, radius);
					Vec4 vertex = bodySpace.multiply(vec4(localVertex.x, localVertex.y, 0, 1));
					ts.vertex(vertex.x, vertex.y, 0, 1, 0, 0);
					
					Vec2 localVertex2 = vec2();
					Vec4 vertex2 = bodySpace.multiply(vec4(localVertex2.x, localVertex2.y, 0, 1));
					ts.vertex(vertex2.x, vertex2.y, 0, 1, 0, 0);
				}
				
			} while ((f = f.getNext()) != null);
			
		} while ((b = b.getNext()) != null);
		ts.end();
	}
	
	public void update() {
		//remove all objects pending to be deleted from the scene
		for (GameObject obj: objectsDelete) {
			objects.remove(obj);
		}
		
		for (GameObject2D obj: objects2DDelete) {
			objects2D.remove(obj);
			world2D.destroyBody(obj.body);
		}
		
		//add all pending objects to the scene
		for (GameObject obj: objectsWaiting) {
			objects.add(obj);
		}
		objectsWaiting.clear();
		
		for (GameObject2D obj: objects2DWaiting) {
			objects2D.add(obj);
		}
		objects2DWaiting.clear();
		
		for (GameObject obj: objects) {
			obj.update();
		}
		
		for (GameObject2D obj: objects2D) {
			obj.update();
		}
		
		world2D.step(Engine.deltaTime, 4, 4);
		world.stepSimulation(Engine.deltaTime, 10);
	}
	
	public void renderShadow(Shader s) {
		for (GameObject obj: objects) {
			obj.renderShadow(s);
		}
	}

	public void add(GameObject o) {
		o.init(this);
		objectsWaiting.add(o);
	}
	
	public void add(GameObject2D o) {
		o.init(this);
		objects2DWaiting.add(o);
	}

	public void add(PointLight light) {
		lights.add(light);
	}
	
	public void delete(GameObject2D obj) {
		objects2DDelete.add(obj);
	}
	
	public void delete(GameObject obj) {
		objectsDelete.add(obj);
	}
	
	public void killWithTag(String tag) {
		for (GameObject2D obj: objects2D) {
			if (obj.tag.equals(tag)) objects2DDelete.add(obj);
		}
	}

	@Override
	public void beginContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		if (bodyA.getUserData() instanceof GameObject2D) {
			((GameObject2D)bodyA.getUserData()).onContact(contact.getFixtureA(), (GameObject2D)contact.getFixtureB().getBody().getUserData());
		}
		
		Body bodyB = contact.getFixtureB().getBody();
		if (bodyB.getUserData() instanceof GameObject2D) {
			((GameObject2D)bodyB.getUserData()).onContact(contact.getFixtureB(), (GameObject2D)contact.getFixtureA().getBody().getUserData());
		}
	}

	@Override
	public void endContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		if (bodyA.getUserData() instanceof GameObject2D) {
			((GameObject2D)bodyA.getUserData()).endContact(contact.getFixtureA(), (GameObject2D)contact.getFixtureB().getBody().getUserData());
		}
		
		Body bodyB = contact.getFixtureB().getBody();
		if (bodyB.getUserData() instanceof GameObject2D) {
			((GameObject2D)bodyB.getUserData()).endContact(contact.getFixtureB(), (GameObject2D)contact.getFixtureA().getBody().getUserData());
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
}
