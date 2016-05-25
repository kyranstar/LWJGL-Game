package org.lwjglb.game;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjglb.game.engine.Mesh;
import org.lwjglb.game.engine.utils.SimplexNoise;
import org.lwjglb.game.engine.utils.Utils;

public class HeightMap extends GameModel {

	private static final float START_X = -0.5f;
	private static final float START_Z = -0.5f;

	public HeightMap(float minY, float maxY, float persistence, int width, int height) {
		super(createMesh(minY, maxY, persistence, width, height));
	}

	protected static Mesh createMesh(final float minY, final float maxY, final float persistence, final int width,
			final int height) {
		SimplexNoise noise = new SimplexNoise(128, persistence, 2);// Utils.getRandom().nextInt());

		float xStep = Math.abs(START_X * 2) / (width-1);
		float zStep = Math.abs(START_Z * 2) / (height-1);

		List<Float> positions = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();

		for (int z = 0; z < height; z++) {
		for (int x = 0; x < width; x++) {
				// scale from [-0.5, 0.5] to [minY, maxY]
				float heightY = (float) ((noise.getNoise(x, z) + 0.5f) * (maxY - minY) + minY);

				positions.add(START_X + x * xStep);
				positions.add(heightY);
				positions.add(START_Z + z * zStep);

				// Create indices
				if (x < width - 1 && z < height - 1) {
					int leftTop = z * width + x;
					int leftBottom = (z + 1) * width + x;
					int rightBottom = (z + 1) * width + x + 1;
					int rightTop = z * width + x + 1;

					indices.add(leftTop);
					indices.add(leftBottom);
					indices.add(rightTop);

					indices.add(rightTop);
					indices.add(leftBottom);
					indices.add(rightBottom);
				}
			}
		}

		float[] verticesArr = Utils.listToArray(positions);
		float[] colorArr = new float[positions.size()];
		for (int i = 0; i < colorArr.length; i += 3) {
			colorArr[i] = (float) i / colorArr.length;
			colorArr[i + 1] = (float) .25f;
			colorArr[i + 2] = (float) 0;
		}
		int[] indicesArr = indices.stream().mapToInt((i) -> i).toArray();

		float[] normalArr = calcNormals(verticesArr, width, height);

		return new Mesh(verticesArr, colorArr, normalArr, indicesArr);
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
					v0.x = posArr[i0];
					v0.y = posArr[i0 + 1];
					v0.z = posArr[i0 + 2];

					int i1 = row * width * 3 + (col - 1) * 3;
					v1.x = posArr[i1];
					v1.y = posArr[i1 + 1];
					v1.z = posArr[i1 + 2];
					v1 = v1.sub(v0);

					int i2 = (row + 1) * width * 3 + col * 3;
					v2.x = posArr[i2];
					v2.y = posArr[i2 + 1];
					v2.z = posArr[i2 + 2];
					v2 = v2.sub(v0);

					int i3 = (row) * width * 3 + (col + 1) * 3;
					v3.x = posArr[i3];
					v3.y = posArr[i3 + 1];
					v3.z = posArr[i3 + 2];
					v3 = v3.sub(v0);

					int i4 = (row - 1) * width * 3 + col * 3;
					v4.x = posArr[i4];
					v4.y = posArr[i4 + 1];
					v4.z = posArr[i4 + 2];
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
				normals.add(normal.x);
				normals.add(normal.y);
				normals.add(normal.z);
			}
		}
		return Utils.listToArray(normals);
	}

}
