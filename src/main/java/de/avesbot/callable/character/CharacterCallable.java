package de.avesbot.callable.character;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import de.avesbot.callable.CommandCallable;

/**
 * Abstract class for character callables.
 * @author Nitrout
 */
public abstract class CharacterCallable extends CommandCallable {
	
	public static final CommandData COMMAND = new CommandData("character", "Execute character related commands.");
	
	static {
		COMMAND.addSubcommands(
			new SubcommandData("choose", "Switch to a new active character.").addOptions(
				new OptionData(OptionType.STRING, "name", "The name of the next active character.", true)
			),
			new SubcommandData("create", "Create a new character.").addOptions(
				new OptionData(OptionType.STRING, "name", "The name of the new character.", true),
				new OptionData(OptionType.STRING, "ruleset", "The ruleset of the new character.", true)
					.addChoice("TDE4", "TDE4")
					.addChoice("TDE5", "TDE5"),
				new OptionData(OptionType.INTEGER, "courage", "The courage of the new character.", true),
				new OptionData(OptionType.INTEGER, "sagacity", "The sagacity of the new character.", true),
				new OptionData(OptionType.INTEGER, "intuition", "The intuition of the new character.", true),
				new OptionData(OptionType.INTEGER, "charisma", "The charisma of the new character.", true),
				new OptionData(OptionType.INTEGER, "dexterity", "The dexterity of the new character.", true),
				new OptionData(OptionType.INTEGER, "agility", "The agility of the new character.", true),
				new OptionData(OptionType.INTEGER, "constitution", "The constitution of the new character.", true),
				new OptionData(OptionType.INTEGER, "strength", "The strength of the new character.", true)
			),
			new SubcommandData("delete", "Delete a character.").addOptions(
				new OptionData(OptionType.STRING, "name", "The name of the character who will be deleted.", true)
			),
			new SubcommandData("info", "Show the information of a character or one of his skills.").addOptions(
				new OptionData(OptionType.STRING, "skill", "The name of the skill to show information for."),
				new OptionData(OptionType.STRING, "tradition", "The tradition of the skill the information should be shown.")
			),
			new SubcommandData("list", "Shows a list of your characters."),
			new SubcommandData("train", "Trains a new skill for the active character.").addOptions(
				new OptionData(OptionType.STRING, "skill", "The name of the skill.", true),
				new OptionData(OptionType.STRING, "attribute1", "The 1st attribute of the skill.", true)
					.addChoice("COU", "Courage")
					.addChoice("SGC", "Sagacity")
					.addChoice("INT", "Intuition")
					.addChoice("CHA", "Charisma")
					.addChoice("DEX", "Dexterity")
					.addChoice("AGI", "Agility")
					.addChoice("CON", "Constitution")
					.addChoice("STR", "Strength"),
				new OptionData(OptionType.STRING, "attribute2", "The 2nd attribute of the skill.", true)
					.addChoice("COU", "Courage")
					.addChoice("SGC", "Sagacity")
					.addChoice("INT", "Intuition")
					.addChoice("CHA", "Charisma")
					.addChoice("DEX", "Dexterity")
					.addChoice("AGI", "Agility")
					.addChoice("CON", "Constitution")
					.addChoice("STR", "Strength"),
				new OptionData(OptionType.STRING, "attribute3", "The 3rd attribute of the skill.", true)
					.addChoice("COU", "Courage")
					.addChoice("SGC", "Sagacity")
					.addChoice("INT", "Intuition")
					.addChoice("CHA", "Charisma")
					.addChoice("DEX", "Dexterity")
					.addChoice("AGI", "Agility")
					.addChoice("CON", "Constitution")
					.addChoice("STR", "Strength"),
				new OptionData(OptionType.INTEGER, "sr", "The skill rating.", true),
				new OptionData(OptionType.STRING, "type", "Is the skill a talent, spell or liturgy.")
					.addChoice("Talent", "talent")
					.addChoice("Spell", "spell")
					.addChoice("Liturgy", "liturgy"),
				new OptionData(OptionType.STRING, "tradition", "The magical tradition of the spell.")
					.addChoice("None", "none")
					.addChoice("Borbarad", "borbarad")
					.addChoice("Druid", "druid")
					.addChoice("Elf", "elf")
					.addChoice("Geode", "geode")
					.addChoice("Witch", "witch")
					.addChoice("Achaz", "achaz")
					.addChoice("Guild Mage", "guild_mage")
					.addChoice("Illusionist", "illusionist")
					.addChoice("Trickster", "trickster")
					.addChoice("Other", "other")
			)
		);
	}
	
	/**
	 * Creates a new character creation callable.
	 * @param event
	 */
	public CharacterCallable(SlashCommandEvent event) {
		super(event);
	}
}