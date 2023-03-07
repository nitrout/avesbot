package de.avesbot.callable.group;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.model.Group;
import de.avesbot.model.RoleplayCharacter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable to leave groups.
 * @author Nitrout
 */
public class GroupLeaveCallable extends GroupCallable {
	
	public static final SubcommandData SUBCOMMAND;
	
	static {
		SUBCOMMAND = buildTranslatedSubcommand(I18N, "groupLeave", "groupLeaveDescription");
		
		OptionData groupnameOption = buildTranslatedOption(I18N, OptionType.STRING, "groupnameOption", "groupnameOptionDescription", true);
		SUBCOMMAND.addOptions(groupnameOption);
	}
	
	/**
	 * Creates a new LeaveCallable.
	 * @param event 
	 */
	public GroupLeaveCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String groupName = this.commandPars.get("groupname").getAsString();
		
		Optional<Group> group = Avesbot.getStatementManager().getGuildGroupByName(guild, groupName);
		Optional<RoleplayCharacter> character = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		
		if(group.isEmpty()) {
			return I18N.getTranslation(settings.locale(), "errorUnknownGroup");
		} else if(character.isEmpty()) {
			return I18N.getTranslation(settings.locale(), "errorNoActiveCharacter");
		} else {
			boolean success = Avesbot.getStatementManager().deleteGroupMember(group.get(), character.get());
			if(success)
				return I18N.format(settings.locale(),"groupCharacterLeave", character.get(), group.get());
			else
				return I18N.format(settings.locale(), "errorGroupCharacterLeave", character.get(), group.get());
		}
	}
}