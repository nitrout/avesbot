package de.avesbot.callable.character;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Ability;
import de.avesbot.model.Attribute;
import de.avesbot.model.Tradition;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Trial;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * A callable to train a new skill for the current character.
 * @author Nitrout
 */
public class CharacterTrainCallable extends CharacterCallable {
	
	/**
	 * Creates a new TrainCallable.
	 * @param event 
	 */
	public CharacterTrainCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String result = "";
		
		Ability.Type type = Ability.Type.TALENT;
		Tradition tradition = Tradition.NONE;
		byte sr = 0;
		
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		
		String abilityName = this.commandPars.get("skill").getAsString();
		Attribute attribute1 = Attribute.abbrevationValueOf(this.commandPars.get("attribute1").getAsString());
		Attribute attribute2 = Attribute.abbrevationValueOf(this.commandPars.get("attribute2").getAsString());
		Attribute attribute3 = Attribute.abbrevationValueOf(this.commandPars.get("attribute3").getAsString());
		Trial trial = new Trial(attribute1, attribute2, attribute3);
		sr = (byte)this.commandPars.get("sr").getAsLong();
		if(this.commandPars.containsKey("type"))
			type = Ability.Type.valueOf(this.commandPars.get("type").getAsString());
		if(this.commandPars.containsKey("tradition"))
			tradition = Tradition.valueOf(this.commandPars.get("tradition").getAsString().toUpperCase());
		
		if(chara.isPresent()) {
			
			Ability ability = new Ability(abilityName, tradition, trial, sr, type);
			Avesbot.getStatementManager().insertAbility(chara.get(), ability);
			result = I18n.getInstance().format(settings.locale(), "characterTrained", chara.get().name(), type, abilityName, tradition.name(), sr);
		}
		else {
			result = I18n.getInstance().getString(settings.locale(), "errorNoActiveCharacter");
		}
		
		return result;
	}
	
}