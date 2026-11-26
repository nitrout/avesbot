package de.avesbot.util;

import de.avesbot.Avesbot;
import de.avesbot.db.Database;

import java.sql.SQLException;

/**
 *
 * @author kling
 */
public class DatabaseKeepAlive implements Runnable {

	@Override
	public void run() {

		try (final var _ = Database.query("SELECT NOW()")) {
		} catch (SQLException ex) {
			Avesbot.getLogger().severe(ex::getMessage);
		}
	}
	
}
