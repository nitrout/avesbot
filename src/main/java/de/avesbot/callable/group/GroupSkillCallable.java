package de.avesbot.callable.group;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import de.avesbot.Avesbot;
import de.avesbot.model.Ability;
import de.avesbot.model.Group;
import de.avesbot.model.Tradition;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.util.LevenshteinHelper;
import de.avesbot.callable.RoleplayCharacterRoll;
import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable to execute a skill trial for all members of a group.
 * @author Nitrout
 */
public class GroupSkillCallable extends GroupCallable implements RoleplayCharacterRoll {
	
	protected static final I18n I18N = GroupCallable.I18N;
	
	static {
		SubcommandData subcommand = buildTranslatedSubcommand(I18N, "groupSkill", "groupSkillDescription");
		
		OptionData skillOption = buildTranslatedOption(I18N, OptionType.STRING, "skillOption", "skillOptionDescription", true);
		OptionData difficultyOption = buildTranslatedOption(I18N, OptionType.INTEGER, "difficultyOption", "difficultyOptionDescription", false);
		OptionData traditionOption = buildTranslatedOption(I18N, OptionType.STRING, "traditionOption", "traditionOptionDescription", false);
		
		subcommand.addOptions(skillOption, difficultyOption, traditionOption);
		
		COMMAND.addSubcommands(subcommand);
	}
	
	private final HashMap<String, Emoji> emoteMap = new HashMap<>();
	
	/**
	 * Creates a new GroupSkillCallable.
	 * @param event 
	 */
	public GroupSkillCallable(SlashCommandInteractionEvent event) {
		super(event);
		guild.getEmojis().stream().forEach(emote -> emoteMap.put(emote.getName(), emote));
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
			return I18N.getTranslation(settings.locale(), "errorNoActiveGroup");
		} else {
			RoleplayCharacter[] charas = Avesbot.getStatementManager().getGroupMemberList(group.get());
			
			if(charas.length == 0)
				return I18N.getTranslation(settings.locale(), "errorNoGroupMember");
			
			// get the best matching skill
			String[] skillbook = Avesbot.getStatementManager().getGroupAbilityList(group.get());
			String skillStr = LevenshteinHelper.geteClosestSubjectIgnoreCase(fullSearch, skillbook);
			
			for(RoleplayCharacter chara : charas) {
				Optional<Ability> ability = Avesbot.getStatementManager().getCharacterAbility(chara, skillStr, rep);
				if(ability.isPresent()) {
					results.add(rollSKill(settings, emoteMap, chara, ability.get(), difficulty, pars));
				}
			}
			
			return results.stream().collect(Collectors.joining("\n\n"));
		}
	}
}