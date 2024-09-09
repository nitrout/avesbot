package de.avesbot.runnable;

import de.avesbot.Avesbot;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nitrout
 */
public class ServerSocketRunnable implements Runnable {

	private final ServerSocket socket;
	
	public ServerSocketRunnable(ServerSocket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		while(!socket.isClosed()) {
			try {
				var sock = socket.accept();
				Avesbot.getThreadPoolExecutor().execute(new SocketRunnable(sock));
			} catch (IOException ex) {
				Logger.getLogger(ServerSocketRunnable.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
}
