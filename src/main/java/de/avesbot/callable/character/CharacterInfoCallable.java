package de.avesbot.callable.character;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.model.Ability;
import de.avesbot.model.Tradition;
import de.avesbot.model.RoleplayCharacter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable to print character's or one of his abilitie's information.
 * @author Nitrout
 */
public class CharacterInfoCallable extends CharacterCallable {
	
	static {
		
		SubcommandData infoCommand = new SubcommandData(I18N.getTranslation("characterInfo"), I18N.getTranslation("characterInfoDescription"));
		infoCommand.setNameLocalizations(I18N.getLocalizations("characterInfo"));
		infoCommand.setDescriptionLocalizations(I18N.getLocalizations("characterInfoDescription"));
		
		OptionData abilityOption = new OptionData(OptionType.STRING, I18N.getTranslation("characterAbilityOption"), I18N.getTranslation("characterInfoAbilityOptionDescription"));
		abilityOption.setNameLocalizations(I18N.getLocalizations("characterAbilityOption"));
		abilityOption.setDescriptionLocalizations(I18N.getLocalizations("characterInfoAbilityOptionDescription"));
		
		OptionData traditionOption = new OptionData(OptionType.STRING, I18N.getTranslation("characterTraditionOption"), I18N.getTranslation("characterInfoTraditionOptionDescription"));
		traditionOption.setNameLocalizations(I18N.getLocalizations("characterTraditionOption"));
		traditionOption.setDescriptionLocalizations(I18N.getLocalizations("characterInfoTraditionOptionDescription"));
		traditionOption.addChoices(Tradition.OPTION_CHOICES);
		
		infoCommand.addOptions(abilityOption, traditionOption);
		
		COMMAND.addSubcommands(infoCommand);
	}
	
	/**
	 * Creates a new InfoCallable.
	 * @param event 
	 */
	public CharacterInfoCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String result = "";
		
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		if(chara.isEmpty()) {
			result = I18N.getTranslation(settings.locale(), "errorNoActiveCharacter");
		} else if(!this.commandPars.containsKey("ability")) {
			result = chara.get().toString();
		} else {
			
			String abilityStr = this.commandPars.get("ability").getAsString();
			Tradition rep = Tradition.NONE;
			if(this.commandPars.containsKey("tradition"))
				rep = Tradition.mappedValueOf(this.commandPars.get("tradition").getAsString());
			
			Optional<Ability> ability = Avesbot.getStatementManager().getCharacterAbility(chara.get(), abilityStr, rep);
			
			if(ability.isPresent()) {
				result = I18N.format(settings.locale(), "characterAbilityInfo", chara.get().name(), ability.get());
			} else {
				result = I18N.getTranslation(settings.locale(), "errorUnknownAbility");
			}
		}
		
		return result;
	}
}