package de.avesbot.callable.group;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import de.avesbot.callable.CommandCallable;

/**
 * Abstract class for group callables.
 * @author Nitrout
 */
public abstract class GroupCallable extends CommandCallable {
	
	public static final CommandData COMMAND = new CommandData("group", "Manage and execute rolls for character groups.");
	
	static {
		COMMAND.addSubcommands(
			new SubcommandData("attribute", "Roll an attribute trial for the active group.").addOptions(
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
			new SubcommandData("choose", "Choose a group as active group.").addOptions(
				new OptionData(OptionType.STRING, "groupname", "The name of the group to be the next active group.", true)
			),
			new SubcommandData("create", "Create a new character group.").addOptions(
				new OptionData(OptionType.STRING, "groupname", "The name of the new group.", true)
			),
			new SubcommandData("join", "Join a group.").addOptions(
				new OptionData(OptionType.STRING, "groupname", "The name of the group to join with the current character.", true)
			),
			new SubcommandData("leave", "Leave a group.").addOptions(
				new OptionData(OptionType.STRING, "groupname", "The name of the group to leave with the current character.", true)
			),
			new SubcommandData("skill", "Roll a skill trial for the active group.").addOptions(
				new OptionData(OptionType.STRING, "skill", "The name of the skill to roll for.", true),
				new OptionData(OptionType.INTEGER, "difficulty", "The difficulty of the trial."),
				new OptionData(OptionType.STRING, "tradition", "The tradition of the spell/liturgy to roll for.")
			)
		);
	}

	public GroupCallable(SlashCommandEvent event) {
		super(event);
	}
}