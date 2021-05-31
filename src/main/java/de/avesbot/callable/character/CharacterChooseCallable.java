package de.avesbot.callable.character;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.RolePlayCharacter;

/**
 * Callable to select an active character.
 * @author Nitrout
 */
public class CharacterChooseCallable extends CharacterCallable {
	
	/**
	 * Creates a new choose character callable.
	 * @param event
	 */
	public CharacterChooseCallable(SlashCommandEvent event) {
		super(event);
	}

	/**
	 * Executes the character selection callable.
	 * @return the result of the selection
	 * @throws Exception 
	 */
	@Override
	public String call() throws Exception {
		String result = "";
		
		String name = this.commandPars.get("name").getAsString();
		boolean success = Avesbot.getStatementManager().enableUsersRoleplayCharacter(member, name);
		Optional<RolePlayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		
		if(success && chara.isPresent())
			result = I18n.getInstance().format(settings.getLocale(), "characterChosen", chara.get().getName());
		else
			result = I18n.getInstance().getString(settings.getLocale(), "errorUnknownCharacter");
		
		return result;
	}
	
}