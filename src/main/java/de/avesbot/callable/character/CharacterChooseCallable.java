package de.avesbot.callable.character;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.model.RoleplayCharacter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Callable to select an active character.
 * @author Nitrout
 */
public class CharacterChooseCallable extends CharacterCallable {
	
	static {
		
		SubcommandData chooseSubcommand = new SubcommandData(I18N.getTranslation("characterChoose"), I18N.getTranslation("characterChooseDescription"));
		chooseSubcommand.setNameLocalizations(I18N.getLocalizations("characterChoose"));
		chooseSubcommand.setDescriptionLocalizations(I18N.getLocalizations("characterChooseDescription"));
		
		OptionData nameOption = new OptionData(OptionType.STRING, I18N.getTranslation("characterNameOption"), I18N.getTranslation("characterChooseNameOptionDescription"), true);
		nameOption.setNameLocalizations(I18N.getLocalizations("characterNameOption"));
		nameOption.setDescriptionLocalizations(I18N.getLocalizations("characterChooseNameOptionDescription"));
		chooseSubcommand.addOptions(nameOption);
		
		COMMAND.addSubcommands(chooseSubcommand);
	}
	
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
			result = I18N.format(settings.locale(), "characterChosen", chara.get().name());
		else
			result = I18N.getTranslation(settings.locale(), "errorUnknownCharacter");
		
		return result;
	}
	
}