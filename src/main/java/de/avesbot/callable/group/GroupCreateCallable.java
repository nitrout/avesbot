package de.avesbot.callable.group;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;

/**
 * A callable to create new groups.
 * @author Nitrout
 */
public class GroupCreateCallable extends GroupCallable {
	
	/**
	 * Creates a new GroupCallable.
	 * @param event 
	 */
	public GroupCreateCallable(SlashCommandEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String groupName = this.commandPars.get("groupname").getAsString();
		
		Avesbot.getStatementManager().insertGroup(member, guild, groupName);
		
		return  I18n.getInstance().format(settings.getLocale(), "groupCreated", groupName);
	}
}