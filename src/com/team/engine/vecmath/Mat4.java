package com.team.engine.vecmath;

import static com.team.engine.Globals.*;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;

/**
 * This class represents a 4x4-Matrix. GLSL equivalent to mat4.
 *
 * @author Heiko Brumme
 */
public class Mat4 {

    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;

    /**
     * Creates a 4x4 identity matrix.
     */
    public Mat4() {
        setIdentity();
    }

    public Mat4(Matrix4f mat) {
      this.m00 = mat.m00;
      this.m10 = mat.m10;
      this.m20 = mat.m20;
      this.m30 = mat.m30;

      this.m01 = mat.m01;
      this.m11 = mat.m11;
      this.m21 = mat.m21;
      this.m31 = mat.m31;

      this.m02 = mat.m02;
      this.m12 = mat.m12;
      this.m22 = mat.m22;
      this.m32 = mat.m32;

      this.m03 = mat.m03;
      this.m13 = mat.m13;
      this.m23 = mat.m23;
      this.m33 = mat.m33;
    }

    /**
     * Creates a 4x4 matrix with specified columns.
     */
    public Mat4(Vec4 col1, Vec4 col2, Vec4 col3, Vec4 col4) {
        m00 = col1.x;
        m10 = col1.y;
        m20 = col1.z;
        m30 = col1.w;

        m01 = col2.x;
        m11 = col2.y;
        m21 = col2.z;
        m31 = col2.w;

        m02 = col3.x;
        m12 = col3.y;
        m22 = col3.z;
        m32 = col3.w;

        m03 = col4.x;
        m13 = col4.y;
        m23 = col4.z;
        m33 = col4.w;
    }

    /**
     * Sets this matrix to the identity matrix.
     */
    public final void setIdentity() {
        m00 = 1f;
        m11 = 1f;
        m22 = 1f;
        m33 = 1f;

        m01 = 0f;
        m02 = 0f;
        m03 = 0f;
        m10 = 0f;
        m12 = 0f;
        m13 = 0f;
        m20 = 0f;
        m21 = 0f;
        m23 = 0f;
        m30 = 0f;
        m31 = 0f;
        m32 = 0f;
    }

    /**
     * Adds this matrix to another matrix.
     */
    public Mat4 add(Mat4 other) {
        Mat4 result = new Mat4();

        result.m00 = this.m00 + other.m00;
        result.m10 = this.m10 + other.m10;
        result.m20 = this.m20 + other.m20;
        result.m30 = this.m30 + other.m30;

        result.m01 = this.m01 + other.m01;
        result.m11 = this.m11 + other.m11;
        result.m21 = this.m21 + other.m21;
        result.m31 = this.m31 + other.m31;

        result.m02 = this.m02 + other.m02;
        result.m12 = this.m12 + other.m12;
        result.m22 = this.m22 + other.m22;
        result.m32 = this.m32 + other.m32;

        result.m03 = this.m03 + other.m03;
        result.m13 = this.m13 + other.m13;
        result.m23 = this.m23 + other.m23;
        result.m33 = this.m33 + other.m33;

        return result;
    }

    /**
     * Negates this matrix.
     */
    public Mat4 negate() {
        return multiply(-1f);
    }

    /**
     * Subtracts this matrix from another matrix.
     */
    public Mat4 subtract(Mat4 other) {
        return this.add(other.negate());
    }

    /**
     * Multiplies this matrix with a scalar.
     */
    public Mat4 multiply(float scalar) {
        Mat4 result = new Mat4();

        result.m00 = this.m00 * scalar;
        result.m10 = this.m10 * scalar;
        result.m20 = this.m20 * scalar;
        result.m30 = this.m30 * scalar;

        result.m01 = this.m01 * scalar;
        result.m11 = this.m11 * scalar;
        result.m21 = this.m21 * scalar;
        result.m31 = this.m31 * scalar;

        result.m02 = this.m02 * scalar;
        result.m12 = this.m12 * scalar;
        result.m22 = this.m22 * scalar;
        result.m32 = this.m32 * scalar;

        result.m03 = this.m03 * scalar;
        result.m13 = this.m13 * scalar;
        result.m23 = this.m23 * scalar;
        result.m33 = this.m33 * scalar;

        return result;
    }

