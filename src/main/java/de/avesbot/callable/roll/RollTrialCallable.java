package de.avesbot.callable.roll;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.model.Attribute;
import de.avesbot.model.Tradition;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Trial;
import de.avesbot.callable.RoleplayCharacterRoll;
import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable to execute a custom trial for a character.
 * @author Nitrout
 */
public class RollTrialCallable extends RollCallable implements RoleplayCharacterRoll {
	
	protected static final I18n I18N = new I18n("de.avesbot.i18n.roll");
	
	static {
		SubcommandData subcommand = buildTranslatedSubcommand(I18N, "rollTrial", "rollTrialDescription");
		
		OptionData attribute1Option = buildTranslatedOption(I18N, OptionType.STRING, "attribute1Option", "attribute1OptionDescription", true);
		attribute1Option.addChoices(Attribute.OPTION_CHOICES);
		
		OptionData attribute2Option = buildTranslatedOption(I18N, OptionType.STRING, "attribute2Option", "attribute2ptionDescription", true);
		attribute2Option.addChoices(Attribute.OPTION_CHOICES);
		
		OptionData attribute3Option = buildTranslatedOption(I18N, OptionType.STRING, "attribute3Option", "attribute3OptionDescription", true);
		attribute3Option.addChoices(Attribute.OPTION_CHOICES);
		
		OptionData skillRatingOption = buildTranslatedOption(I18N, OptionType.INTEGER, "skillRatingOption", "skillRatingOptionDescription", true);
		
		OptionData spellOption = buildTranslatedOption(I18N, OptionType.STRING, "spellOption", "spellOptionDescription", false);
		spellOption.addChoices(buildTranslatedChoice(I18N, "noChoice", "false"), buildTranslatedChoice(I18N, "yesChoice", "true"));
		
		OptionData difficultyOption = buildTranslatedOption(I18N, OptionType.INTEGER, "difficultyOption", "difficultyOptionDescription", false);
		
		subcommand.addOptions(attribute1Option, attribute2Option, attribute3Option, skillRatingOption, spellOption, difficultyOption);
		
		COMMAND.addSubcommands(subcommand);
	}
	
	/**
	 * Creates a new RollTrialCallable.
	 * @param event 
	 */
	public RollTrialCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		Trial trial;
		boolean spell = false;
		int sr = 0;
		byte difficulty = 0;
		
		Attribute attribute1 = Attribute.valueOf(this.commandPars.get("attribute1").getAsString().toUpperCase());
		Attribute attribute2 = Attribute.valueOf(this.commandPars.get("attribute2").getAsString().toUpperCase());
		Attribute attribute3 = Attribute.valueOf(this.commandPars.get("attribute3").getAsString().toUpperCase());
		trial = new Trial(attribute1, attribute2, attribute3);
		if(this.commandPars.containsKey("spell"))
			spell = this.commandPars.get("spell").getAsBoolean();
		if(this.commandPars.containsKey("sr"))
			sr = (int)this.commandPars.get("sr").getAsLong();
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		
		if(chara.isEmpty()) {
			return I18N.getTranslation(settings.locale(), "errorNoActiveCharacter");
		} else {
			
			String abilityName = trial.toString();
			return rollSkill(settings, emoteMap, chara.get(), abilityName, Tradition.NONE, trial, spell, sr, difficulty);
		}
	}
}