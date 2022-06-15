package de.avesbot.callable.group;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Emote;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Attribute;
import de.avesbot.model.Group;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.callable.RoleplayCharacterRoll;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * A callable to execute an attribute trial for all members of a group.
 * @author Nitrout
 */
public class GroupAttributeCallable extends GroupCallable implements RoleplayCharacterRoll {
	
	private final HashMap<String, Emote> emoteMap = new HashMap<>();
	
	/**
	 * Creates a new GroupAttributeCallable.
	 * @param event 
	 */
	public GroupAttributeCallable(SlashCommandInteractionEvent event) {
		super(event);
		
		guild.getEmotes().stream().forEach(emote -> emoteMap.put(emote.getName(), emote));
	}
	
	@Override
	public String call() throws Exception {
		
		LinkedList<String> results = new LinkedList<>();
		Optional<Group> group = Avesbot.getStatementManager().getUsersActiveGroup(guild, member);
		Optional<Attribute> attribute = Optional.empty();
		byte difficulty = 0;
		
		attribute = Optional.of(Attribute.valueOf(this.commandPars.get("attribute").getAsString().toUpperCase()));
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		
		if(group.isEmpty()) {
			return I18n.getInstance().getString(settings.locale(), "errorNoActiveGroup");
		} else if(attribute.isEmpty()) {
			return I18n.getInstance().getString(settings.locale(), "errorNoAttribute");
		} else {
			
			RoleplayCharacter[] charas = Avesbot.getStatementManager().getGroupMemberList(group.get());
			
			if(charas.length == 0)
				return I18n.getInstance().getString(settings.locale(), "errorNoGroupMember");
			
			for(RoleplayCharacter chara : charas) {
				results.add(rollAttribute(settings, emoteMap, chara, attribute.get(), difficulty));
			}
			
			return results.stream().collect(Collectors.joining("\n\n"));
		}
	}
}