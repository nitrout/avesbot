package de.avesbot;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import de.avesbot.callable.CommandCallable;
import de.avesbot.callable.ImportCallable;
import de.avesbot.i18n.I18n;
import de.avesbot.model.GuildSetting;

/**
 * Listener for incoming messages.
 * @author nitrout
 */
public class MessageListener extends ListenerAdapter {

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		
		GuildSetting settings = Avesbot.getStatementManager().getGuildSetting(event.getGuild()).orElse(GuildSetting.DEFAULT);
		StringBuilder answer = new StringBuilder();
		
		Optional<CommandCallable> command = CommandBook.getInstance().getCommand(event);
		command.ifPresentOrElse(callable -> {
			try {
				Future<String> result = Avesbot.getThreadPoolExecutor().submit(callable);
				answer.append(result.get(callable instanceof ImportCallable ? 15 : 2, TimeUnit.SECONDS));
			} catch (ExecutionException ex) {
				ex.printStackTrace(System.err);
				answer.append(I18n.getInstance().getString(settings.getLocale(), "errorCommandExecution"));
			} catch (TimeoutException ex) {
				System.err.println(ex.getMessage());
				answer.append(I18n.getInstance().getString(settings.getLocale(), "errorCommandTimeLimit"));
			} catch (InterruptedException ex) {
				System.err.println(ex.getMessage());
				answer.append(I18n.getInstance().getString(settings.getLocale(), "errorCommandInterrupted"));
			}
		},
		() -> answer.append(I18n.getInstance().getString(settings.getLocale(), "errorCommandNotImplemented")));
		
		event
			.reply(answer.toString())
			.queue();
	}
	
	
}