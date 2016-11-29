package com.team.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;

public class Util {
	/**
	 * Returns true if the specified file exists.
	 */
	public static boolean fileExists(String file) {
		return Files.exists(Paths.get(file), LinkOption.NOFOLLOW_LINKS);
	}
	
	public static ByteBuffer readFile(String resource) {
		ByteBuffer buffer;

		Path path = Paths.get(resource);
		if ( Files.isReadable(path) ) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
				while ( fc.read(buffer) != -1 ) ;
				
				buffer.flip();
				return buffer;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static String readFileString(String resource) {
		String source = "";
		try {
			BufferedReader reader = Files.newBufferedReader(Paths.get(Settings.RESOURCE_PATH + resource), Charset.defaultCharset());
			String line = null;
			while ((line = reader.readLine()) != null) {
				source += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return source;
	}
	
	public static FloatBuffer toBuffer(float[] contents) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(contents.length);
		for (float i : contents) {
			buffer.put(i);
		}
		buffer.flip();
		
		return buffer;
	}
	
	public static IntBuffer toBuffer(int[] contents) {
		IntBuffer buffer = BufferUtils.createIntBuffer(contents.length);
		for (int i : contents) {
			buffer.put(i);
		}
		buffer.flip();
		
		return buffer;
	}
	
	public static ByteBuffer toBuffer(byte[] contents) {
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
