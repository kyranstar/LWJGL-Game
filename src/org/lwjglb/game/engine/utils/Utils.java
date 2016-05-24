package org.lwjglb.game.engine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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

}