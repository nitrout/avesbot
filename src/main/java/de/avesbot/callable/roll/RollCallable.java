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
		COMMAND.addSubcommand(new SubcommandData("attribute", "Roll an attribute trial for the active character.")
				.addOption(new OptionData(OptionType.STRING, "attribute", "The attribute of the trial.")
						.setRequired(true)
						.addChoice("COU", "Courage")
						.addChoice("SGC", "Sagacity")
						.addChoice("INT", "Intuition")
						.addChoice("CHA", "Charisma")
						.addChoice("DEX", "Dexterity")
						.addChoice("AGI", "Agility")
						.addChoice("CON", "Constitution")
						.addChoice("STR", "Strength")
				)
				.addOption(new OptionData(OptionType.INTEGER, "difficulty", "The difficulty of the trial."))
		);
		COMMAND.addSubcommand(new SubcommandData("dice", "Roll dice(s).")
				.addOption(new OptionData(OptionType.STRING, "dice", "An expression like 2W6 or the name of a symbol dice.")
						.setRequired(true)
				)
		);
		COMMAND.addSubcommand(new SubcommandData("skill", "Roll a skill trial for the active character.")
				.addOption(new OptionData(OptionType.STRING, "skill", "The name of the skill to roll for.")
						.setRequired(true)
				)
				.addOption(new OptionData(OptionType.INTEGER, "difficulty", "The difficulty of the trial."))
				.addOption(new OptionData(OptionType.STRING, "coverage", "The coverages of the spell/liturgy as comma separated list."))
				.addOption(new OptionData(OptionType.STRING, "tradition", "The tradition of the spell/liturgy.")
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
		COMMAND.addSubcommand(new SubcommandData("slip", "Roll slip dices for battle (TDE4 only)."));
		COMMAND.addSubcommand(new SubcommandData("sum", "Roll dice(s).")
				.addOption(new OptionData(OptionType.STRING, "dice", "An expression like 2W6+2")
						.setRequired(true)
				)
		);
		COMMAND.addSubcommand(new SubcommandData("trial", "Roll a custom trial for the active character.")
				.addOption(new OptionData(OptionType.STRING, "attribute1", "The 1st attribute of the trial.")
						.setRequired(true)
						.addChoice("COU", "Courage")
						.addChoice("SGC", "Sagacity")
						.addChoice("INT", "Intuition")
						.addChoice("CHA", "Charisma")
						.addChoice("DEX", "Dexterity")
						.addChoice("AGI", "Agility")
						.addChoice("CON", "Constitution")
						.addChoice("STR", "Strength")
				)
				.addOption(new OptionData(OptionType.STRING, "attribute2", "The 2nd attribute of the trial.")
						.setRequired(true)
						.addChoice("COU", "Courage")
						.addChoice("SGC", "Sagacity")
						.addChoice("INT", "Intuition")
						.addChoice("CHA", "Charisma")
						.addChoice("DEX", "Dexterity")
						.addChoice("AGI", "Agility")
						.addChoice("CON", "Constitution")
						.addChoice("STR", "Strength")
				)
				.addOption(new OptionData(OptionType.STRING, "attribute3", "The 3rd attribute of the trial.")
						.setRequired(true)
						.addChoice("COU", "Courage")
						.addChoice("SGC", "Sagacity")
						.addChoice("INT", "Intuition")
						.addChoice("CHA", "Charisma")
						.addChoice("DEX", "Dexterity")
						.addChoice("AGI", "Agility")
						.addChoice("CON", "Constitution")
						.addChoice("STR", "Strength")
				)
				.addOption(new OptionData(OptionType.INTEGER, "sr", "The skill rating of the trial.")
						.setRequired(true)
				)
				.addOption(new OptionData(OptionType.STRING, "spell", "Is the trial a spell?")
						.addChoice("N", "false")
						.addChoice("Y", "true")
				)
				.addOption(new OptionData(OptionType.INTEGER, "difficulty", "The difficulty of the trial."))
		);
	}

	public RollCallable(SlashCommandEvent event) {
		super(event);
		emoteMap = new HashMap<>();
		guild.getEmotes().stream().forEach(emote -> emoteMap.put(emote.getName(), emote));
	}
}