package de.avesbot.callable;

import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.*;
import de.avesbot.util.Formatter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

/**
 * Helper class to support the use of rolls for the different rulesets.
 * @author Nitrout
 */
public interface RoleplayCharacterRoll {

	I18n I18N = new I18n("de.avesbot.i18n.messages");
	String TAP_FORMAT = "%d %s*";
	String OUTCOME_SLIP = "outcomeSlip";
	String OUTCOME_SPLENDOR = "outcomeSplendor";
	String OUTCOME_SUCCESS = "outcomeSuccess";
	String OUTCOME_FAILURE = "outcomeFailure";

	/**
	 * Executes a skill roll.
	 * @param settings settings of the current guild
	 * @param emoteMap map for the dice emoticons
	 * @param chara the character the roll is executed for
	 * @param ability the ability the dices are rolled for
	 * @param difficulty the difficulty of the trial
	 * @param coverage the coverages used in the trial
	 * @return the string result of the roll
	 */
	default String rollSKill(GuildSetting settings, Map<String, Emoji> emoteMap, RoleplayCharacter chara, Ability ability, byte difficulty, String...coverage) {
		
		return rollSkill(settings, emoteMap, chara, ability.name(), ability.rep(), ability.trial(), ability.type() == Ability.Type.SPELL, ability.taw(), difficulty, coverage);
	}
	
	/**
	 * Executes a skill roll.
	 * @param settings settings of the current guild
	 * @param emoteMap map for the dice emoticons
	 * @param chara the character the roll is executed for
	 * @param abilityName the name of the used ability
	 * @param rep the used representation
	 * @param trial the trial used for the roll
	 * @param spell is the ability a spell
	 * @param taw the TaW of the ability
	 * @param difficulty the difficulty of the trial
	 * @param coverage the coverages used in the trial
	 * @return the string result of the roll
	 */
	default String rollSkill(GuildSetting settings, Map<String, Emoji> emoteMap, RoleplayCharacter chara, String abilityName, Tradition rep, Trial trial, boolean spell, int taw, byte difficulty, String...coverage) {
		
		String result;
		
		result = switch (chara.ruleset()) {
			case TDE4 -> rollSkillTde4(settings, emoteMap, chara, abilityName, rep, trial, spell, taw, difficulty, coverage);
			case TDE5 -> rollSkillTde5(settings, emoteMap, chara, abilityName, trial, spell, taw, difficulty, coverage);
			default -> I18N.getTranslation(settings.locale(), "errorUnknownRuleset");
		};
		
		return result;
	}
	
	/**
	 * Executes a skill roll for DSA4 ruleset.
	 * @param settings settings of the current guild
	 * @param emoteMap map for the dice emoticons
	 * @param chara the character the roll is executed for
	 * @param abilityName the name of the used ability
	 * @param rep the used representation
	 * @param trial the trial used for the roll
	 * @param spell is the ability a spell
	 * @param taw the TaW of the ability
	 * @param difficulty the difficulty of the trial
	 * @param coverage the coverages used in the trial
	 * @return the string result of the roll
	 */
	private String rollSkillTde4(GuildSetting settings, Map<String, Emoji> emoteMap, RoleplayCharacter chara, String abilityName, Tradition rep, Trial trial, boolean spell, int taw, byte difficulty, String...coverage) {
		
		StringBuilder result = new StringBuilder();
		
		// is there a specialization of the character for that roll, then add 2 to the taw
		if ((spell && chara.specials().stream().anyMatch(sp -> sp.name().equals("Zauberspezialisierung") && sp.attribute1().equals(abilityName) && sp.attribute2().equalsIgnoreCase(rep.name()) && List.of(coverage).contains(sp.attribute3())))
				|| (!spell && chara.specials().stream().anyMatch(sp -> sp.name().equals("Talentspezialisierung") && sp.attribute1().equals(abilityName) && List.of(coverage).contains(sp.attribute2())))) {
			taw+=2;
		}
		
		SkillRollResult4 rollResult = Avesbot.getDiceSimulator().rollSkill4(chara, trial, taw, difficulty, spell);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.rolls().<Pair<Integer, Integer>>toArray(Pair[]::new));
		String tapStr = I18N.getTranslation(settings.locale(), spell ? "spP" : "skP");
		
