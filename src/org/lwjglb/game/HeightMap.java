package org.lwjglb.game;

import java.util.Random;

import org.lwjglb.game.engine.Mesh;
import org.lwjglb.game.engine.utils.SimplexNoise;
import org.lwjglb.game.engine.utils.Utils;

public class HeightMap extends GameModel{

	public HeightMap(int minY, int maxY, float smoothness, int rows, int cols) {
		super(createMesh(minY, maxY, smoothness, rows, cols));
		// TODO Auto-generated constructor stub
	}

	private static Mesh createMesh(int minY, int maxY, float smoothness, int rows, int cols) {
		SimplexNoise noise = new SimplexNoise(maxY - minY, smoothness, Utils.getRandom().nextInt());
		return null;
	}

}
