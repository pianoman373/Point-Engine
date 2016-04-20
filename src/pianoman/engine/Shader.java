package pianoman.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import pianoman.engine.vecmath.Mat4;
import pianoman.engine.vecmath.Vec3;
import pianoman.engine.vecmath.Vec4;

/**
 * Main class for handling shaders and using them as objects.
 */
public class Shader {
	public int id;
	
	/**
	 * creates a new shader object (we're using objects not static methods here) for you to bind later when rendering.
	 * The filename corresponds to Constants.RESOURCE_PATH + "/shaders/" + filename + shader extension.
	 */
	public Shader(String filename) {
		int vertexShader;
		vertexShader = glCreateShader(GL_VERTEX_SHADER);
		
		glShaderSource(vertexShader, read(Constants.RESOURCE_PATH + "/shaders/" + filename + ".vsh"));
		glCompileShader(vertexShader);
		check(vertexShader);
		
		int fragmentShader;
		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		
		glShaderSource(fragmentShader, read(Constants.RESOURCE_PATH + "/shaders/" + filename + ".fsh"));
		glCompileShader(fragmentShader);
		check(fragmentShader);
		
		id = glCreateProgram();
		glAttachShader(id, vertexShader);
		glAttachShader(id, fragmentShader);
		glLinkProgram(id);
		
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}
	
	/**
	 * binds the shader for use. Do this just before rendering. And then after binding you set uniforms.
	 */
	public void bind() {
		glUseProgram(this.id);
		this.uniformMat4("view", Engine.view);
		this.uniformMat4("projection", Engine.projection);
	}
	
	/**
	 * This one could technically be static, but it just makes code prettier.
	 */
	public void unBind() {
		glUseProgram(0);
	}
	
	/**
	 * Translates a Vec4 object into the glsl uniform.
	 */
	public void uniformVec4(String name, Vec4 value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniform4f(vertexColorLocation, value.x, value.y, value.z, value.w);
	}
	
	/**
	 * Translates a Vec3 object into the glsl uniform.
	 */
	public void uniformVec3(String name, Vec3 value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniform3f(vertexColorLocation, value.x, value.y, value.z);
	}
	
	/**
	 * Sends a float to the glsl uniform.
	 */
	public void uniformFloat(String name, float value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniform1f(vertexColorLocation, value);
	}
	
	/**
	 * Sends an int to the glsl uniform.
	 */
	public void uniformInt(String name, int value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniform1i(vertexColorLocation, value);
	}
	
	/**
	 * Translates a Mat4 object into the glsl uniform.
	 */
	public void uniformMat4(String name, Mat4 value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniformMatrix4fv(vertexColorLocation, false, value.getBuffer());
	}
	
	//TODO: Maybe we could do something like ISerializable, but for uniforms instead of hardcoding the objects in here.
	public void uniformPointLight(String name, PointLight value) {
		this.uniformVec3(name + ".position", value.position);
		this.uniformVec3(name + ".color", value.color);
		this.uniformFloat(name + ".linear", value.linear);
		this.uniformFloat(name + ".quadradic", value.quadric);
	}
	
	public void uniformDirectionalLight(String name, DirectionalLight value) {
		this.uniformVec3(name + ".direction", value.direction);
		this.uniformVec3(name + ".color", value.color);
	}
	
	private static void check(int shader) {
		int success;
		success = glGetShaderi(shader, GL_COMPILE_STATUS);
		if (success != GL_TRUE) {
			throw new RuntimeException(glGetShaderInfoLog(shader));
		}
	}
	
	/**
	 * This actually returns one big string of a file's contents.
	 */
	private static String read(String path) {
		String shader = "";
		try {
			BufferedReader reader = Files.newBufferedReader(Paths.get(path), Charset.defaultCharset());
			String line = null;
			while ((line = reader.readLine()) != null) {
				shader += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return shader;
	}
}
