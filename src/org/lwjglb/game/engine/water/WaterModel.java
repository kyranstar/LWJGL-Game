package org.lwjglb.game.engine.water;

import java.util.ArrayList;
import java.util.List;

import org.lwjglb.game.GameModel;
import org.lwjglb.game.engine.Mesh;
import org.lwjglb.game.engine.utils.Utils;

public class WaterModel extends GameModel{
	private static final float START_X = -0.5f;
	private static final float START_Z = -0.5f;
	private static final float REFLECTANCE = 0.8f;
	
	private final float height;

	public WaterModel(int vertX, int vertY, float height) {
		super(generateMesh(vertX, vertY, height), REFLECTANCE);
		this.height = height;
		
	}

	private static Mesh generateMesh(int vertX, int vertY, float height) {
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
		return new Mesh(Utils.listToArray(vertList), indList.stream().mapToInt((i)->i).toArray(), colors);

	}

	public float getHeight() {
		return height * getScale();
	}
}
