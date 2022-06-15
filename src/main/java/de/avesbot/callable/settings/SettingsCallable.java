package de.avesbot.callable.settings;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import de.avesbot.callable.CommandCallable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Abstract class for setting callables.
 * @author Nitrout
 */
public abstract class SettingsCallable extends CommandCallable {
	
	public static final SlashCommandData COMMAND = Commands.slash("settings", "Execute simple or character rolls.");
	
	static {
		COMMAND.addSubcommands(
			new SubcommandData("language", "Set the localization of the bot.").addOptions(
				new OptionData(OptionType.STRING, "locale", "The locale like en_US or de_DE.", true)
			),
			new SubcommandData("hidestats", "Set the language of the bot.").addOptions(
				new OptionData(OptionType.STRING, "hide", "Hide the stats?.", true)
					.addChoice("N", "false")
					.addChoice("Y", "true")
			)
		);
	}
	
	public SettingsCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
}