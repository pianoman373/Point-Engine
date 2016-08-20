package com.team.engine.demos;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;

import com.team.engine.Engine;
import com.team.engine.PointLight;
import com.team.engine.Shader;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A demo that can simulate orbits, clustering, and gravitational interactions.
 */
public class NbodyDemo extends Engine {
	private static PointLight lights[] = {
		new PointLight(new Vec3(0, 0, 0), new Vec3(1f, 0.8f, 0.0f), 0.09f, 0.032f)
	};
	
	public static void main(String[] args) {
		new NbodyDemo().initialize(false);
	}
	
	private static final boolean POINT_TO_POINT_GRAVITY = false;
	private static final float POINT_TO_POINT_STRENGTH = 0.11f;
	private static final float POINT_VELOCITY = 1f;
	private static final float SUN_STRENGTH = 10f;
	private static final int POINT_COUNT = 100000;
	private static final int SPREAD = 10;
	private static final int MIN_SPREAD = 5;
	private static final float VERTICAL_SIZE = 10f;
	private static final float VERTICAL_SHAPE = 5f;
	
	private static int VAO;
	private static int VBO;
	
	private static Vec3 points[] = new Vec3[POINT_COUNT];
	private static Vec3 velocities[] = new Vec3[POINT_COUNT];

	@Override
	public void setupGame() {
		loadShader("light");
		
		Random rand = new Random();
		
		for (int i = 0; i < points.length; i++) {
			
			float angle = rand.nextFloat() * 360;
			
			float x = (float)Math.sin(Math.toRadians(angle)) * ((rand.nextFloat() * SPREAD) + MIN_SPREAD);
			float z = (float)Math.cos(Math.toRadians(angle)) * ((rand.nextFloat() * SPREAD) + MIN_SPREAD);
			
			float y = ((rand.nextFloat() * VERTICAL_SIZE) - VERTICAL_SIZE/2) / (float)Math.sqrt(x*x + z*z) * VERTICAL_SHAPE;
			
			points[i] = new Vec3(x, y, z);
		}
		
		for (int i = 0; i < points.length; i++) {
			velocities[i] = (points[i].normalize()).cross(new Vec3(0, 1, 0)).multiply(POINT_VELOCITY);
		}
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(points.length * 3);
		
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
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glPointSize(2);
		
		glBindVertexArray(0);
	}
	
	private void updateVBO() {
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(points.length * 3);
		
		for (Vec3 i : points) {
			vertexBuffer.put(i.x);
			vertexBuffer.put(i.y);
			vertexBuffer.put(i.z);
		}
		vertexBuffer.flip();
		
		glBindVertexArray(VAO);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STREAM_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glBindVertexArray(0);
	}

	@Override
	public void tick() {
		for (int t = 0; t < 1; t++) {
		for (int i = 0; i < points.length; i++) {
			Vec3 pos = points[i];
			Vec3 vel = velocities[i];
			
			velocities[i] = velocities[i].add(getInfluenceAt(pos));
			
			points[i] = pos.add(vel.multiply(Engine.instance.deltaTime));
		}
		}
		
		updateVBO();
	}
	
	public Vec3 getInfluenceAt(Vec3 pos) {
		Vec3 total = new Vec3();
		
		if (POINT_TO_POINT_GRAVITY) {
			for (Vec3 i : points) {
				if (i.x != pos.x && i.y != pos.y && i.z != pos.z) {
					Vec3 toPoint = pos.subtract(i).negate().normalize();
					float distanceToPoint = pos.subtract(i).length();
					
					if (distanceToPoint > 0.1f) {
						total = total.add(toPoint.multiply(POINT_TO_POINT_STRENGTH / (distanceToPoint * distanceToPoint) * Engine.instance.deltaTime));
					}
				}
			}
		}
		
		Vec3 toPoint = pos.negate().normalize();
		float distanceToPoint = pos.length();
		total = total.add(toPoint.multiply(SUN_STRENGTH / (distanceToPoint * distanceToPoint) * Engine.instance.deltaTime));
		
		
		return total;
	}

	@Override
	public void render() {
		Shader s = getShader("light");
		s.bind();
		
		for (PointLight light : lights) {
			s.uniformMat4("model", new Mat4().translate(light.position).scale(0.2f));
			s.uniformVec3("lightColor", light.color);
			cubeMesh.draw();
		}
		
		s.uniformMat4("model", new Mat4());
		s.uniformVec3("lightColor", new Vec3(1.0f, 1.0f, 1.0f));
		
		glBindVertexArray(VAO);
		glDrawArrays(GL_POINTS, 0, points.length * 3);
		
		glBindVertexArray(0);
	}

	@Override
	public void postRenderUniforms(Shader shader) {}

	@Override
	public void kill() {
		
	}
}
