package de.avesbot.callable.character;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.model.Ability;
import de.avesbot.model.Attribute;
import de.avesbot.model.Tradition;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Trial;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable to learn a new ability for the current character.
 * @author Nitrout
 */
public class CharacterLearnCallable extends CharacterCallable {
	
	public static final SubcommandData SUBCOMMAND;
	
	static {
		
		SUBCOMMAND = new SubcommandData(I18N.getTranslation("characterLearn"), I18N.getTranslation("characterLearnAbilityOptionDescription"));
		SUBCOMMAND.setNameLocalizations(I18N.getLocalizations("characterLearn"));
		SUBCOMMAND.setDescriptionLocalizations(I18N.getLocalizations("characterLearnAbilityOptionDescription"));
		
		OptionData abilityOption = new OptionData(OptionType.STRING, I18N.getTranslation("characterAbilityOption"), I18N.getTranslation("characterLearnAbilityOptionDescription"), true);
		abilityOption.setNameLocalizations(I18N.getLocalizations("characterAbilityOption"));
		abilityOption.setDescriptionLocalizations(I18N.getLocalizations("characterLearnAbilityOptionDescription"));
		
		OptionData attribute1Option = new OptionData(OptionType.STRING, I18N.getTranslation("characterAttribute1Option"), I18N.getTranslation("characterLearnAttribute1OptionDescription"), true);
		attribute1Option.setNameLocalizations(I18N.getLocalizations("characterAttribute1Option"));
		attribute1Option.setDescriptionLocalizations(I18N.getLocalizations("characterLearnAttribute1OptionDescription"));
		attribute1Option.addChoices(Attribute.OPTION_CHOICES);
		
		OptionData attribute2Option = new OptionData(OptionType.STRING, I18N.getTranslation("characterAttribute2Option"), I18N.getTranslation("characterLearnAttribute2OptionDescription"), true);
		attribute2Option.setNameLocalizations(I18N.getLocalizations("characterAttribute2Option"));
		attribute2Option.setDescriptionLocalizations(I18N.getLocalizations("characterLearnAttribute2OptionDescription"));
		attribute2Option.addChoices(Attribute.OPTION_CHOICES);
		
		OptionData attribute3Option = new OptionData(OptionType.STRING, I18N.getTranslation("characterAttribute3Option"), I18N.getTranslation("characterLearnAttribute3OptionDescription"), true);
		attribute3Option.setNameLocalizations(I18N.getLocalizations("characterAttribute3Option"));
		attribute3Option.setDescriptionLocalizations(I18N.getLocalizations("characterLearnAttribute3OptionDescription"));
		attribute3Option.addChoices(Attribute.OPTION_CHOICES);
		
		OptionData abilityRatingOption = new OptionData(OptionType.INTEGER, I18N.getTranslation("characterAbilityRatingOption"), I18N.getTranslation("characterLearnAbilityRatingOptionDescription"), true);
		abilityRatingOption.setNameLocalizations(I18N.getLocalizations("characterAbilityRatingOption"));
		abilityRatingOption.setDescriptionLocalizations(I18N.getLocalizations("characterLearnAbilityRatingOptionDescription"));
		
		OptionData typeOption = new OptionData(OptionType.STRING, I18N.getTranslation("characterTypeOption"), I18N.getTranslation("characterLearnTypeOptionDescription"));
		typeOption.setNameLocalizations(I18N.getLocalizations("characterTypeOption"));
		typeOption.setDescriptionLocalizations(I18N.getLocalizations("characterLearnTypeOptionDescription"));
		typeOption.addChoices(Ability.Type.OPTION_CHOICES);
		
		OptionData traditionOption = new OptionData(OptionType.STRING, I18N.getTranslation("characterTraditionOption"), I18N.getTranslation("characterLearnTraditionOptionDescription"));
		traditionOption.setNameLocalizations(I18N.getLocalizations("characterTraditionOption"));
		traditionOption.setDescriptionLocalizations(I18N.getLocalizations("characterLearnTraditionOptionDescription"));
		traditionOption.addChoices(Tradition.OPTION_CHOICES);
		
		SUBCOMMAND.addOptions(
			abilityOption,
			attribute1Option,
			attribute2Option,
			attribute3Option,
			abilityRatingOption,
			typeOption,
			traditionOption
		);
	}
	
	/**
	 * Creates a new TrainCallable.
	 * @param event 
	 */
	public CharacterLearnCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String result = "";
		
		Ability.Type type = Ability.Type.TALENT;
		Tradition tradition = Tradition.NONE;
		byte sr = 0;
		
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		
		String abilityName = this.commandPars.get("ability").getAsString();
		Attribute attribute1 = Attribute.abbrevationValueOf(this.commandPars.get("attribute1").getAsString());
		Attribute attribute2 = Attribute.abbrevationValueOf(this.commandPars.get("attribute2").getAsString());
		Attribute attribute3 = Attribute.abbrevationValueOf(this.commandPars.get("attribute3").getAsString());
		Trial trial = new Trial(attribute1, attribute2, attribute3);
		sr = (byte)this.commandPars.get("ar").getAsLong();
		if(this.commandPars.containsKey("type"))
			type = Ability.Type.valueOf(this.commandPars.get("type").getAsString());
		if(this.commandPars.containsKey("tradition"))
			tradition = Tradition.valueOf(this.commandPars.get("tradition").getAsString().toUpperCase());
		
		if(chara.isPresent()) {
			
			Ability ability = new Ability(abilityName, tradition, trial, sr, type);
			Avesbot.getStatementManager().insertAbility(chara.get(), ability);
			result = I18N.format(settings.locale(), "characterLearned", chara.get().name(), type, abilityName, tradition.name(), sr);
		}
		else {
			result = I18N.getTranslation(settings.locale(), "errorNoActiveCharacter");
		}
		
		return result;
	}
	
}