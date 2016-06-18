package org.lwjglb.game.engine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Mesh {
	private final int vaoId;
	private final int vertexVboId;
	private final int colorVboId;
	private final int normalVboId;
	private final int indexVboId;

	private final int indexCount;

	public Mesh(float[] positions, float[] colors, float[] normals, int[] indices) {
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		vertexVboId = GL15.glGenBuffers();
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(positions.length);
		verticesBuffer.put(positions).flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

		colorVboId = GL15.glGenBuffers();
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colors.length);
		colorBuffer.put(colors).flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);

		normalVboId = GL15.glGenBuffers();
		FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(normals.length);
		normalBuffer.put(normals).flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);

		indexVboId = GL15.glGenBuffers();
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices).flip();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		indexCount = indices.length;

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public Mesh(float[] positions, int[] indices, float[] colors) {
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		vertexVboId = GL15.glGenBuffers();
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(positions.length);
		verticesBuffer.put(positions).flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

		colorVboId = GL15.glGenBuffers();
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colors.length);
		colorBuffer.put(colors).flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);

		indexVboId = GL15.glGenBuffers();
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices).flip();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		indexCount = indices.length;

		normalVboId = -1;

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public int getVaoId() {
		return vaoId;
	}

	public void cleanup() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);

		// Delete the vertex vbo
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vertexVboId);

		GL15.glDeleteBuffers(colorVboId);

		// Delete the index vbo
		GL15.glDeleteBuffers(indexVboId);

		// Delete the color vbo
		GL15.glDeleteBuffers(normalVboId);

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}

	public void render() {
		GL30.glBindVertexArray(getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
}
