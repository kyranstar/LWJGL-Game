package org.lwjglb.game.engine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.joml.Vector3f;

public class Utils {
	private static Random random = new Random();

	public static String loadResource(String fileName) throws IOException {
		String result = "";
		try (InputStream in = Utils.class.getClass().getResourceAsStream(fileName);
				Scanner s = new Scanner(in, "UTF-8")) {
			result = s.useDelimiter("\\A").next();
		}
		return result;
	}

	public static Random getRandom() {
		return random;
	}

	public static List<String> readAllLines(String fileName) throws IOException {
		List<String> result = new ArrayList<>();
		try (InputStream in = Utils.class.getClass().getResourceAsStream(fileName);
				Scanner s = new Scanner(in, "UTF-8")) {
			while (s.hasNextLine()) {
				result.add(s.nextLine());
			}
		}
		return result;
	}

	public static float[] listToArr(List<Vector3f> vertices) {
		float[] f = new float[vertices.size() * 3];
		int i = 0;
		for(Vector3f v : vertices){
			f[i] = v.x;
			f[i+1] = v.y;
			f[i+2] = v.z;
			i += 3;
		}
		return f;
	}

}