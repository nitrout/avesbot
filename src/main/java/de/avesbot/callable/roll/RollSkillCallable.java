package de.avesbot.callable.roll;

import de.avesbot.Avesbot;
import de.avesbot.callable.RoleplayCharacterRoll;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Ability;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Tradition;
import de.avesbot.util.LevenshteinHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Optional;

/**
 * A callable to execute a trial for a character's skill.
 * @author Nitrout
 */
public class RollSkillCallable extends RollCallable implements RoleplayCharacterRoll {
	
	protected static final I18n I18N = RollCallable.I18N;
	public static final SubcommandData SUBCOMMAND;
	
	static {
		
		SUBCOMMAND = buildTranslatedSubcommand(I18N, "rollSkill", "rollSkillDescription");
		
		OptionData skillOption = buildTranslatedOption(I18N, OptionType.STRING, "skillOption", "skillOptionDescription", true);
		OptionData difficultyOption = buildTranslatedOption(I18N, OptionType.INTEGER, "difficultyOption", "difficultyOptionDescription", false);
		OptionData coverageOption = buildTranslatedOption(I18N, OptionType.STRING, "coverageOption", "coverageOptionDescription", false);
		OptionData traditionOption = buildTranslatedOption(I18N, OptionType.STRING, "traditionOption", "traditionOptionDescription", false);
		traditionOption.addChoices(Tradition.optionChoices());
		
		SUBCOMMAND.addOptions(skillOption, difficultyOption, coverageOption, traditionOption);
	}
	
	/**
	 * Creates a new RollSkillCallable.
	 * @param event command event
	 */
	public RollSkillCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		if(chara.isEmpty())
			return I18N.getTranslation(settings.locale(), "errorNoActiveCharacter");

		final var fullSearch = this.commandPars.get("skill").getAsString();
		if (fullSearch.isEmpty())
			return I18N.getTranslation(settings.locale(), "errorEmptySkill");

		final var difficulty = Optional.ofNullable(this.commandPars.get("difficulty"))
				.map(OptionMapping::getAsInt)
				.map(Integer::byteValue)
				.orElse((byte) 0);
		final var coverages = Optional.ofNullable(this.commandPars.get("coverage"))
				.map(OptionMapping::getAsString)
				.map(cover -> cover.split("\\s"))
				.orElse(new String[0]);
		final var rep = Optional.ofNullable(this.commandPars.get("tradition"))
				.map(OptionMapping::getAsString)
				.map(String::toUpperCase)
				.map(Tradition::valueOf)
				.orElse(Tradition.NONE);

		String[] skills = Avesbot.getStatementManager().getCharacterAbilityList(chara.get());

		// get the closest skill
		String skillStr = LevenshteinHelper.geteClosestSubjectIgnoreCase(fullSearch, skills);
		Optional<Ability> ability = Avesbot.getStatementManager().getCharacterAbility(chara.get(), skillStr, rep);

		return ability.map(a -> rollSkill(settings, emoteMap, chara.get(), a, difficulty, coverages))
				.orElse(I18N.getTranslation(settings.locale(), "errorUnknownAbility"));
	}
}