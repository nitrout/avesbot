package de.avesbot.callable.character;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import de.avesbot.Avesbot;
import de.avesbot.model.RoleplayCharacter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * A callable that lists every available character of an user.
 * @author Nitrout
 */
public class CharacterListCallable extends CharacterCallable {
	
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