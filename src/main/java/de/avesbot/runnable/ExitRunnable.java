package de.avesbot.runnable;

import de.avesbot.Avesbot;

/**
 * Shuts down the ThreadPoolExecutor to stop the application.
 * @author Nitrout
 */
public class ExitRunnable implements Runnable {

	@Override
	public void run() {
		
		Avesbot.getThreadPoolExecutor().shutdown();
	}
}