package com.team.engine;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

public class Util {
	public static void print(String s) {
		System.out.println(s);
	}
	
	/**
	 * Returns true if the specified file exists.
	 */
	public static boolean fileExists(String file) {
		return Files.exists(Paths.get(file), LinkOption.NOFOLLOW_LINKS);
	}
}
