package de.avesbot.callable.roll;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Attribute;
import de.avesbot.model.Tradition;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Trial;
import de.avesbot.callable.RoleplayCharacterRoll;

/**
 * A callable to execute a custom trial for a character.
 * @author Nitrout
 */
public class RollTrialCallable extends RollCallable implements RoleplayCharacterRoll {
	
	/**
	 * Creates a new RollTrialCallable.
	 * @param event 
	 */
	public RollTrialCallable(SlashCommandEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		Trial trial;
		boolean spell = false;
		int sr = 0;
		byte difficulty = 0;
		
		Attribute attribute1 = Attribute.valueOf(this.commandPars.get("attribute1").getAsString().toUpperCase());
		Attribute attribute2 = Attribute.valueOf(this.commandPars.get("attribute2").getAsString().toUpperCase());
		Attribute attribute3 = Attribute.valueOf(this.commandPars.get("attribute3").getAsString().toUpperCase());
		trial = new Trial(attribute1, attribute2, attribute3);
		if(this.commandPars.containsKey("spell"))
			spell = this.commandPars.get("spell").getAsBoolean();
		if(this.commandPars.containsKey("sr"))
			sr = (int)this.commandPars.get("sr").getAsLong();
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		
		if(chara.isEmpty()) {
			return I18n.getInstance().getString(settings.getLocale(), "errorNoActiveCharacter");
		} else {
			
			String abilityName = trial.toString();
			return rollSkill(settings, emoteMap, chara.get(), abilityName, Tradition.NONE, trial, spell, sr, difficulty);
		}
	}
}