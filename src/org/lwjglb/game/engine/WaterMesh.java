package org.lwjglb.game.engine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglb.game.engine.utils.Utils;

public class WaterMesh {
	private static final float START_X = -5f;
	private static final float START_Z = -5f;
	private final int vaoId;
	private final int vertexVboId;
	private final int colorVboId;
	private final int indexVboId;

	private final int indexCount;

	public WaterMesh(int vertX, int vertY, float height) {
		float xStep = Math.abs(START_X * 2) / (vertX - 1);
		float zStep = Math.abs(START_Z * 2) / (vertY - 1);

		List<Float> vertList = new ArrayList<>();
		List<Integer> indList = new ArrayList<>();

		for (int y = 0; y < vertY; y++) {
			for (int x = 0; x < vertX; x++) {
				// scale from [-1, 1] to [minY, maxY]

				vertList.add(START_X + x * xStep);
				vertList.add(height);
				vertList.add(START_Z + y * zStep);

				// Create indices
				if (x < vertX - 1 && y < vertY - 1) {
					int leftTop = y * vertX + x;
					int leftBottom = (y + 1) * vertX + x;
					int rightBottom = (y + 1) * vertX + x + 1;
					int rightTop = y * vertX + x + 1;

					indList.add(leftTop);
					indList.add(leftBottom);
					indList.add(rightTop);

					indList.add(rightTop);
					indList.add(leftBottom);
					indList.add(rightBottom);
				}
			}
		}
		float[] colors = new float[vertList.size()];
		for (int i = 0; i < vertList.size(); i += 3) {
			colors[i] = 45 / 255f;
			colors[i + 1] = 174 / 255f;
			colors[i + 2] = 255 / 255f;
		}

		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		vertexVboId = GL15.glGenBuffers();
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertList.size());
		verticesBuffer.put(Utils.listToArray(vertList)).flip();
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
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indList.size());
		indicesBuffer.put(indList.stream().mapToInt((i) -> i).toArray()).flip();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		indexCount = indList.size();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public int getVboId() {
		return vertexVboId;
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

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}

	public void render() {
		GL30.glBindVertexArray(getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
//		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
}