    /**
     * Multiplies this matrix to a vector.
     */
    public Vec4 multiply(Vec4 vector) {
        float x = this.m00 * vector.x + this.m01 * vector.y + this.m02 * vector.z + this.m03 * vector.w;
        float y = this.m10 * vector.x + this.m11 * vector.y + this.m12 * vector.z + this.m13 * vector.w;
        float z = this.m20 * vector.x + this.m21 * vector.y + this.m22 * vector.z + this.m23 * vector.w;
        float w = this.m30 * vector.x + this.m31 * vector.y + this.m32 * vector.z + this.m33 * vector.w;
        return vec4(x, y, z, w);
    }

    /**
     * Multiplies this matrix to another matrix.
     */
    public Mat4 multiply(Mat4 other) {
        Mat4 result = new Mat4();

        result.m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20 + this.m03 * other.m30;
        result.m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20 + this.m13 * other.m30;
        result.m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20 + this.m23 * other.m30;
        result.m30 = this.m30 * other.m00 + this.m31 * other.m10 + this.m32 * other.m20 + this.m33 * other.m30;

        result.m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21 + this.m03 * other.m31;
        result.m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21 + this.m13 * other.m31;
        result.m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21 + this.m23 * other.m31;
        result.m31 = this.m30 * other.m01 + this.m31 * other.m11 + this.m32 * other.m21 + this.m33 * other.m31;

        result.m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22 + this.m03 * other.m32;
        result.m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22 + this.m13 * other.m32;
        result.m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22 + this.m23 * other.m32;
        result.m32 = this.m30 * other.m02 + this.m31 * other.m12 + this.m32 * other.m22 + this.m33 * other.m32;

        result.m03 = this.m00 * other.m03 + this.m01 * other.m13 + this.m02 * other.m23 + this.m03 * other.m33;
        result.m13 = this.m10 * other.m03 + this.m11 * other.m13 + this.m12 * other.m23 + this.m13 * other.m33;
        result.m23 = this.m20 * other.m03 + this.m21 * other.m13 + this.m22 * other.m23 + this.m23 * other.m33;
        result.m33 = this.m30 * other.m03 + this.m31 * other.m13 + this.m32 * other.m23 + this.m33 * other.m33;

        return result;
    }

    /**
     * Transposes this matrix.
     */
    public Mat4 transpose() {
        Mat4 result = new Mat4();

        result.m00 = this.m00;
        result.m10 = this.m01;
        result.m20 = this.m02;
        result.m30 = this.m03;

        result.m01 = this.m10;
        result.m11 = this.m11;
        result.m21 = this.m12;
        result.m31 = this.m13;

        result.m02 = this.m20;
        result.m12 = this.m21;
        result.m22 = this.m22;
        result.m32 = this.m23;

        result.m03 = this.m30;
        result.m13 = this.m31;
        result.m23 = this.m32;
        result.m33 = this.m33;

        return result;
    }

    public static Mat4 LookAt(Vec3 eye, Vec3 center, Vec3 up) {
    		Vec3 forward = vec3(0, 0, -1);
    		Vec3 upVec = vec3(0, 1, 0);
    		Vec3 side = vec3(1, 0, 0);

    		forward.x = center.x - eye.x;
    		forward.y = center.y - eye.y;
    		forward.z = center.z - eye.z;

    		upVec.x = up.x;
    		upVec.y = up.y;
    		upVec.z = up.z;

    		forward = forward.normalize();

    		/* Side = forward x up */
    		side = forward.cross(upVec);
    		side = side.normalize();

    		/* Recompute up as: up = side x forward */
    		upVec = side.cross(forward);

    		Mat4 mat = new Mat4();
    		mat.m00 = side.x;
    		mat.m01 = side.y;
    		mat.m02 = side.z;

    		mat.m10 = upVec.x;
    		mat.m11 = upVec.y;
    		mat.m12 = upVec.z;

    		mat.m20 = -forward.x;
    		mat.m21 = -forward.y;
    		mat.m22 = -forward.z;

    		return mat.translate(vec3(-eye.x, -eye.y, -eye.z));
    }

