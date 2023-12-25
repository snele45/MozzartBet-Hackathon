package com.mozzartbet.hackaton.connect4.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class SwingHelper {

	public static void runAsync(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
			return;
		}

		try {
			SwingUtilities.invokeAndWait(r);
		} catch (InterruptedException | InvocationTargetException e) {
		}
	}
	
}
