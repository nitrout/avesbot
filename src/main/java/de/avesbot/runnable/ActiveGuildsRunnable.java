package de.avesbot.runnable;

import de.avesbot.Avesbot;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

/**
 *
 * @author Nitrout
 */
public class ActiveGuildsRunnable implements Runnable {

	@Override
	public void run() {
		
		List<Guild> guildList = Avesbot.getJda().getGuilds();
		guildList.forEach(g -> {
			Avesbot.getLogger().info(() -> String.format("%s %s", g.getId(), g.getName()));
		});
		Avesbot.getLogger().info(() -> String.format("Total: %d guilds", guildList.size()));
	}
}