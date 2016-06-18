package org.lwjglb.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjglb.game.engine.Mesh;
import org.lwjglb.game.engine.utils.SimplexNoise;
import org.lwjglb.game.engine.utils.Utils;

public class HeightMap extends GameModel {

	private static final float START_X = -0.5f;
	private static final float START_Z = -0.5f;
	private static final float REFLECTANCE = .1f;

	public HeightMap(float minY, float maxY, float persistence, int radius, float spikeness) {
		super(createMesh(minY, maxY, persistence, radius, spikeness), REFLECTANCE);
	}

	protected static Mesh createMesh(final float minY, final float maxY, final float persistence, final int radius,
			float spikeness) {
		SimplexNoise noise = new SimplexNoise(128, persistence, 2);// Utils.getRandom().nextInt());

		float xStep = Math.abs(START_X * 2) / (radius * 2 - 1);
		float zStep = Math.abs(START_Z * 2) / (radius * 2 - 1);

		List<Float> positions = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();

		for (int z = 0; z < radius * 2; z++) {
			for (int x = 0; x < radius * 2; x++) {
				// scale from [-1, 1] to [minY, maxY]
				float heightY = (float) ((noise.getNoise(x * xStep * spikeness, z * zStep * spikeness) + 1f) / 2
						* (maxY - minY) + minY);
				heightY = createCircle(heightY, ((float) x / radius) - 1, ((float) z / radius) - 1);

				positions.add(START_X + x * xStep);
				positions.add(heightY);
				positions.add(START_Z + z * zStep);
				positions.add(START_X + x * xStep);
				positions.add(heightY);
				positions.add(START_Z + z * zStep);
			}
		}
		for (int z = 0; z < radius * 2 - 1; z++) {
			for (int x = 0; x < radius * 2 - 1; x++) {
				int leftTop = z * radius * 2 + x;
				int leftBottom = (z + 1) * radius * 2 + x;
				int rightBottom = (z + 1) * radius * 2 + x + 1;
				int rightTop = z * radius * 2 + x + 1;

				indices.add(2 * leftTop);
				indices.add(2 * leftBottom);
				indices.add(2 * rightTop);

				indices.add(2 * rightTop + 1);
				indices.add(2 * leftBottom + 1);
				indices.add(2 * rightBottom + 1);
			}
		}

		float[] verticesArr = Utils.listToArray(positions);
		Color c = new Color(235, 192, 149);
		float[] colorArr = new float[positions.size()];
		for (int i = 0; i < colorArr.length; i += 3) {
			float brightness = (Utils.getRandom().nextFloat() - 0.5f) * 0.5f;
			colorArr[i] = (float) c.getRed() / 255f + brightness;
			colorArr[i + 1] = (float) c.getGreen() / 255f + brightness;
			colorArr[i + 2] = (float) c.getBlue() / 255f + brightness;
		}
		int[] indicesArr = indices.stream().mapToInt((i) -> i).toArray();

		float[] normalArr = calcNormals(verticesArr, radius * 2, radius * 2);

		return new Mesh(verticesArr, colorArr, normalArr, indicesArr);
	}

	/**
	 * Attenuates heights as they approach the edge
	 * 
	 * @param heightY
	 * @param x
	 *            [-1, 1]
	 * @param z
	 *            [-1, 1]
	 * @return
	 */
	private static float createCircle(float heightY, float x, float z) {
		float dist = new Vector2f(x, z).length();
		float attenuationDistance = 0.65f;
		if (dist > attenuationDistance) {
			heightY -= (dist - attenuationDistance) * (.45f * dist);
		}
		return heightY;
	}

	private static float[] calcNormals(float[] posArr, int width, int height) {
		Vector3f v0 = new Vector3f();
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		Vector3f v3 = new Vector3f();
		Vector3f v4 = new Vector3f();
		Vector3f v12 = new Vector3f();
		Vector3f v23 = new Vector3f();
		Vector3f v34 = new Vector3f();
		Vector3f v41 = new Vector3f();
		List<Float> normals = new ArrayList<>();
		Vector3f normal = new Vector3f();
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (row > 0 && row < height - 1 && col > 0 && col < width - 1) {
					int i0 = row * width * 3 + col * 3;
					v0.x = posArr[2 * i0];
					v0.y = posArr[2 * i0 + 1];
					v0.z = posArr[2 * i0 + 2];

					int i1 = row * width * 3 + (col - 1) * 3;
					v1.x = posArr[2 * (i1)];
					v1.y = posArr[2 * i1 + 1];
					v1.z = posArr[2 * i1 + 2];
					v1 = v1.sub(v0);

					int i2 = (row + 1) * width * 3 + col * 3;
					v2.x = posArr[2 * (i2)];
					v2.y = posArr[2 * i2 + 1];
					v2.z = posArr[2 * i2 + 2];
					v2 = v2.sub(v0);

					int i3 = (row) * width * 3 + (col + 1) * 3;
					v3.x = posArr[2 * i3];
					v3.y = posArr[2 * i3 + 1];
					v3.z = posArr[2 * i3 + 2];
					v3 = v3.sub(v0);

					int i4 = (row - 1) * width * 3 + col * 3;
					v4.x = posArr[2 * i4];
					v4.y = posArr[2 * i4 + 1];
					v4.z = posArr[2 * i4 + 2];
					v4 = v4.sub(v0);

					v1.cross(v2, v12);
					v12.normalize();

					v2.cross(v3, v23);
					v23.normalize();

					v3.cross(v4, v34);
					v34.normalize();

					v4.cross(v1, v41);
					v41.normalize();

					normal = v12.add(v23).add(v34).add(v41);
					normal.normalize();
				} else {
					normal.x = 0;
					normal.y = 1;
					normal.z = 0;
				}
				normal.normalize();
				for (int i = 0; i < 2; i++) {
					normals.add(normal.x);
					normals.add(normal.y);
					normals.add(normal.z);
				}
			}
		}
		return Utils.listToArray(normals);
	}

}
