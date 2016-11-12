package com.team.engine.vecmath;

import java.nio.FloatBuffer;

import org.jbox2d.common.Vector2;
import org.lwjgl.BufferUtils;

import javax.vecmath.Vector3f;

public class Vec3 {

    public float x;
    public float y;
    public float z;

    /**
     * Creates a default 3-tuple vector with all values set to 0.
     */
    public Vec3() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }

    /**
     * Creates a 3-tuple vector with specified values.
     */
    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec4 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    /**
     * Creates a Vec3 out of another Vec2 (box2D's vec2) and a specified z.
     */
    public Vec3(Vector2 v, float z) {
    	this.x = v.x;
        this.y = v.y;
        this.z = z;
    }

    public Vec3(Vector3f v) {
    	this.x = v.x;
    	this.y = v.y;
    	this.z = v.z;
    }

    public Vec3(double x, double y, double z) {
    	this((float)x, (float)y, (float)z);
    }

    public Vector3f asv3f() {
        return new Vector3f(x, y, z);
    }

    /**
     * Rotates around the X axis.
     */
    public Vec3 rotatePitch(float pitch)
    {
        float cosine = (float)Math.cos(Math.toRadians(pitch));
        float sine = (float)Math.sin(Math.toRadians(pitch));
        float newX = this.x;
        float newY = this.y * cosine + this.z * sine;
        float newZ = this.z * cosine - this.y * sine;
        return new Vec3(newX, newY, newZ);
    }

    /**
     * Rotates around the Y axis.
     */
    public Vec3 rotateYaw(float yaw)
    {
        float cosine = (float)Math.cos(Math.toRadians(yaw));
        float sine = (float)Math.sin(Math.toRadians(yaw));
        float newX = this.x * cosine + this.z * sine;
        float newY = this.y;
        float newZ = this.z * cosine - this.x * sine;
        return new Vec3(newX, newY, newZ);
    }

    /**
     * Rotates around the X axis.
     */
    public Vec3 rotateRoll(float roll)
    {
        float cosine = (float)Math.cos(Math.toRadians(roll));
        float sine = (float)Math.sin(Math.toRadians(roll));
        float newX = this.x * cosine + this.y * sine;
        float newY = this.y * cosine + this.x * sine;
        float newZ = this.z;
        return new Vec3(newX, newY, newZ);
    }

    /**
     * Calculates the squared length of the vector.
     */
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Calculates the length of the vector.
     */
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector.
     */
    public Vec3 normalize() {
        float length = length();
        return divide(length);
    }

    /**
     * Adds this vector to another vector.
     */
    public Vec3 add(Vec3 other) {
        float x = this.x + other.x;
        float y = this.y + other.y;
        float z = this.z + other.z;
        return new Vec3(x, y, z);
    }
    /**
     * Multiplies this vector to another vector.
     */
    public Vec3 multiply(Vec3 other) {
        float x = this.x * other.x;
        float y = this.y * other.y;
        float z = this.z * other.z;
        return new Vec3(x, y, z);
    }

    /**
     * Multiplies this vector to a scalar.
     */
    public Vec3 multiply(float scalar) {
        float x = this.x * scalar;
        float y = this.y * scalar;
        float z = this.z * scalar;
        return new Vec3(x, y, z);
    }

    /**
     * Negates this vector.
     */
    public Vec3 negate() {
        return multiply(-1f);
    }

    /**
     * Subtracts this vector from another vector.
     */
    public Vec3 subtract(Vec3 other) {
        return this.add(other.negate());
    }

    /**
     * Divides a vector by a scalar.
     */
    public Vec3 divide(float scalar) {
        return multiply(1f / scalar);
    }

    /**
     * Calculates the dot product of this vector with another vector.
     */
    public float dot(Vec3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    /**
     * Calculates the dot product of this vector with another vector.
     */
    public Vec3 cross(Vec3 other) {
        float x = this.y * other.z - this.z * other.y;
        float y = this.z * other.x - this.x * other.z;
        float z = this.x * other.y - this.y * other.x;
        return new Vec3(x, y, z);
    }

    /**
     * Calculates a linear interpolation between this vector with another
     * vector.
     */
    public Vec3 lerp(Vec3 other, float alpha) {
        return this.multiply(1f - alpha).add(other.multiply(alpha));
    }

    /**
     * Returns the Buffer representation of this vector.
     */
    public FloatBuffer getBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        buffer.put(x).put(y).put(z);
        buffer.flip();
        return buffer;
    }

    public String toString() {
    	return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