    /**
     * Returns the Buffer representation of this vector.
     */
    public FloatBuffer getBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(m00).put(m10).put(m20).put(m30);
        buffer.put(m01).put(m11).put(m21).put(m31);
        buffer.put(m02).put(m12).put(m22).put(m32);
        buffer.put(m03).put(m13).put(m23).put(m33);
        buffer.flip();
        return buffer;
    }
    
    public float[] getArray() {
    	return new float[] {
    			m00, m10, m20, m30,
    			m01, m11, m21, m31,
    			m02, m12, m22, m32,
    			m03, m13, m23, m33
    	};
    }

    /**
     * Creates a orthographic projection matrix. Similar to
     * <code>glOrtho(left, right, bottom, top, near, far)</code>.
     */
    public static Mat4 orthographic(float left, float right, float bottom, float top, float near, float far) {
        Mat4 ortho = new Mat4();

        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        ortho.m00 = 2f / (right - left);
        ortho.m11 = 2f / (top - bottom);
        ortho.m22 = -2f / (far - near);
        ortho.m03 = tx;
        ortho.m13 = ty;
        ortho.m23 = tz;

        return ortho;
    }

    /**
     * Creates a perspective projection matrix. Similar to
     * <code>glFrustum(left, right, bottom, top, near, far)</code>.
     */
    public static Mat4 frustum(float left, float right, float bottom, float top, float near, float far) {
        Mat4 frustum = new Mat4();

        float a = (right + left) / (right - left);
        float b = (top + bottom) / (top - bottom);
        float c = -(far + near) / (far - near);
        float d = -(2f * far * near) / (far - near);

        frustum.m00 = (2f * near) / (right - left);
        frustum.m11 = (2f * near) / (top - bottom);
        frustum.m02 = a;
        frustum.m12 = b;
        frustum.m22 = c;
        frustum.m32 = -1f;
        frustum.m23 = d;
        frustum.m33 = 0f;

        return frustum;
    }

    /**
     * Creates a perspective projection matrix. Similar to
     * <code>gluPerspective(fovy, aspec, zNear, zFar)</code>.
     * positive
     * positive
     */
    public static Mat4 perspective(float fov, float aspect, float near, float far) {
        Mat4 perspective = new Mat4();

        float sine, cotangent, deltaZ;
        float radians = fov / 2 * (float)Math.PI / 180;

        deltaZ = far - near;
		sine = (float) Math.sin(radians);

        cotangent = (float) Math.cos(radians) / sine;

        perspective.m00 = cotangent / aspect;
        perspective.m11 = cotangent;
        perspective.m22 = -(far + near) / deltaZ;
        perspective.m32 = -1f;
        perspective.m23 = -2 * near * far / deltaZ;
        perspective.m33 = 0f;

        return perspective;
    }

    /**
     * Creates a translation matrix. Similar to
     * <code>glTranslate(x, y, z)</code>.
     */
    public Mat4 translate(Vec3 translate) {
        Mat4 translation = new Mat4();

        translation.m03 = translate.x;
        translation.m13 = translate.y;
        translation.m23 = translate.z;

        return this.multiply(translation);
    }

    /**
     * Creates a rotation matrix. Similar to
     * <code>glRotate(angle, x, y, z)</code>.
     */
    public Mat4 rotate(Vec4 rot) {
        Mat4 rotation = new Mat4();

        float c = (float) Math.cos(Math.toRadians(rot.w));
        float s = (float) Math.sin(Math.toRadians(rot.w));
        Vec3 vec = vec3(rot.x, rot.y, rot.z);
        if (vec.length() != 1f) {
            vec = vec.normalize();
            rot.x = vec.x;
            rot.y = vec.y;
            rot.z = vec.z;
        }

        rotation.m00 = vec.x * vec.x * (1f - c) + c;
        rotation.m10 = vec.y * vec.x * (1f - c) + vec.z * s;
        rotation.m20 = vec.x * vec.z * (1f - c) - vec.y * s;
        rotation.m01 = vec.x * vec.y * (1f - c) - vec.z * s;
        rotation.m11 = vec.y * vec.y * (1f - c) + c;
        rotation.m21 = vec.y * vec.z * (1f - c) + vec.x * s;
        rotation.m02 = vec.x * vec.z * (1f - c) + vec.y * s;
        rotation.m12 = vec.y * vec.z * (1f - c) - vec.x * s;
        rotation.m22 = vec.z * vec.z * (1f - c) + c;

        return this.multiply(rotation);
    }

    public Mat4 rotateX(float rot) {
    	return this.rotate(vec4(1, 0, 0, rot));
    }

    public Mat4 rotateY(float rot) {
    	return this.rotate(vec4(0, 1, 0, rot));
    }
    public Mat4 rotateZ(float rot) {
    	return this.rotate(vec4(0, 0, 1, rot));
    }

    /**
     * Creates a scaling matrix. Similar to <code>glScale(x, y, z)</code>.
     */
    public Mat4 scale(Vec3 scale) {
        Mat4 scaling = new Mat4();

        scaling.m00 = scale.x;
        scaling.m11 = scale.y;
        scaling.m22 = scale.z;

        return this.multiply(scaling);
    }

    public Mat4 scale(float scale) {
        Mat4 scaling = new Mat4();

        scaling.m00 = scale;
        scaling.m11 = scale;
        scaling.m22 = scale;

        return this.multiply(scaling);
    }

    private static float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20, float t21, float t22) {
    	return   t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20);
	}

    public float determinant() {
		float f =
			m00
				* ((m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32)
					- m13 * m22 * m31
					- m11 * m23 * m32
					- m12 * m21 * m33);
		f -= m01
			* ((m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32)
				- m13 * m22 * m30
				- m10 * m23 * m32
				- m12 * m20 * m33);
		f += m02
			* ((m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31)
				- m13 * m21 * m30
				- m10 * m23 * m31
				- m11 * m20 * m33);
		f -= m03
			* ((m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31)
				- m12 * m21 * m30
				- m10 * m22 * m31
				- m11 * m20 * m32);
		return f;
	}

    public Mat4 inverse() {
    	Mat4 src = this;
    	Mat4 dest = new Mat4();

		float determinant = src.determinant();

		if (determinant != 0) {
			/*
			 * m00 m01 m02 m03
			 * m10 m11 m12 m13
			 * m20 m21 m22 m23
			 * m30 m31 m32 m33
			 */
			float determinant_inv = 1f/determinant;

			// first row
			float t00 =  determinant3x3(src.m11, src.m12, src.m13, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33);
			float t01 = -determinant3x3(src.m10, src.m12, src.m13, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33);
			float t02 =  determinant3x3(src.m10, src.m11, src.m13, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33);
			float t03 = -determinant3x3(src.m10, src.m11, src.m12, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32);
			// second row
			float t10 = -determinant3x3(src.m01, src.m02, src.m03, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33);
			float t11 =  determinant3x3(src.m00, src.m02, src.m03, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33);
			float t12 = -determinant3x3(src.m00, src.m01, src.m03, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33);
			float t13 =  determinant3x3(src.m00, src.m01, src.m02, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32);
			// third row
			float t20 =  determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m31, src.m32, src.m33);
			float t21 = -determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m30, src.m32, src.m33);
			float t22 =  determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m30, src.m31, src.m33);
			float t23 = -determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m30, src.m31, src.m32);
			// fourth row
			float t30 = -determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m21, src.m22, src.m23);
			float t31 =  determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m20, src.m22, src.m23);
			float t32 = -determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m20, src.m21, src.m23);
			float t33 =  determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m20, src.m21, src.m22);

			// transpose and divide by the determinant
			dest.m00 = t00*determinant_inv;
			dest.m11 = t11*determinant_inv;
			dest.m22 = t22*determinant_inv;
			dest.m33 = t33*determinant_inv;
			dest.m01 = t10*determinant_inv;
			dest.m10 = t01*determinant_inv;
			dest.m20 = t02*determinant_inv;
			dest.m02 = t20*determinant_inv;
			dest.m12 = t21*determinant_inv;
			dest.m21 = t12*determinant_inv;
			dest.m03 = t30*determinant_inv;
			dest.m30 = t03*determinant_inv;
			dest.m13 = t31*determinant_inv;
			dest.m31 = t13*determinant_inv;
			dest.m32 = t23*determinant_inv;
			dest.m23 = t32*determinant_inv;
			return dest;
		} else
			return null;
	}
}
