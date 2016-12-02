package com.team.engine.demos;

import static com.team.engine.Globals.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.swing.Timer;

import org.lwjgl.BufferUtils;

import com.team.engine.AbstractGame;
import com.team.engine.Engine;
import com.team.engine.SpaceCamera;
import com.team.engine.Scene;
import com.team.engine.rendering.PointLight;
import com.team.engine.rendering.Shader;
import com.team.engine.vecmath.Vec3;

/**
 * A demo that can simulate orbits, clustering, and gravitational interactions.
 * 
 * I'm not even gonna document this one too much mainly because it hardly uses the engine at all.
 */
public class NbodyDemo extends AbstractGame implements ActionListener{
	public static void main(String[] args) {
		Engine.start(false, false, new NbodyDemo());
	}
	
	private static final boolean POINT_TO_POINT_GRAVITY = false;
	private static final float POINT_TO_POINT_STRENGTH = 0.1f;
	private static final float POINT_VELOCITY = 0.09f;
	private static final float SUN_STRENGTH = 10f;
	private static final int POINT_COUNT = 200000;
	private static final int SPREAD = 12;
	private static final int MIN_SPREAD = 20;
	
	private static final float VERTICAL_SIZE = 0f;
	private static final float VERTICAL_SHAPE = 5f;
	private static final float DRAG = 0.0000f;
	private static final float REDSHIFT_RANGE = 0.2f;
	private static final int TIMESTEP = 0;
	
	private static int VAO;
	private static int VBO;
	
	private static Scene scene;
	
	private static Vec3 points[] = new Vec3[POINT_COUNT];
	private static Vec3 colors[] = new Vec3[POINT_COUNT];
	private static Vec3 velocities[] = new Vec3[POINT_COUNT];
	
	private static Timer t;
	private static boolean readyToRender = false;

	@Override
	public void init() {
		SpaceCamera.WASD_SENSITIVITY = 25.0f;
		
		loadShader("point");
		loadShader("light");
		
		Random rand = new Random();
		
		for (int i = 0; i < points.length; i++) {
			
			float angle = rand.nextFloat() * 360;
			
			float x = (float)Math.sin(Math.toRadians(angle)) * ((rand.nextFloat() * SPREAD) + MIN_SPREAD);
			float z = (float)Math.cos(Math.toRadians(angle)) * ((rand.nextFloat() * SPREAD) + MIN_SPREAD);
			
			float y = ((rand.nextFloat() * VERTICAL_SIZE) - VERTICAL_SIZE/2) / (float)Math.sqrt(x*x + z*z) * VERTICAL_SHAPE;
			
			points[i] = vec3(x, y, z);
			colors[i] = vec3(Math.random(), Math.random(), Math.random());
		}
		
		for (int i = 0; i < points.length; i++) {
			velocities[i] = (points[i].normalize()).cross(vec3(0, 1, 0)).multiply(POINT_VELOCITY);
		}
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(points.length * 6);
		
		for (Vec3 i : points) {
			vertexBuffer.put(i.x);
			vertexBuffer.put(i.y);
			vertexBuffer.put(i.z);
		}
		vertexBuffer.flip();
		
		
		
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		
		VBO = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STREAM_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glPointSize(2);
		
		glBindVertexArray(0);
		
		scene = new Scene();
		scene.add(new PointLight(vec3(0, 0, 0), vec3(0.5f, 0.5f, 1f), 1f, 0.032f));
		scene.add(new PointLight(vec3(5, 5, 20), vec3(1f, 0.5f, 0.5f), 1f, 0.032f));
		//scene.add(new PointLight(vec3(30, 3, 3), vec3(1f, 0.8f, 0.0f), 0.09f, 0.032f));
		//scene.add(new PointLight(vec3(20, 5, 30), vec3(1f, 0.8f, 0.0f), 0.09f, 0.032f));
		
		t = new Timer(TIMESTEP, this);
		t.start();
	}
	
	private void updateVBO() {
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(points.length * 6);
		
		for (int i = 0; i < POINT_COUNT; i++) {
			vertexBuffer.put(points[i].x);
			vertexBuffer.put(points[i].y);
			vertexBuffer.put(points[i].z);
			
			vertexBuffer.put(colors[i].x);
			vertexBuffer.put(colors[i].y);
			vertexBuffer.put(colors[i].z);
		}
		vertexBuffer.flip();
		
		glBindVertexArray(VAO);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STREAM_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glBindVertexArray(0);
	}

	@Override
	public void update() {
		if (readyToRender) {
			readyToRender = false;
			updateVBO();
		}
	}
	
	public Vec3 getInfluenceAt(Vec3 pos) {
		Vec3 total = vec3();
		
		if (POINT_TO_POINT_GRAVITY) {
			for (Vec3 i : points) {
				if (i.x != pos.x && i.y != pos.y && i.z != pos.z) {
					Vec3 toPoint = pos.subtract(i).negate().normalize();
					float distanceToPoint = pos.subtract(i).length();
					
					if (distanceToPoint > 0.1f) {
						total = total.add(toPoint.multiply(POINT_TO_POINT_STRENGTH / (distanceToPoint * distanceToPoint) * Engine.deltaTime));
					}
				}
			}
		}
		
		for (PointLight i : scene.lights) {
			Vec3 toPoint = pos.subtract(i.position).negate().normalize();
			float distanceToPoint = pos.subtract(i.position).length();
			
			if (distanceToPoint > 0.5f) {
				total = total.add(toPoint.multiply(SUN_STRENGTH / (distanceToPoint * distanceToPoint) * Engine.deltaTime));
			}
		}
		
		/*Vec3 toPoint = pos.negate().normalize();
		float distanceToPoint = pos.length();
		total = total.add(toPoint.multiply(SUN_STRENGTH / (distanceToPoint * distanceToPoint) * Engine.instance.deltaTime));*/
		
		
		return total;
	}

	@Override
	public void render() {
		scene.render(Engine.camera);
		
		Shader s = getShader("point");
		s.bind();
		s.uniformMat4("model", mat4());
		s.uniformVec3("lightColor", vec3(1.0f, 1.0f, 1.0f));
		
		glBindVertexArray(VAO);
		glDrawArrays(GL_POINTS, 0, points.length * 3);
		
		glBindVertexArray(0);
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		//Send our exposure uniform to the post processing shader.
		shader.uniformFloat("exposure", 2.0f);
	}

	@Override
	public void kill() {
		t.stop();
	}

	@Override
	public void renderShadow(Shader s) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (true) {
			for (int t = 0; t < 1; t++) {
				for (int i = 0; i < points.length; i++) {
					Vec3 pos = points[i];
					Vec3 vel = velocities[i];
					
					velocities[i] = velocities[i].add(getInfluenceAt(pos)).multiply(1.0f - DRAG);
					
					points[i] = pos.add(vel);
					
					float speed = vel.length();
					
					if (speed > REDSHIFT_RANGE) {
						speed = REDSHIFT_RANGE;
					}
					
					colors[i] = vec3(0.1f, 0.6f, 1.0f).normalize().lerp(vec3(1.0f, 0.1f, 0.1f).normalize(), speed / REDSHIFT_RANGE);
				}
			}
//			
//			if (Mouse.isButtonDown(1) && accum > 0f) {
//				PointLight p = new PointLight(Engine.instance.camera.getPosition(), vec3(1.0f, 1.0f, 2.0f), 100f, 0.032f);
//				scene.add(p);
//	
//				accum = 0;
//			}
			
			//updateVBO();
			readyToRender = true;
		}
	}
}
