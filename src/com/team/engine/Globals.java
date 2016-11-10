package com.team.engine;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.jbox2d.common.Vector2;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

public class Globals {
	public static void print(Object obj) {
		System.out.println(obj);
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
	//vec4
	//-------------------------
	public static Mat4 mat4() {
		return mat4();
	}
	
	public static Mat4 mat4(Matrix4f mat) {
		return new Mat4(mat);
	}
	
	public static Mat4 mat4(Vec4 col1, Vec4 col2, Vec4 col3, Vec4 col4) {
		return new Mat4(col1, col2, col3, col4);
	}
}
