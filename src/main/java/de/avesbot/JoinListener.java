package de.avesbot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

/**
 * Listener to handle necessary actions, when Avesbot joins a new guild.
 * @author Nitrout
 */
public class JoinListener extends ListenerAdapter {
	
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		
		Guild guild = event.getGuild();
		Optional<Member> aves = Optional.ofNullable(guild.getMember(event.getJDA().getSelfUser()));
		
		// can we manage emotes and do we have 26 free emotes?
		// then add aves dice icons to guild
		aves.ifPresent(a -> {
			if(a.getRoles().stream().anyMatch(role -> role.hasPermission(Permission.MANAGE_EMOTES)) && guild.getBoostTier().getMaxEmotes()-guild.getEmotes().size() >= 26) {
				try {
					for(int i = 1; i <= 20; i++) {
						InputStream emoteStream = Avesbot.class.getResourceAsStream("assets/"+i+"d20.png");
						AuditableRestAction<Emote> createEmote = guild.createEmote(i+"d20", Icon.from(emoteStream, Icon.IconType.PNG));
						createEmote.queue();
					}
					for(int i = 1; i <= 6; i++) {
						InputStream emoteStream = Avesbot.class.getResourceAsStream("assets/"+i+"d6.png");
						AuditableRestAction<Emote> createEmote = guild.createEmote(i+"d6", Icon.from(emoteStream, Icon.IconType.PNG));
						createEmote.queue();
					}
				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}
			} else {
				System.out.println("No permissions to create new emotes!");
			}
		});
		
		Avesbot.getStatementManager().insertGuild(guild);
	}
}