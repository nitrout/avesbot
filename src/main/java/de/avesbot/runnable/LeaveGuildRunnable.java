package de.avesbot.runnable;

import java.util.Optional;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.Guild;
import de.avesbot.Avesbot;

/**
 *
 * @author Nitrout
 */
public class LeaveGuildRunnable implements Runnable {
	
	String[] guildIds;
	
	public LeaveGuildRunnable(String...pars) {
		this.guildIds = pars;
	}
	
	public void run() {
		
		Stream.of(guildIds).forEach(gId -> {
			Optional<Guild> oGuild = Optional.ofNullable(Avesbot.getJda().getGuildById(gId));
			oGuild.ifPresent(g -> {
				System.out.println(String.format("Aves leaves guild %s", g.getName()));
				g.leave().submit();
			});
		});
	}
}