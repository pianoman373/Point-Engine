package com.team.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.team.engine.vecmath.Vec2i;

public class Texture {
	public int id;
	public Vec2i dimensions;
	
	public Texture(String path) {
		this(path, false);
	}
	
	public Texture(int id, Vec2i dimensions) {
		this.id = id;
		this.dimensions = dimensions;
	}
	
	/**
	 * Creates a new texture object that can be bound later, from the specified path.
	 * 
	 * It is advised to use the texture management system in Engine rather than handle texture objects yourself.
	 */
	public Texture(String path, boolean pixelated) {
		id = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, id);
	
		RawImage img = getRawImage(Constants.RESOURCE_PATH + path);
		
		dimensions = new Vec2i(img.width, img.height);
	
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, img.width, img.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, img.data);
	
		glGenerateMipmap(GL_TEXTURE_2D);
		
		if (pixelated) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		}
		else {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		}
		
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	/**
	 * Binds the texture for rendering in the first texture location.
	 */
	public void bind() {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	/**
	 * Binds the texture for rendering in the specified texture location. Multiple textures can be sent
	 * to the same shader if they are in different locations.
	 */
	public void bind(int num) {
		glActiveTexture(GL_TEXTURE0 + num);
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public static void unBind(int num) {
		glActiveTexture(GL_TEXTURE0 + num);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	/**
	 * All the IO stuff here.
	 */
	public static RawImage getRawImage(String path) {
		BufferedImage image;
		try {
			InputStream in = new FileInputStream(path);
			image = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	
		//AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
		//transform.translate(0, -image.getHeight());
		//AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		//image = operation.filter(image, null);
	
		int width = image.getWidth();
		int height = image.getHeight();

		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
	
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				/* Pixel as RGBA: 0xAARRGGBB */
				int pixel = pixels[y * width + x];

				/* Red component 0xAARRGGBB >> (4 * 4) = 0x0000AARR */
				buffer.put((byte) ((pixel >> 16) & 0xFF));

				/* Green component 0xAARRGGBB >> (2 * 4) = 0x00AARRGG */
				buffer.put((byte) ((pixel >> 8) & 0xFF));

				/* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
				buffer.put((byte) (pixel & 0xFF));

				/* Alpha component 0xAARRGGBB >> (6 * 4) = 0x000000AA */
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		buffer.flip();
		
		return new RawImage(buffer, width, height);
	}
}

/**
 * Don't you wish java had multiple return values? I do, that way this class wouldn't have to exist.
 */
class RawImage {
	public ByteBuffer data;
	public int width;
	public int height;
	
	public RawImage(ByteBuffer data, int width, int height) {
		this.data = data;
		this.width = width;
		this.height = height;
	}
}
