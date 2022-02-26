package de.avesbot.callable.roll;

import java.util.HashMap;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import de.avesbot.callable.CommandCallable;

/**
 * Abstract class for roll callables.
 * @author Nitrout
 */
public abstract class RollCallable extends CommandCallable {
	
	public static final CommandData COMMAND = new CommandData("roll", "Execute simple or character rolls.");
	
	protected final HashMap<String, Emote> emoteMap;
	
	static {
		COMMAND.addSubcommands(
			new SubcommandData("attribute", "Roll an attribute trial for the active character.").addOptions(
				new OptionData(OptionType.STRING, "attribute", "The attribute of the trial.", true)
					.addChoice("COU", "Courage")
					.addChoice("SGC", "Sagacity")
					.addChoice("INT", "Intuition")
					.addChoice("CHA", "Charisma")
					.addChoice("DEX", "Dexterity")
					.addChoice("AGI", "Agility")
					.addChoice("CON", "Constitution")
					.addChoice("STR", "Strength"),
				new OptionData(OptionType.INTEGER, "difficulty", "The difficulty of the trial.")
			),
			new SubcommandData("dice", "Roll dice(s).").addOptions(
				new OptionData(OptionType.STRING, "dice", "An expression like 2W6 or the name of a symbol dice.", true)
			),
			new SubcommandData("skill", "Roll a skill trial for the active character.").addOptions(
				new OptionData(OptionType.STRING, "skill", "The name of the skill to roll for.", true),
				new OptionData(OptionType.INTEGER, "difficulty", "The difficulty of the trial."),
				new OptionData(OptionType.STRING, "coverage", "The coverages of the spell/liturgy as comma separated list."),
				new OptionData(OptionType.STRING, "tradition", "The tradition of the spell/liturgy.")
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
			),
			new SubcommandData("slip", "Roll slip dices for battle (TDE4 only)."),
			new SubcommandData("sum", "Roll dice(s).").addOptions(
				new OptionData(OptionType.STRING, "dice", "An expression like 2W6+2", true)
			),
			new SubcommandData("trial", "Roll a custom trial for the active character.").addOptions(
				new OptionData(OptionType.STRING, "attribute1", "The 1st attribute of the trial.", true)
					.addChoice("COU", "Courage")
					.addChoice("SGC", "Sagacity")
					.addChoice("INT", "Intuition")
					.addChoice("CHA", "Charisma")
					.addChoice("DEX", "Dexterity")
					.addChoice("AGI", "Agility")
					.addChoice("CON", "Constitution")
					.addChoice("STR", "Strength"),
				new OptionData(OptionType.STRING, "attribute2", "The 2nd attribute of the trial.", true)
					.addChoice("COU", "Courage")
					.addChoice("SGC", "Sagacity")
					.addChoice("INT", "Intuition")
					.addChoice("CHA", "Charisma")
					.addChoice("DEX", "Dexterity")
					.addChoice("AGI", "Agility")
					.addChoice("CON", "Constitution")
					.addChoice("STR", "Strength"),
				new OptionData(OptionType.STRING, "attribute3", "The 3rd attribute of the trial.", true)
					.addChoice("COU", "Courage")
					.addChoice("SGC", "Sagacity")
					.addChoice("INT", "Intuition")
					.addChoice("CHA", "Charisma")
					.addChoice("DEX", "Dexterity")
					.addChoice("AGI", "Agility")
					.addChoice("CON", "Constitution")
					.addChoice("STR", "Strength"),
				new OptionData(OptionType.INTEGER, "sr", "The skill rating of the trial.", true),
				new OptionData(OptionType.STRING, "spell", "Is the trial a spell?")
					.addChoice("N", "false")
					.addChoice("Y", "true"),
				new OptionData(OptionType.INTEGER, "difficulty", "The difficulty of the trial.")
			)
		);
	}

	public RollCallable(SlashCommandEvent event) {
		super(event);
		emoteMap = new HashMap<>();
		guild.getEmotes().stream().forEach(emote -> emoteMap.put(emote.getName(), emote));
	}
}