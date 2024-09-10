package de.avesbot;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import de.avesbot.callable.CommandCallable;
import de.avesbot.i18n.I18n;
import de.avesbot.model.GuildSetting;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Listener for incoming messages.
 * @author nitrout
 */
public class MessageListener extends ListenerAdapter {
	
	private static final I18n I18N = new I18n("de.avesbot.i18n.messages");

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		
		event.deferReply().queue();
		
		GuildSetting settings = Avesbot.getStatementManager().getGuildSetting(event.getGuild()).orElse(GuildSetting.DEFAULT);
		StringBuilder answer = new StringBuilder();
		
		Optional<CommandCallable> command = CommandBook.getInstance().getCommand(event);
		command.ifPresentOrElse(callable -> {
			try {
				Future<String> result = Avesbot.getThreadPoolExecutor().submit(callable);
				answer.append(result.get(callable.getTimeout(), TimeUnit.SECONDS));
			} catch (ExecutionException ex) {
				Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
				answer.append(I18N.getTranslation(settings.locale(), "errorCommandExecution"));
			} catch (TimeoutException ex) {
				Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
				answer.append(I18N.getTranslation(settings.locale(), "errorCommandTimeLimit"));
			} catch (InterruptedException ex) {
				Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
				answer.append(I18N.getTranslation(settings.locale(), "errorCommandInterrupted"));
			}
		},
		() -> answer.append(I18N.getTranslation(settings.locale(), "errorCommandNotImplemented")));
		
		event.getHook().sendMessage(answer.toString()).queue();
	}
}