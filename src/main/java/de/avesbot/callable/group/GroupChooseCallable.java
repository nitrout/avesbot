package de.avesbot.callable.group;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Group;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * A callable to switch between groups.
 * @author Nitrout
 */
public class GroupChooseCallable extends GroupCallable {
	
	/**
	 * Creates a new ChooseGroupCallable.
	 * @param event 
	 */
	public GroupChooseCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String groupName = this.commandPars.get("groupname").getAsString();
		boolean success = Avesbot.getStatementManager().enableUsersGroup(member, guild, groupName);
		
		if(success) {
			Optional<Group> group = Avesbot.getStatementManager().getUsersActiveGroup(guild, member);
			if(group.isPresent()) {
				return  I18n.getInstance().format(settings.locale(),"groupNewActive", group.get().name());
			} else {
				return I18n.getInstance().getString(settings.locale(), "errorUnknownGroup");
			}
		} else {
			return I18n.getInstance().getString(settings.locale(), "errorUnknownGroup");
		}
	}
}