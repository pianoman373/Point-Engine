package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

public class Shader {
	public int id;

	/**
	 * creates a new shader object (we're using objects not static methods here) for you to bind later when rendering.
	 * The filename corresponds to Constants.RESOURCE_PATH + filename + shader extension.
	 *
	 * It is advised to use the shader management system in Engine rather than handle shader objects yourself.
	 */
	public Shader(String filename) {
		int vertexShader;
		vertexShader = glCreateShader(GL_VERTEX_SHADER);

		glShaderSource(vertexShader, read(Constants.RESOURCE_PATH + filename + ".vsh"));
		glCompileShader(vertexShader);
		check(vertexShader);

		int fragmentShader;
		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

		glShaderSource(fragmentShader, read(Constants.RESOURCE_PATH + filename + ".fsh"));
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
		this.uniformMat4("view", Engine.camera.getView());
		this.uniformVec3("eyePos", Engine.camera.getPosition());
		this.uniformMat4("projection", Engine.camera.getProjection());
		if (!Engine.is2d) {
			this.uniformScene(Engine.scene);
		}
	}

	/**
	 * You should never need to use this, but just in case.
	 */
	public static void unBind() {
		glUseProgram(0);
	}

	public void uniformVec4(String name, Vec4 value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniform4f(vertexColorLocation, value.x, value.y, value.z, value.w);
	}

	public void uniformVec3(String name, Vec3 value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniform3f(vertexColorLocation, value.x, value.y, value.z);
	}

	public void uniformVec2(String name, Vec2 value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniform2f(vertexColorLocation, value.x, value.y);
	}

	public void uniformFloat(String name, float value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniform1f(vertexColorLocation, value);
	}

	public void uniformInt(String name, int value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniform1i(vertexColorLocation, value);
	}

	public void uniformBool(String name, boolean value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);

		if (value) {
			glUniform1i(vertexColorLocation, 1);
		}
		else {
			glUniform1i(vertexColorLocation, 0);
		}
	}

	public void uniformMat4(String name, Mat4 value) {
		int vertexColorLocation = glGetUniformLocation(this.id, name);
		glUniformMatrix4fv(vertexColorLocation, false, value.getBuffer());
	}

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

	public void uniformScene(Scene scene) {
		this.uniformInt("pointLightCount", scene.lights.size());
		for (int i = 0; i < scene.lights.size(); i++) {
			this.uniformPointLight("pointLights[" + i + "]", scene.lights.get(i));
		}

		this.uniformVec3("dirLight.direction", scene.sun.direction);
		this.uniformVec3("dirLight.color", scene.sun.color);

		this.uniformBool("dirLight.hasShadowMap", scene.sun.castShadow);
		if (scene.sun.castShadow) {
			scene.sun.shadowBuffer.tex[0].bind(6);
			this.uniformInt("dirLight.shadowMap", 6);
			this.uniformMat4("lightSpace", scene.sun.getShadowMat());
		}

		this.uniformVec3("ambient", scene.ambient);

		if (scene.skybox != null) {
			scene.skybox.bind(4);
			this.uniformInt("skybox", 4);
			this.uniformBool("hasSkybox", true);
		}
		else {
			this.uniformBool("hasSkybox", false);
		}
		
		if (scene.irradiance != null) {
			scene.irradiance.bind(5);
			this.uniformInt("irradiance", 5);
			this.uniformBool("hasIrradiance", true);
		}
		else {
			this.uniformInt("irradiance", 4);
			this.uniformBool("hasIrradiance", false);
		}
	}

	public void uniformMaterial(Material mat) {
		if (mat.albedoTex != null)
			Engine.getTexture(mat.albedoTex).bind(0);
		this.uniformInt("material.albedoTex",  0);
		this.uniformVec3("material.albedo", mat.albedo);
		this.uniformBool("material.albedoTextured", mat.albedoTextured);

		if (mat.roughnessTex != null)
			Engine.getTexture(mat.roughnessTex).bind(1);
		this.uniformInt("material.roughnessTex", 1);
		this.uniformFloat("material.roughness", mat.roughness);
		this.uniformBool("material.roughnessTextured", mat.roughnessTextured);

		if (Graphics.ENABLE_NORMAL_MAPPING) {
			if (mat.normalTex != null)
				Engine.getTexture(mat.normalTex).bind(2);
			this.uniformInt("material.normalTex", 2);
			this.uniformBool("material.normalTextured", mat.normalTextured);
		}

		if (mat.metallicTex != null)
			Engine.getTexture(mat.metallicTex).bind(3);
		this.uniformInt("material.metallicTex", 3);
		this.uniformFloat("material.metallic", mat.metallic);
		this.uniformBool("material.metallicTextured", mat.metallicTextured);
	}

	private static void check(int shader) {
		int success;
		success = glGetShaderi(shader, GL_COMPILE_STATUS);
		if (success != GL_TRUE) {
			throw new RuntimeException(glGetShaderInfoLog(shader, 4096));
		}
	}

	/**
	 * This actually returns one big string of a file's contents.
	 */
	private static String read(String path) {
		System.out.println("loading shader: " + path);
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
