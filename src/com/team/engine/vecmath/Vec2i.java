package com.team.engine.vecmath;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Vec2i {

    public int x;
    public int y;

    /**
     * Creates a default 3-tuple vector with all values set to 0.
     */
    public Vec2i() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Creates a 3-tuple vector with specified values.
     */
    public Vec2i(int x, int y) {
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
    public int length() {
        return (int) Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector.
     */
    public Vec2i normalize() {
        int length = length();
        return divide(length);
    }

    /**
     * Adds this vector to another vector.
     */
    public Vec2i add(Vec2i other) {
        int x = this.x + other.x;
        int y = this.y + other.y;
        return new Vec2i(x, y);
    }
    
    public Vec2i multiply(Vec2i other) {
        int x = this.x * other.x;
        int y = this.y * other.y;
        return new Vec2i(x, y);
    }

    /**
     * Negates this vector.
     */
    public Vec2i negate() {
        return scale(-1);
    }

    /**
     * Subtracts this vector from another vector.
     */
    public Vec2i subtract(Vec2i other) {
        return this.add(other.negate());
    }

    /**
     * Multiplies a vector by a scalar.
     */
    public Vec2i scale(int scalar) {
        int x = this.x * scalar;
        int y = this.y * scalar;
        return new Vec2i(x, y);
    }

    /**
     * Divides a vector by a scalar.
     */
    public Vec2i divide(int scalar) {
        return scale(1 / scalar);
    }

    /**
     * Calculates the dot product of this vector with another vector.
     */
    public float dot(Vec2i other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * Calculates a linear interpolation between this vector with another
     * vector.
     */
    public Vec2i lerp(Vec2i other, int alpha) {
        return this.scale(1 - alpha).add(other.scale(alpha));
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