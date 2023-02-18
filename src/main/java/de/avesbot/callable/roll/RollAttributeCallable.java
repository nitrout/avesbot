package de.avesbot.callable.roll;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.callable.RoleplayCharacterRoll;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Attribute;
import de.avesbot.model.RoleplayCharacter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Executes an attribute trial for the character.
 * @author Nitrout
 */
public class RollAttributeCallable extends RollCallable implements RoleplayCharacterRoll {
	
	protected static final I18n I18N = new I18n("de.avesbot.i18n.roll");
	
	static {
		SubcommandData subcommand = buildTranslatedSubcommand(I18N, "rollAttribute", "rollAttributeDescription");

		OptionData attributeOption = buildTranslatedOption(I18N, OptionType.STRING, "attributeOption", "attributeOptionDescription", true);
		attributeOption.addChoices(Attribute.OPTION_CHOICES);
		
		OptionData difficultyOption = buildTranslatedOption(I18N, OptionType.INTEGER, "difficultyOption", "difficultyOptionDescription", false);

		subcommand.addOptions(attributeOption, difficultyOption);
		
		COMMAND.addSubcommands(subcommand);
	}
	
	/**
	 * Creates a new attribute trial callable.
	 * @param event
	 */
	public RollAttributeCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	/**
	 * Executes the callable.
	 * @return the result
	 * @throws Exception 
	 */
	@Override
	public String call() throws Exception {
		
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		Optional<Attribute> attribute = Optional.empty();
		byte difficulty = 0;
		
		attribute = Optional.of(Attribute.valueOf(this.commandPars.get("attribute").getAsString().toUpperCase()));
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		
		if(chara.isEmpty())
			return I18N.getTranslation(settings.locale(), "errorNoActiveCharacter");
		if(attribute.isEmpty())
			return I18N.getTranslation(settings.locale(), "errorNoAttribute");
		
		return rollAttribute(settings, emoteMap, chara.get(), attribute.get(), difficulty);
	}
}