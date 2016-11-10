package com.team.engine.vecmath;

import static com.team.engine.Globals.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Vec4 {

    public float x;
    public float y;
    public float z;
    public float w;

    /**
     * Creates a default 4-tuple vector with all values set to 0.
     */
    public Vec4() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
        this.w = 0f;
    }

    /**
     * Creates a 4-tuple vector with specified values.
     */
    public Vec4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Calculates the squared length of the vector.
     */
    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
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
    public Vec4 normalize() {
        float length = length();
        return divide(length);
    }

    /**
     * Adds this vector to another vector.
     */
    public Vec4 add(Vec4 other) {
        float x = this.x + other.x;
        float y = this.y + other.y;
        float z = this.z + other.z;
        float w = this.w + other.w;
        return new Vec4(x, y, z, w);
    }

    /**
     * Negates this vector.
     */
    public Vec4 negate() {
        return scale(-1f);
    }

    /**
     * Subtracts this vector from another vector.
     */
    public Vec4 subtract(Vec4 other) {
        return this.add(other.negate());
    }

    /**
     * Multiplies a vector by a scalar.
     */
    public Vec4 scale(float scalar) {
        float x = this.x * scalar;
        float y = this.y * scalar;
        float z = this.z * scalar;
        float w = this.w * scalar;
        return new Vec4(x, y, z, w);
    }

    /**
     * Divides a vector by a scalar.
     */
    public Vec4 divide(float scalar) {
        return scale(1f / scalar);
    }

    /**
     * Calculates the dot product of this vector with another vector.
     */
    public float dot(Vec4 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
    }

    /**
     * Calculates a linear interpolation between this vector with another
     * vector.
     */
    public Vec4 lerp(Vec4 other, float alpha) {
        return this.scale(1f - alpha).add(other.scale(alpha));
    }

    /**
     * Returns the Buffer representation of this vector.
     */
    public FloatBuffer getBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
        buffer.put(x).put(y).put(z).put(w);
        buffer.flip();
        return buffer;
    }
}
