package de.avesbot.callable.group;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.model.Group;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable to switch between groups.
 * @author Nitrout
 */
public class GroupChooseCallable extends GroupCallable {
	
	public static final SubcommandData SUBCOMMAND;
	
	static {
		SUBCOMMAND = buildTranslatedSubcommand(I18N, "groupChoose", "groupChooseDescription");
		
		OptionData groupnameOption = buildTranslatedOption(I18N, OptionType.STRING, "groupnameOption", "groupnameOptionDescription", true);
		SUBCOMMAND.addOptions(groupnameOption);
	}
	
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
				return  I18N.format(settings.locale(),"groupNewActive", group.get().name());
			} else {
				return I18N.getTranslation(settings.locale(), "errorUnknownGroup");
			}
		} else {
			return I18N.getTranslation(settings.locale(), "errorUnknownGroup");
		}
	}
}