package de.avesbot.callable.character;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import de.avesbot.Avesbot;
import de.avesbot.model.RoleplayCharacter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable that lists every available character of an user.
 * @author Nitrout
 */
public class CharacterListCallable extends CharacterCallable {
	
	static {
		
		SubcommandData listCommand = new SubcommandData(I18N.getTranslation("characterList"), I18N.getTranslation("characterListDescription"));
		listCommand.setNameLocalizations(I18N.getLocalizations("characterList"));
		listCommand.setDescriptionLocalizations(I18N.getLocalizations("characterListDescription"));
		
		COMMAND.addSubcommands(listCommand);
	}
	
	/**
	 * Creates a new ListCallable.
	 * @param event 
	 */
	public CharacterListCallable(SlashCommandInteractionEvent event) {
		super(event);
	}

	@Override
	public String call() throws Exception {
		
		RoleplayCharacter[] chars = Avesbot.getStatementManager().getRolePlayCharacterList(this.member);
		
		return Stream.of(chars).map(RoleplayCharacter::toString).collect(Collectors.joining("\n"));
	}
}