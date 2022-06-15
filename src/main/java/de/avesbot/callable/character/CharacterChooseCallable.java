package de.avesbot.callable.character;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.RoleplayCharacter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Callable to select an active character.
 * @author Nitrout
 */
public class CharacterChooseCallable extends CharacterCallable {
	
	/**
	 * Creates a new choose character callable.
	 * @param event
	 */
	public CharacterChooseCallable(SlashCommandInteractionEvent event) {
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
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		
		if(success && chara.isPresent())
			result = I18n.getInstance().format(settings.locale(), "characterChosen", chara.get().name());
		else
			result = I18n.getInstance().getString(settings.locale(), "errorUnknownCharacter");
		
		return result;
	}
	
}