		if(settings.hideStats())
			result.append(I18N.format(settings.locale(), "rollTde4SkillStatsHidden", chara.name(), abilityName, difficulty));
		else
			result.append(I18N.format(settings.locale(), "rollTde4SkillStatsVisible", chara.name(), abilityName, taw, chara.getTrialValues(trial), difficulty));
		
		result.append("\n");
		result.append(rollResultStr);
		result.append("\n");
		switch (rollResult.outcome()) {
			case SLIP -> result.append("**").append(I18N.getTranslation(settings.locale(), OUTCOME_SLIP)).append("**");
			case SPLENDOR -> {
				result.append(String.format(TAP_FORMAT, taw > 0 ? taw : 1, tapStr));
				result.append("\n");
				result.append("**").append(I18N.getTranslation(settings.locale(), OUTCOME_SPLENDOR)).append("**");
			}
			case FAILURE -> {
				result.append(String.format(TAP_FORMAT, rollResult.tap(), tapStr));
				result.append("\n");
				result.append("**").append(I18N.getTranslation(settings.locale(), OUTCOME_FAILURE)).append("**");
			}
			case SUCCESS -> {
				result.append(String.format(TAP_FORMAT, rollResult.tap(), tapStr));
				result.append("\n");
				result.append("**").append(I18N.getTranslation(settings.locale(), OUTCOME_SUCCESS)).append("**");
			}
		}
		
