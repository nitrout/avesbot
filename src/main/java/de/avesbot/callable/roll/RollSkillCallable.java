package de.avesbot.callable.roll;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.model.Ability;
import de.avesbot.model.Tradition;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.util.LevenshteinHelper;
import de.avesbot.callable.RoleplayCharacterRoll;
import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable to execute a trial for a character's skill.
 * @author Nitrout
 */
public class RollSkillCallable extends RollCallable implements RoleplayCharacterRoll {
	
	protected static final I18n I18N = new I18n("de.avesbot.i18n.roll");
	
	static {
		
		SubcommandData subcommand = buildTranslatedSubcommand(I18N, "rollSkill", "rollSkillDescription");
		
		OptionData skillOption = buildTranslatedOption(I18N, OptionType.STRING, "skillOption", "skillOptionDescription", true);
		OptionData difficultyOption = buildTranslatedOption(I18N, OptionType.INTEGER, "difficultyOption", "difficultyOptionDescription", false);
		OptionData coverageOption = buildTranslatedOption(I18N, OptionType.STRING, "coverageOption", "coverageOptionDescription", false);
		OptionData traditionOption = buildTranslatedOption(I18N, OptionType.STRING, "traditionOption", "traditionOptionDescription", false);
		traditionOption.addChoices(Tradition.OPTION_CHOICES);
		
		subcommand.addOptions(skillOption, difficultyOption, coverageOption, traditionOption);
		
		COMMAND.addSubcommands(subcommand);
	}
	
	/**
	 * Creates a new RollSkillCallable.
	 * @param event 
	 */
	public RollSkillCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		String fullSearch = "";
		Tradition rep = Tradition.NONE;
		String[] coverages = new String[]{};
		byte difficulty = 0;
		
		fullSearch = this.commandPars.get("skill").getAsString();
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		if(this.commandPars.containsKey("coverage"))
			coverages = this.commandPars.get("coverage").getAsString().split(" ");
		if(this.commandPars.containsKey("tradition"))
			rep = Tradition.valueOf(this.commandPars.get("tradition").getAsString().toUpperCase());
		
		if(chara.isEmpty())
			return I18N.getTranslation(settings.locale(), "errorNoActiveCharacter");
		if(fullSearch.length() == 0)
			return I18N.getTranslation(settings.locale(), "errorEmptySkill");
		
		String[] skillbook = Avesbot.getStatementManager().getCharacterAbilityList(chara.get());

		// get the closest skill
		String skillStr = LevenshteinHelper.geteClosestSubjectIgnoreCase(fullSearch, skillbook);
		Optional<Ability> ability = Avesbot.getStatementManager().getCharacterAbility(chara.get(), skillStr, rep);

		if(ability.isPresent()) {
			return rollSKill(settings, emoteMap, chara.get(), ability.get(), difficulty, coverages);
		} else {
			return I18N.getTranslation(settings.locale(), "errorUnknownAbility");
		}
	}
}