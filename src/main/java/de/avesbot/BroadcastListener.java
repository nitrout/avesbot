package de.avesbot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

/**
 * Listener to execute broadcast commands as administrator of the bot.
 * @author Nitrout
 */
public class BroadcastListener extends ListenerAdapter {

	private static String broadcastMessage;

	private static void broadcast(Guild t) {
		Optional<DefaultGuildChannelUnion> defaultChannel = Optional.ofNullable(t.getDefaultChannel());
		if (defaultChannel.isPresent() && defaultChannel.get().asStandardGuildMessageChannel().canTalk()) {
			defaultChannel.get().asStandardGuildMessageChannel().sendMessage(broadcastMessage).queue();
		} else {
			t.getTextChannels().stream().filter(GuildMessageChannel::canTalk).findAny().ifPresent(channel -> channel.sendMessage(broadcastMessage).queue());
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		String authorId = event.getAuthor().getId();
		String message = event.getMessage().getContentRaw();
		
		if(authorId.equals(Avesbot.getProperties().getProperty("control_user")) && event.isFromType(ChannelType.PRIVATE)) {
			
			if("!broadcast".equals(message)) {
				Avesbot.getJda().getGuilds().forEach(BroadcastListener::broadcast);
			} else {
				broadcastMessage = message;
				event.getChannel().sendMessage(message).submit();
			}
		}
	}
}