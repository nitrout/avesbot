package de.avesbot.runnable;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.avesbot.model.Ruleset;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nitrout
 */
public class SocketRunnable implements Runnable {
	
	private record HeroImportRequest(String userId, Ruleset ruleset, String data) {}
	private record HeroImportResponse(int characterId) {}
	
	private final Socket socket;
	private final ObjectMapper mapper;

	public SocketRunnable(Socket socket) {
		this.socket = socket;
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
	}
	
	@Override
	public void run() {
		try(socket; var br = new BufferedReader(new InputStreamReader(socket.getInputStream())); var bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
			var line = br.readLine();
			var data = mapper.readValue(line, HeroImportRequest.class);
			bw.write(mapper.writeValueAsString(new HeroImportResponse(5)));
		} catch (IOException ex) {
			Logger.getLogger(SocketRunnable.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
