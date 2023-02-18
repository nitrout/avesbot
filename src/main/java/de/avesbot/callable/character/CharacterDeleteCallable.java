package de.avesbot.callable.character;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.model.RoleplayCharacter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable for character deletion.
 * @author Nitrout
 */
public class CharacterDeleteCallable extends CharacterCallable {
	
	static {
		
		SubcommandData deleteCommand = new SubcommandData(I18N.getTranslation("characterDelete"), I18N.getTranslation("characterDeleteDescription"));
		deleteCommand.setNameLocalizations(I18N.getLocalizations("characterDelete"));
		deleteCommand.setDescriptionLocalizations(I18N.getLocalizations("characterDeleteDescription"));
		
		OptionData nameOption = new OptionData(OptionType.STRING, I18N.getTranslation("characterNameOption"), I18N.getTranslation("characterDeleteNameOptionDescription"), true);
		nameOption.setNameLocalizations(I18N.getLocalizations("characterNameOption"));
		nameOption.setDescriptionLocalizations(I18N.getLocalizations("characterDeleteNameOptionDescription"));
		deleteCommand.addOptions(nameOption);
		
		COMMAND.addSubcommands(deleteCommand);
	}
	
	/**
	 * Creates a new DeleteCallable.
	 * @param event
	 */
	public CharacterDeleteCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String charaName = this.commandPars.get("name").getAsString();
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getRolePlayCharacterByName(member, charaName);
		
		if(chara.isPresent()) {
			boolean success = Avesbot.getStatementManager().deleteRoleplayCharacter(chara.get());
			if(success) {
				return I18N.format(settings.locale(), "characterDeleted", chara.get().name());
			} else {
				return I18N.getTranslation(settings.locale(), "errorCharacterDeletion");
			}
		} else {
			return I18N.getTranslation(settings.locale(), "errorUnknownCharacter");
		}
	}
}