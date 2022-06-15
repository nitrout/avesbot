package de.avesbot;

import java.util.Optional;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
			Optional<BaseGuildMessageChannel> defaultChannel = Optional.ofNullable(t.getDefaultChannel());
			if(defaultChannel.isPresent() && defaultChannel.get().canTalk()) {
				defaultChannel.get().sendMessage(broadcastMessage).queue();
			} else {
				t.getTextChannels().stream().filter(g -> g.canTalk()).findAny().ifPresent(channel -> channel.sendMessage(broadcastMessage).queue());
			}
		}
	};
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		String authorId = event.getAuthor().getId();
		String message = event.getMessage().getContentRaw();
		
		if(authorId.equals(Avesbot.getProperties().getProperty("control_user")) && event.isFromType(ChannelType.PRIVATE)) {
			
			if("!broadcast".equals(message)) {
				Avesbot.getJda().getGuilds().forEach(BROADCAST_ACTION);
			} else {
				broadcastMessage = message;
				event.getChannel().sendMessage(message).submit();
			}
		}
	}
}