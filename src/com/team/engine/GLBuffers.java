package com.team.engine;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

/**
 * These are just some handy functions for turning arrays into buffers.
 */
public class GLBuffers {
	public static FloatBuffer StaticBuffer(float[] contents) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(contents.length);
		for (float i : contents) {
			buffer.put(i);
		}
		buffer.flip();
		
		return buffer;
	}
	
	public static IntBuffer StaticBuffer(int[] contents) {
		IntBuffer buffer = BufferUtils.createIntBuffer(contents.length);
		for (int i : contents) {
			buffer.put(i);
		}
		buffer.flip();
		
		return buffer;
	}
	
	public static ByteBuffer StaticBuffer(byte[] contents) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(contents.length);
		for (byte i : contents) {
			buffer.put(i);
		}
		buffer.flip();
		
		return buffer;
	}
	
	public static float[] toArray(FloatBuffer array) {
		float[] ret = new float[array.capacity()];
		
		for (int i = 0; i < ret.length; i++) {
			ret[i] = array.get(i);
		}
		
		return ret;
	}
	
	public static int[] toArray(IntBuffer array) {
		int[] ret = new int[array.capacity()];
		
		for (int i = 0; i < ret.length; i++) {
			ret[i] = array.get(i);
		}
		
		return ret;
	}
}