		return result.toString();
	}
	
	/**
	 * Executes a skill roll for DSA5 ruleset.
	 * @param settings settings of the current guild
	 * @param emoteMap map for the dice emoticons
	 * @param chara the character the roll is executed for
	 * @param abilityName the name of the used ability
	 * @param trial the trial used for the roll
	 * @param spell is the ability a spell
	 * @param taw the TaW of the ability
	 * @param difficulty the difficulty of the trial
	 * @param coverage the coverages used in the trial
	 * @return the string result of the roll
	 */
	private String rollSkillTde5(GuildSetting settings, Map<String, Emoji> emoteMap, RoleplayCharacter chara, String abilityName, Trial trial, boolean spell, int taw, byte difficulty, String...coverage) {
		
		StringBuilder result = new StringBuilder();
		SkillRollResult5 rollResult = Avesbot.getDiceSimulator().rollSkill5(chara, trial, taw, difficulty, spell);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.rolls().<Pair<Integer, Integer>>toArray(Pair[]::new));
		
		if(settings.hideStats())
			result.append(I18N.format(settings.locale(), "rollTde5SkillStatsHidden", chara.name(), abilityName, difficulty));
		else
			result.append(I18N.format(settings.locale(), "rollTde5SkillStatsVisible", chara.name(), abilityName, taw, chara.getTrialValues(trial), difficulty));
		result.append("\n");
		result.append(rollResultStr);
		result.append("\n");
		
		switch(rollResult.outcome()) {
			case SLIP -> result.append("**").append(I18N.getTranslation(settings.locale(), OUTCOME_SLIP)).append("**");
			case SPLENDOR -> {
				result.append(I18N.format(settings.locale(), "rollTde5SkillResult", rollResult.qs()));
				result.append("\n");
				result.append("**").append(I18N.getTranslation(settings.locale(), OUTCOME_SPLENDOR)).append("**");
			}
			case FAILURE ->
					result.append("**").append(I18N.getTranslation(settings.locale(), OUTCOME_FAILURE)).append("**");
			case SUCCESS -> {
				result.append(I18N.format(settings.locale(), "rollTde5SkillResult", rollResult.qs()));
				result.append("\n");
				result.append("**").append(I18N.getTranslation(settings.locale(), OUTCOME_SUCCESS)).append("**");
			}
		}
		
		return result.toString();
	}
	
	/**
	 * Executes an attribute roll.
	 * @param settings settings of the current guild
	 * @param emoteMap map for the dice emoticons
	 * @param chara the character the roll is executed for
	 * @param attr the used attribute for the roll
	 * @param difficulty the difficulty of the trial
	 * @return the string result of the roll
	 */
	default String rollAttribute(GuildSetting settings, Map<String, Emoji> emoteMap, RoleplayCharacter chara, Attribute attr, byte difficulty) {
		
		String result;
		
		result = switch (chara.ruleset()) {
			case TDE4 -> rollAttributeTde4(settings, emoteMap, chara, attr, difficulty);
			case TDE5 -> rollAttributeTde5(settings, emoteMap, chara, attr, difficulty);
			default -> I18N.getTranslation(settings.locale(), "errorUnknownRuleset");
		};
		
		return result;
	}
	
	/**
	 * Executes an attribute roll for DSA4 ruleset.
	 * @param settings settings of the current guild
	 * @param emoteMap map for the dice emoticons
	 * @param chara the character the roll is executed for
	 * @param attr the used attribute for the roll
	 * @param difficulty the difficulty of the trial
	 * @return the string result of the roll
	 */
	private String rollAttributeTde4(GuildSetting settings, Map<String, Emoji> emoteMap, RoleplayCharacter chara, Attribute attr, byte difficulty) {
		
		StringBuilder result = new StringBuilder();
		RollResult rollResult = Avesbot.getDiceSimulator().rollAttribute4(chara, difficulty, attr);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.rolls().<Pair<Integer, Integer>>toArray(Pair[]::new));
		String rollOutcomeStr = rollResult.outcome() == Outcome.SPLENDOR || rollResult.outcome() == Outcome.SUCCESS ?
				"**" + I18N.getTranslation(settings.locale(), OUTCOME_SUCCESS) + "**" :
				"**" + I18N.getTranslation(settings.locale(), OUTCOME_FAILURE) + "**";

		String attrAbbrStr = I18N.getTranslation(settings.locale(), attr.getAbbreviation().toLowerCase());
		
		if(settings.hideStats())
			result.append(I18N.format(settings.locale(), "rollTde4AttributeStatsHidden", chara.name(), attrAbbrStr, rollResultStr, difficulty, rollOutcomeStr));
		else
			result.append(I18N.format(settings.locale(), "rollTde4AttributeStatsVisible", chara.name(), attrAbbrStr, chara.getAttribute(attr), rollResultStr, difficulty, rollOutcomeStr));
		
		return result.toString();
	}
	
	/**
	 * Executes an attribute roll for DSA5 ruleset.
	 * @param settings settings of the current guild
	 * @param emoteMap map for the dice emoticons
	 * @param chara the character the roll is executed for
	 * @param attr the used attribute for the roll
	 * @param difficulty the difficulty of the trial
	 * @return the string result of the roll
	 */
	private String rollAttributeTde5(GuildSetting settings, Map<String, Emoji> emoteMap, RoleplayCharacter chara, Attribute attr, byte difficulty) {
		
		StringBuilder result = new StringBuilder();
		RollResult rollResult = Avesbot.getDiceSimulator().rollAttribute5(chara, difficulty, attr);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.rolls().<Pair<Integer, Integer>>toArray(Pair[]::new));
		String rollOutcomeStr = "";
		switch(rollResult.outcome()) {
			case SLIP -> rollOutcomeStr = "**" + I18N.getTranslation(settings.locale(), OUTCOME_SLIP) + "**";
			case FAILURE -> rollOutcomeStr = "**" + I18N.getTranslation(settings.locale(), OUTCOME_FAILURE) + "**";
			case SUCCESS -> rollOutcomeStr = "**" + I18N.getTranslation(settings.locale(), OUTCOME_SUCCESS) + "**";
			case SPLENDOR -> rollOutcomeStr = "**" + I18N.getTranslation(settings.locale(), OUTCOME_SPLENDOR) + "**";
		}

		String attrAbbrStr = I18N.getTranslation(settings.locale(), attr.getAbbreviation().toLowerCase());
		
		if(settings.hideStats())
			result.append(I18N.format(settings.locale(), "rollTde5AttributeStatsHidden", chara.name(), attrAbbrStr, difficulty, rollResultStr, rollOutcomeStr));
		else
			result.append(I18N.format(settings.locale(), "rollTde5AttributeStatsVisible", chara.name(), attrAbbrStr, difficulty, chara.getAttribute(attr)+difficulty, rollResultStr, rollOutcomeStr));
		
		return result.toString();
	}
}