package com.team.engine.vecmath;

import java.nio.FloatBuffer;

import org.jbox2d.common.Vector2;
import org.lwjgl.BufferUtils;

public class Vec2 {

    public float x;
    public float y;

    /**
     * Creates a default 2-tuple vector with all values set to 0.
     */
    public Vec2() {
        this.x = 0f;
        this.y = 0f;
    }

    /**
     * Creates a vector out of another vector (box2D's vector type).
     */
    public Vec2(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    /**
     * Creates a 2-tuple vector with specified values.
     */
    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Calculates the squared length of the vector.
     */
    public float lengthSquared() {
        return x * x + y * y;
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
    public Vec2 normalize() {
        float length = length();
        return divide(length);
    }

    /**
     * Adds this vector to another vector.
     */
    public Vec2 add(Vec2 other) {
        float x = this.x + other.x;
        float y = this.y + other.y;
        return new Vec2(x, y);
    }

    public Vec2 multiply(Vec2 other) {
        float x = this.x * other.x;
        float y = this.y * other.y;
        return new Vec2(x, y);
    }

    /**
     * Negates this vector.
     */
    public Vec2 negate() {
        return scale(-1f);
    }

    /**
     * Subtracts this vector from another vector.
     */
    public Vec2 subtract(Vec2 other) {
        return this.add(other.negate());
    }

    /**
     * Multiplies a vector by a scalar.
     */
    public Vec2 scale(float scalar) {
        float x = this.x * scalar;
        float y = this.y * scalar;
        return new Vec2(x, y);
    }

    /**
     * Divides a vector by a scalar.
     */
    public Vec2 divide(float scalar) {
        return scale(1f / scalar);
    }

    /**
     * Calculates the dot product of this vector with another vector.
     */
    public float dot(Vec2 other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * Calculates a linear interpolation between this vector with another
     * vector.
     */
    public Vec2 lerp(Vec2 other, float alpha) {
        return this.scale(1f - alpha).add(other.scale(alpha));
    }

    /**
     * Returns the Buffer representation of this vector.
     */
    public FloatBuffer getBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(2);
        buffer.put(x).put(y);
        buffer.flip();
        return buffer;
    }
}
