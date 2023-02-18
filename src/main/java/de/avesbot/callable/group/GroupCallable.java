package de.avesbot.callable.group;

import de.avesbot.callable.CommandCallable;
import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Abstract class for group callables.
 * @author Nitrout
 */
public abstract class GroupCallable extends CommandCallable {
	
	protected static final I18n I18N = new I18n("de.avesbot.i18n.group");
	public static final SlashCommandData COMMAND = buildTranslatedSlashCommand(I18N, "group", "groupDescription");

	public GroupCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
}