package de.avesbot;

import java.util.Optional;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Listener to execute broadcast commands as administrator of the bot.
 * @author Nitrout
 */
public class BroadcastListener extends ListenerAdapter {

	private String broadcastMessage;
	
	private final Consumer<Guild> BROADCAST_ACTION = new Consumer<Guild>() {
		@Override
		public void accept(Guild t) {
			Optional<TextChannel> defaultChannel = Optional.ofNullable(t.getDefaultChannel());
			if(defaultChannel.isPresent() && defaultChannel.get().canTalk()) {
				defaultChannel.get().sendMessage(broadcastMessage).queue();
			} else {
				t.getTextChannels().stream().filter(g -> g.canTalk()).findAny().ifPresent(channel -> channel.sendMessage(broadcastMessage).queue());
			}
		}
	};
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		
		String authorId = event.getAuthor().getId();
		String message = event.getMessage().getContentRaw();
		
		if(authorId.equals(Avesbot.getProperties().getProperty("control_user"))) {
			
			if("!broadcast".equals(message)) {
				Avesbot.getJda().getGuilds().forEach(BROADCAST_ACTION);
			} else {
				broadcastMessage = message;
				event.getChannel().sendMessage(message).submit();
			}
		}
	}
}