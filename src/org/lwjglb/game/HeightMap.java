package org.lwjglb.game;

import java.util.ArrayList;
import java.util.List;

import org.lwjglb.game.engine.Mesh;
import org.lwjglb.game.engine.utils.SimplexNoise;
import org.lwjglb.game.engine.utils.Utils;

public class HeightMap extends GameModel {
	
	private static final float START_X = -0.5f;
	private static final float START_Z = -0.5f;

	public HeightMap(float minZ, float maxZ, float persistence, int rows, int cols) {
		super(createMesh(minZ, maxZ, persistence, rows, cols));
	}

	protected static Mesh createMesh(float minZ, float maxZ, float persistence, int rows, int cols) {
		SimplexNoise noise = new SimplexNoise(128, persistence, Utils.getRandom().nextInt());
		
		float xStep = Math.abs(START_X * 2) / (cols - 1);
		float zStep = Math.abs(START_Z * 2) / (rows - 1);
		
		List<Float> positions = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();

		for (int x = 0; x < cols; x++) {
			for (int z = 0; z < rows; z++) {
				positions.add(START_X + x * xStep);
				// scale from [-0.5, 0.5] to [minY, maxY]
				float height = (float) ((noise.getNoise(x, z) + 0.5f) * (maxZ - minZ) + minZ);
				positions.add(height);
				positions.add(START_Z + z * zStep);
				
				// Create indices
		        if (x < cols - 1 && z < rows - 1) {
		            int leftTop = z * cols + x;
		            int leftBottom = (z + 1) * cols + x;
		            int rightBottom = (z + 1) * cols + x + 1;
		            int rightTop = z * cols + x + 1;

		            indices.add(rightTop);
		            indices.add(leftBottom);
		            indices.add(leftTop);

		            indices.add(rightBottom);
		            indices.add(leftBottom);
		            indices.add(rightTop);
		        }
			}
		}
		
		float[] verticesArr = new float[positions.size()];
		for(int i = 0; i < verticesArr.length; i++){
			verticesArr[i] = positions.get(i);
		}
		float[] colorArr = new float[positions.size()];
		for(int i = 0; i < colorArr.length; i += 3){
			
			colorArr[i] = (float) i / colorArr.length;
			colorArr[i+1] = (float) 0;
			colorArr[i+2] = (float) 0;
		}
		int[] indicesArr = indices.stream().mapToInt((i) -> i).toArray();		

		return new Mesh(verticesArr, colorArr, indicesArr);
	}

}
