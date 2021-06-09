package de.avesbot.callable.character;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.RoleplayCharacter;

/**
 * A callable for character deletion.
 * @author Nitrout
 */
public class CharacterDeleteCallable extends CharacterCallable {
	
	/**
	 * Creates a new DeleteCallable.
	 * @param event
	 */
	public CharacterDeleteCallable(SlashCommandEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String charaName = this.commandPars.get("name").getAsString();
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getRolePlayCharacterByName(member, charaName);
		
		if(chara.isPresent()) {
			boolean success = Avesbot.getStatementManager().deleteRoleplayCharacter(chara.get());
			if(success) {
				return I18n.getInstance().format(settings.getLocale(), "characterDeleted", chara.get().getName());
			} else {
				return I18n.getInstance().getString(settings.getLocale(), "errorCharacterDeletion");
			}
		} else {
			return I18n.getInstance().getString(settings.getLocale(), "errorUnknownCharacter");
		}
	}
}