package de.avesbot.callable.group;

import de.avesbot.Avesbot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable to create new groups.
 * @author Nitrout
 */
public class GroupCreateCallable extends GroupCallable {
	
	static {
		SubcommandData subcommand = buildTranslatedSubcommand(I18N, "create", "groupCreateDescription");
		
		OptionData groupnameOption = buildTranslatedOption(I18N, OptionType.STRING, "groupname", "The name of the new group.", true);
		subcommand.addOptions(groupnameOption);
		
		COMMAND.addSubcommands(subcommand);
	}
	
	/**
	 * Creates a new GroupCallable.
	 * @param event 
	 */
	public GroupCreateCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String groupName = this.commandPars.get("groupname").getAsString();
		
		Avesbot.getStatementManager().insertGroup(member, guild, groupName);
		
		return  I18N.format(settings.locale(), "groupCreated", groupName);
	}
}