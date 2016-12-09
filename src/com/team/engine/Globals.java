package com.team.engine;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.jbox2d.common.Vector2;

import com.team.engine.rendering.Shader;
import com.team.engine.rendering.Texture;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

/**
 * This class contains a bunch of helper methods designed to be imported statically.
 */
public class Globals {
	public static void print(Object obj) {
		System.out.println(obj);
	}
	
	//-------------------------
	//resource management
	//-------------------------
	
	/**
	 * This will load a shader from the specified path on disk into memory.
	 * This does NOT bind the shader or even return the shader. Use getShader for that.
	 */
	public static void loadShader(String path) {
		Shader s = new Shader("shaders/" + path);
		Engine.shaders.put(path, s);
	}
	
	public static void loadShaderBuiltin(String path) {
		Shader s = new Shader("builtin/shaders/" + path);
		Engine.shaders.put(path, s);
	}
	
	/**
	 * This will return a shader object from memory ONLY if it has been loaded with loadShader.
	 * The object will only be returned, you will have to bind it yourself.
	 */
	public static Shader getShader(String path) {
		return Engine.shaders.get(path);
	}
	
	/**
	 * This will load audio from the specified path on disk into memory.
	 * This does NOT play or even return the audio. Use getAudio for that.
	 */
	public static void loadAudio(String path) {
		Audio a = new Audio("audio/" + path);
		Engine.sounds.put(path, a);
	}
	
	/**
	 * This will return an audio object from memory ONLY if it has been loaded with loadSAudio.
	 * The object will only be returned, you will have to play it yourself.
	 */
	public static Audio getAudio(String path) {
		return Engine.sounds.get(path);
	}

	/**
	 * This will load a texture from the specified path on disk into memory.
	 * This does NOT bind the texture or even return the texture. Use getTexture for that.
	 */
	public static void loadTexture(String path, boolean pixelated, boolean srgb) {
		Texture s = new Texture("textures/" + path, pixelated, srgb);
		Engine.textures.put(path, s);
	}
	
	public static void loadTexture(String path) {
		loadTexture(path, false, false);
	}
	
	/**
	 * This will return a texture object from memory ONLY if it has been loaded with loadTexture.
	 * The object will only be returned, you will have to bind it yourself.
	 */
	public static Texture getTexture(String path) {
		return Engine.textures.get(path);
	}
	
	//-------------------------
	//vec2
	//-------------------------
	
	public static Vec2 vec2() {
		return new Vec2(0f, 0f);
	}
	
	public static Vec2 vec2(float x, float y) {
		return new Vec2(x, y);
	}
	
	public static Vec2 vec2(Vector2 v) {
		return new Vec2(v.x, v.y);
	}
	
	
	//-------------------------
	//vec3
	//-------------------------
	
	public static Vec3 vec3() {
		return new Vec3(0f, 0f, 0f);
	}
	
	public static Vec3 vec3(float x, float y, float z) {
		return new Vec3(x, y, z);
	}
	
	public static Vec3 vec3(Vec4 v) {
		return new Vec3(v.x, v.y, v.z);
	}
	
	public static Vec3 vec3(Vec2 v, float z) {
		return new Vec3(v.x, v.y, z);
	}
	
	public static Vec3 vec3(Vector2 v, float z) {
		return new Vec3(v.x, v.y, z);
	}
	
	public static Vec3 vec3(Vector3f v) {
		return new Vec3(v.x, v.y, v.z);
	}
	
	public static Vec3 vec3(double x, double y, double z) {
		return new Vec3((float)x, (float)y, (float)z);
	}
	
	//-------------------------
	//vec4
	//-------------------------
	
	public static Vec4 vec4() {
		return new Vec4(0f, 0f, 0f, 0f);
	}
	
	public static Vec4 vec4(float x, float y, float z, float w) {
		return new Vec4(x, y, z, w);
	}
	
	//-------------------------
	//mat4
	//-------------------------
	public static Mat4 mat4() {
		return new Mat4();
	}
	
	public static Mat4 mat4(Matrix4f mat) {
		return new Mat4(mat);
	}
	
	public static Mat4 mat4(Vec4 col1, Vec4 col2, Vec4 col3, Vec4 col4) {
		return new Mat4(col1, col2, col3, col4);
	}
}
