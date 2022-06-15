package de.avesbot.callable.character;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Ability;
import de.avesbot.model.Tradition;
import de.avesbot.model.RoleplayCharacter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * A callable to print character's or his skills information.
 * @author Nitrout
 */
public class CharacterInfoCallable extends CharacterCallable {
	
	/**
	 * Creates a new InfoCallable.
	 * @param event 
	 */
	public CharacterInfoCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String result = "";
		
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		if(chara.isEmpty()) {
			result = I18n.getInstance().getString(settings.locale(), "errorNoActiveCharacter");
		} else if(!this.commandPars.containsKey("skill")) {
			result = chara.get().toString();
		} else {
			
			String skill = this.commandPars.get("skill").getAsString();
			Tradition rep = Tradition.NONE;
			if(this.commandPars.containsKey("tradition"))
				rep = Tradition.mappedValueOf(this.commandPars.get("tradition").getAsString());
			
			Optional<Ability> ability = Avesbot.getStatementManager().getCharacterAbility(chara.get(), skill, rep);
			
			if(ability.isPresent()) {
				result = I18n.getInstance().format(settings.locale(), "characterAbilityInfo", chara.get().name(), ability.get());
			} else {
				result = I18n.getInstance().getString(settings.locale(), "errorUnknownAbility");
			}
		}
		
		return result;
	}
}