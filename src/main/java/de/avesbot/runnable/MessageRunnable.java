package de.avesbot.runnable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import de.avesbot.Avesbot;

/**
 * Used to broadcast a message to all active guilds.
 * @deprecated use broadcast listener instead
 * @see BroadcastListener
 * @author Nitrout
 */
@Deprecated
public class MessageRunnable implements Runnable {

	private final Pattern GUILD_PATTERN = Pattern.compile("\\d+");
	
	private final String message;
	private final Optional<Guild> guild;
	
	private final Consumer<Guild> MESSAGE_ACTION = new Consumer<Guild>() {
		@Override
		public void accept(Guild t) {
			Optional<TextChannel> defaultChannel = Optional.ofNullable(t.getDefaultChannel());
			if(defaultChannel.isPresent() && defaultChannel.get().canTalk()) {
				defaultChannel.get().sendMessage(message).queue();
			} else {
				t.getTextChannels().stream().filter(g -> g.canTalk()).findAny().ifPresent(channel -> channel.sendMessage(message).queue());
			}
		}
	};
	
	public MessageRunnable(String...pars) {
		if(GUILD_PATTERN.asMatchPredicate().test(pars[0])) {
			this.guild = Optional.ofNullable(Avesbot.getJda().getGuildById(pars[0]));
			this.message = String.join(" ", Arrays.copyOfRange(pars, 1, pars.length));
		}
		else {
			this.guild = Optional.empty();
			this.message = String.join(" ", pars);
		}
	}
	
	@Override
	public void run() {
		
		guild.ifPresentOrElse(MESSAGE_ACTION,
			() -> Avesbot.getJda().getGuilds().forEach(MESSAGE_ACTION)
		);
	}
}