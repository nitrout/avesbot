package de.avesbot.callable.group;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Ability;
import de.avesbot.model.Group;
import de.avesbot.model.Tradition;
import de.avesbot.model.RolePlayCharacter;
import de.avesbot.util.LevenshteinHelper;
import de.avesbot.callable.RoleplayCharacterRoll;

/**
 * A callable to execute a skill trial for all members of a group.
 * @author Nitrout
 */
public class GroupSkillCallable extends GroupCallable implements RoleplayCharacterRoll {
	
	private final HashMap<String, Emote> emoteMap = new HashMap<>();
	
	/**
	 * Creates a new GroupSkillCallable.
	 * @param event 
	 */
	public GroupSkillCallable(SlashCommandEvent event) {
		super(event);
		guild.getEmotes().stream().forEach(emote -> emoteMap.put(emote.getName(), emote));
	}
	
	@Override
	public String call() throws Exception {
		
		LinkedList<String> results = new LinkedList<>();
		Optional<Group> group = Avesbot.getStatementManager().getUsersActiveGroup(guild, member);
		
		String fullSearch = "";
		Tradition rep = Tradition.NONE;
		byte difficulty = 0;
		
		fullSearch = this.commandPars.get("skill").getAsString();
		if(this.commandPars.containsKey("tradition"))
			rep = Tradition.valueOf(this.commandPars.get("tradition").getAsString());
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		
		if(group.isEmpty()) {
			return I18n.getInstance().getString(settings.getLocale(), "errorNoActiveGroup");
		} else {
			RolePlayCharacter[] charas = Avesbot.getStatementManager().getGroupMemberList(group.get());
			
			if(charas.length == 0)
				return I18n.getInstance().getString(settings.getLocale(), "errorNoGroupMember");
			
			// get the best matching skill
			String[] skillbook = Avesbot.getStatementManager().getGroupAbilityList(group.get());
			String skillStr = LevenshteinHelper.geteClosestSubjectIgnoreCase(fullSearch, skillbook);
			
			for(RolePlayCharacter chara : charas) {
				Optional<Ability> ability = Avesbot.getStatementManager().getCharacterAbility(chara, skillStr, rep);
				if(ability.isPresent()) {
					results.add(rollSKill(settings, emoteMap, chara, ability.get(), difficulty, pars));
				}
			}
			
			return results.stream().collect(Collectors.joining("\n\n"));
		}
	}
}