package de.avesbot.util;

import java.sql.SQLException;
import de.avesbot.db.Database;

/**
 *
 * @author kling
 * @deprecated 
 */
public class DatabaseKeepAlive implements Runnable {

	@Override
	public void run() {
		
		try {
			Database.query("SELECT NOW()");
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
}
