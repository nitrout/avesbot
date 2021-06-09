package de.avesbot.callable.group;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Group;
import de.avesbot.model.RoleplayCharacter;

/**
 * A callable to leave groups.
 * @author Nitrout
 */
public class GroupLeaveCallable extends GroupCallable {
	
	/**
	 * Creates a new LeaveCallable.
	 * @param event 
	 */
	public GroupLeaveCallable(SlashCommandEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String groupName = this.commandPars.get("groupname").getAsString();
		
		Optional<Group> group = Avesbot.getStatementManager().getGuildGroupByName(guild, groupName);
		Optional<RoleplayCharacter> character = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		
		if(group.isEmpty()) {
			return I18n.getInstance().getString(settings.getLocale(), "errorUnknownGroup");
		} else if(character.isEmpty()) {
			return I18n.getInstance().getString(settings.getLocale(), "errorNoActiveCharacter");
		} else {
			boolean success = Avesbot.getStatementManager().deleteGroupMember(group.get(), character.get());
			if(success)
				return I18n.getInstance().format(settings.getLocale(),"groupCharacterLeave", character.get(), group.get());
			else
				return I18n.getInstance().format(settings.getLocale(), "errorGroupCharacterLeave", character.get(), group.get());
		}
	}
}