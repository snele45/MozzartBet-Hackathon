package com.mozzartbet.hackaton.connect4.util;

public final class ThreadHelper {

	private ThreadHelper() {}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignore) {
		}
	}
	
}
