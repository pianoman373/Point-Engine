package pianoman.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
	private int VAO;
	private int EBO;
	private int VBO;
	
	private int length;
	
	/**
	 * Makes a mesh object from specified mesh data. Just like Texture and Shader, bind before rendering.
	 * 
	 * TODO: Currently positions, normals, and uv's are all in the verices array since OpenGL likes them in one big buffer.
	 * someday we should have them as seperate arrays and combine them in here.
	 */
	public Mesh(float[] vertices, int[] indices) {
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		
		EBO = glGenBuffers();
		VBO = glGenBuffers();
		
		length = vertices.length / 8;
		
		FloatBuffer vertexBuffer = GLBuffers.StaticBuffer(vertices);
		IntBuffer indexBuffer = GLBuffers.StaticBuffer(indices);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
		glEnableVertexAttribArray(2);
		
		glBindVertexArray(0);
	}
	
	/**
	 * Same as first constructor except without indices, don't use this yet.
	 */
	public Mesh(float[] vertices) {
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		
		EBO = glGenBuffers();
		VBO = glGenBuffers();
		
		length = vertices.length / 8;
		
		FloatBuffer vertexBuffer = GLBuffers.StaticBuffer(vertices);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
		glEnableVertexAttribArray(2);
		
		glBindVertexArray(0);
	}
	
	/**
	 * Binds the model. The usual workflow for rendering the same model multiple times would be to bind, enter position in uniforms,
	 * and then draw. Then to draw a new model, just re-enter a new position again. No need for unBinding after every draw (as long as you're drawing the same mesh).
	 */
	public void bind() {
		glBindVertexArray(VAO);
	}
	
	/**
	 * Same story as Shader.unbind()
	 */
	public void unBind() {
		glBindVertexArray(0);
	}
	
	public void draw() {
		glDrawArrays(GL_TRIANGLES, 0, length);
	}
}
