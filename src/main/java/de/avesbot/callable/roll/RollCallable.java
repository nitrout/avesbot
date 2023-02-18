package de.avesbot.callable.roll;

import java.util.HashMap;
import de.avesbot.callable.CommandCallable;
import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Abstract class for roll callables.
 * @author Nitrout
 */
public abstract class RollCallable extends CommandCallable {
	
	protected static final I18n I18N = new I18n("de.avesbot.i18n.roll");
	public static final SlashCommandData COMMAND = buildTranslatedSlashCommand(I18N, "roll", "rollDescription");
	
	protected final HashMap<String, Emoji> emoteMap;

	public RollCallable(SlashCommandInteractionEvent event) {
		super(event);
		emoteMap = new HashMap<>();
		guild.getEmojis().stream().forEach(emote -> emoteMap.put(emote.getName(), emote));
	}
}