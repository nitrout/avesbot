package de.avesbot.callable.character;

import de.avesbot.callable.CommandCallable;
import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Abstract class for character callables.
 * @author Nitrout
 */
public abstract class CharacterCallable extends CommandCallable {
	
	protected static final I18n I18N = new I18n("de.avesbot.i18n.character");
	public static final SlashCommandData COMMAND = buildTranslatedSlashCommand(I18N, "character", "characterDescription");
	
	/**
	 * Creates a new character creation callable.
	 * @param event
	 */
	public CharacterCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
}