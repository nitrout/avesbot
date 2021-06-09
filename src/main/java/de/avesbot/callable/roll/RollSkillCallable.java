package de.avesbot.callable.roll;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Ability;
import de.avesbot.model.Tradition;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.util.LevenshteinHelper;
import de.avesbot.callable.RoleplayCharacterRoll;

/**
 * A callable to execute a trial for a character's skill.
 * @author Nitrout
 */
public class RollSkillCallable extends RollCallable implements RoleplayCharacterRoll {
	
	/**
	 * Creates a new RollSkillCallable.
	 * @param event 
	 */
	public RollSkillCallable(SlashCommandEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		String fullSearch = "";
		Tradition rep = Tradition.NONE;
		String[] coverages = new String[]{};
		byte difficulty = 0;
		
		fullSearch = this.commandPars.get("skill").getAsString();
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		if(this.commandPars.containsKey("coverage"))
			coverages = this.commandPars.get("coverage").getAsString().split(" ");
		if(this.commandPars.containsKey("tradition"))
			rep = Tradition.valueOf(this.commandPars.get("tradition").getAsString().toUpperCase());
		
		if(chara.isEmpty()) {
			return I18n.getInstance().getString(settings.getLocale(), "errorNoActiveCharacter");
		} else if(fullSearch.length() == 0) {
			return I18n.getInstance().getString(settings.getLocale(), "errorEmptySkill");
		} else {
			String[] skillbook = Avesbot.getStatementManager().getCharacterAbilityList(chara.get());
			
			// get the closest skill
			String skillStr = LevenshteinHelper.geteClosestSubjectIgnoreCase(fullSearch, skillbook);
			Optional<Ability> ability = Avesbot.getStatementManager().getCharacterAbility(chara.get(), skillStr, rep);
			
			if(ability.isPresent()) {
				return rollSKill(settings, emoteMap, chara.get(), ability.get(), difficulty, coverages);
			} else {
				return I18n.getInstance().getString(settings.getLocale(), "errorUnknownAbility");
			}
		}
	}
}