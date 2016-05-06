package com.team.engine;

import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
import com.team.engine.vecmath.Vec4;

public class Transform {
	public Vec3 position;
	public Vec3 scale;
	public Vec3 rotation;
	
	public Transform() {
		this.position = new Vec3();
		this.scale = new Vec3(1, 1, 1);
		this.rotation = new Vec3(0, 0, 0);
	}
	
	public Mat4 getMatrix() {
		Mat4 mat = new Mat4().translate(position);
		mat = mat.rotate(new Vec4(0, 1, 0, rotation.y));
		mat = mat.rotate(new Vec4(1, 0, 0, rotation.x));
		mat = mat.rotate(new Vec4(0, 0, 1, rotation.z));
		mat = mat.scale(scale);
		
		return mat;
	}
}
