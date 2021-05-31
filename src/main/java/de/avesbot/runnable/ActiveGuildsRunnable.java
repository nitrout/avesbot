package de.avesbot.runnable;

import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import de.avesbot.Avesbot;

/**
 *
 * @author Nitrout
 */
public class ActiveGuildsRunnable implements Runnable {

	@Override
	public void run() {
		
		List<Guild> guildList = Avesbot.getJda().getGuilds();
		guildList.forEach(g -> {
			System.out.println(String.format("%s %s", g.getId(), g.getName()));
		});
		System.out.println(String.format("Total: %d guilds", guildList.size()));
	}
}