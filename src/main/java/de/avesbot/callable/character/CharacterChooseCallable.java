package de.avesbot.callable.character;

import de.avesbot.Avesbot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Callable to select an active character.
 * @author Nitrout
 */
public class CharacterChooseCallable extends CharacterCallable {

	public static final SubcommandData SUBCOMMAND = new SubcommandData(I18N.getTranslation("characterChoose"), I18N.getTranslation("characterChooseDescription"));
	
	static {
		SUBCOMMAND.setNameLocalizations(I18N.getLocalizations("characterChoose"));
		SUBCOMMAND.setDescriptionLocalizations(I18N.getLocalizations("characterChooseDescription"));
		
		OptionData nameOption = new OptionData(OptionType.STRING, I18N.getTranslation("characterNameOption"), I18N.getTranslation("characterChooseNameOptionDescription"), true);
		nameOption.setNameLocalizations(I18N.getLocalizations("characterNameOption"));
		nameOption.setDescriptionLocalizations(I18N.getLocalizations("characterChooseNameOptionDescription"));
		SUBCOMMAND.addOptions(nameOption);
	}
	
	/**
	 * Creates a new choose character callable.
	 * @param event the called event
	 */
	public CharacterChooseCallable(SlashCommandInteractionEvent event) {
		super(event);
	}

	/**
	 * Executes the character selection callable.
	 * @return the result of the selection
	 */
	@Override
	public String call() {

		final var name = this.commandPars.get("name").getAsString();
		final var success = Avesbot.getStatementManager().enableUsersRoleplayCharacter(member, name);
		final var chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);

		return success && chara.isPresent()
				? I18N.format(settings.locale(), "characterChosen", chara.get().name())
				: I18N.getTranslation(settings.locale(), "errorUnknownCharacter");
	}
	
}