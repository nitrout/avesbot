package de.avesbot.callable.settings;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import de.avesbot.callable.CommandCallable;

/**
 * Abstract class for setting callables.
 * @author Nitrout
 */
public abstract class SettingsCallable extends CommandCallable {
	
	public static final CommandData COMMAND = new CommandData("settings", "Execute simple or character rolls.");
	
	static {
		COMMAND.addSubcommand(new SubcommandData("language", "Set the localization of the bot.")
				.addOption(new OptionData(OptionType.STRING, "locale", "The locale like en_US or de_DE.")
						.setRequired(true)
				)
		);
		COMMAND.addSubcommand(new SubcommandData("hidestats", "Set the language of the bot.")
				.addOption(new OptionData(OptionType.STRING, "hide", "Hide the stats?.")
						.setRequired(true)
						.addChoice("N", "false")
						.addChoice("Y", "true")
				)
		);
	}
	
	public SettingsCallable(SlashCommandEvent event) {
		super(event);
	}
}