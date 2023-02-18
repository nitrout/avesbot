package de.avesbot.callable.settings;

import de.avesbot.callable.CommandCallable;
import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Abstract class for setting callables.
 * @author Nitrout
 */
public abstract class SettingsCallable extends CommandCallable {
	
	protected static final I18n I18N = new I18n("de.avesbot.i18n.settings");
	public static final SlashCommandData COMMAND = buildTranslatedSlashCommand(I18N, "settings", "settingsDescription");
	
	public SettingsCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
}