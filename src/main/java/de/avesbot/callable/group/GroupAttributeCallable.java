package de.avesbot.callable.group;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import de.avesbot.Avesbot;
import de.avesbot.model.Attribute;
import de.avesbot.model.Group;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.callable.RoleplayCharacterRoll;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable to execute an attribute trial for all members of a group.
 * @author Nitrout
 */
public class GroupAttributeCallable extends GroupCallable implements RoleplayCharacterRoll {
	
	private final HashMap<String, Emoji> emoteMap = new HashMap<>();
	
	public static final SubcommandData SUBCOMMAND;
	
	static {
		SUBCOMMAND = buildTranslatedSubcommand(GroupCallable.I18N, "groupAttribute", "groupAttributeDescription");
		
		OptionData attributeOpption = buildTranslatedOption(GroupCallable.I18N, OptionType.STRING, "attributeOption", "attributeOptionDescription", true);
		attributeOpption.addChoices(Attribute.OPTION_CHOICES);
		
		OptionData difficultyOption = buildTranslatedOption(GroupCallable.I18N, OptionType.INTEGER, "difficultyOption", "difficultyOptionDescription", false);
		
		SUBCOMMAND.addOptions(attributeOpption, difficultyOption);
	}

	
	/**
	 * Creates a new GroupAttributeCallable.
	 * @param event 
	 */
	public GroupAttributeCallable(SlashCommandInteractionEvent event) {
		super(event);
		
		guild.getEmojis().stream().forEach(emote -> emoteMap.put(emote.getName(), emote));
	}
	
	@Override
	public String call() throws Exception {
		
		LinkedList<String> results = new LinkedList<>();
		Optional<Group> group = Avesbot.getStatementManager().getUsersActiveGroup(guild, member);
		byte difficulty = 0;
		
		var attribute = Optional.of(Attribute.valueOf(this.commandPars.get("attribute").getAsString().toUpperCase()));
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		
		if(group.isEmpty()) {
			return GroupCallable.I18N.getTranslation(settings.locale(), "errorNoActiveGroup");
		} else if(attribute.isEmpty()) {
			return GroupCallable.I18N.getTranslation(settings.locale(), "errorNoAttribute");
		} else {
			
			RoleplayCharacter[] charas = Avesbot.getStatementManager().getGroupMemberList(group.get());
			
			if(charas.length == 0)
				return GroupCallable.I18N.getTranslation(settings.locale(), "errorNoGroupMember");
			
			for(RoleplayCharacter chara : charas) {
				results.add(rollAttribute(settings, emoteMap, chara, attribute.get(), difficulty));
			}
			
			return results.stream().collect(Collectors.joining("\n\n"));
		}
	}
}