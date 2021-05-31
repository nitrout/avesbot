package de.avesbot.callable.roll;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Attribute;
import de.avesbot.model.RolePlayCharacter;
import de.avesbot.util.RollHelper;

/**
 * Executes an attribute trial for the character.
 * @author Nitrout
 */
public class RollAttributeCallable extends RollCallable {
	
	/**
	 * Creates a new attribute trial callable.
	 * @param event
	 */
	public RollAttributeCallable(SlashCommandEvent event) {
		super(event);
	}
	
	/**
	 * Executes the callable.
	 * @return the result
	 * @throws Exception 
	 */
	@Override
	public String call() throws Exception {
		
		String result;
		Optional<RolePlayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		Optional<Attribute> attribute = Optional.empty();
		byte difficulty = 0;
		
		attribute = Optional.of(Attribute.valueOf(this.commandPars.get("attribute").getAsString().toUpperCase()));
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		
		if(chara.isEmpty()) {
			result = I18n.getInstance().getString(settings.getLocale(), "errorNoActiveCharacter");
		} else if(attribute.isEmpty()) {
			result = I18n.getInstance().getString(settings.getLocale(), "errorNoAttribute");
		} else {
			result = RollHelper.rollAttribute(settings, emoteMap, chara.get(), attribute.get(), difficulty);
		}
		
		return result;
	}
}