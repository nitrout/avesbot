package de.avesbot.callable;

import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.entities.Emote;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Ability;
import de.avesbot.model.Attribute;
import de.avesbot.model.GuildSetting;
import de.avesbot.model.Outcome;
import de.avesbot.model.Tradition;
import de.avesbot.model.RolePlayCharacter;
import de.avesbot.model.RollResult;
import de.avesbot.model.SkillRollResult4;
import de.avesbot.model.SkillRollResult5;
import de.avesbot.model.Trial;
import de.avesbot.util.Formatter;

/**
 * Helper class to support the use of rolls for the different rulesets.
 * @author Nitrout
 */
public interface RoleplayCharacterRoll {
	
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
	default String rollSKill(GuildSetting settings, Map<String, Emote> emoteMap, RolePlayCharacter chara, Ability ability, byte difficulty, String...coverage) {
		
		return rollSkill(settings, emoteMap, chara, ability.getName(), ability.getRep(), ability.getTrial(), ability.isSpell(), ability.getTaw(), difficulty, coverage);
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
	default String rollSkill(GuildSetting settings, Map<String, Emote> emoteMap, RolePlayCharacter chara, String abilityName, Tradition rep, Trial trial, boolean spell, int taw, byte difficulty, String...coverage) {
		
		String result;
		
		switch(chara.getRuleset()) {
			case TDE4 : result = rollSkillTde4(settings, emoteMap, chara, abilityName, rep, trial, spell, taw, difficulty, coverage); break;
			case TDE5 : result = rollSkillTde5(settings, emoteMap, chara, abilityName, trial, spell, taw, difficulty, coverage); break;
			default : result = I18n.getInstance().getString(settings.getLocale(), "errorUnknownRuleset");
		}
		
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
	private String rollSkillTde4(GuildSetting settings, Map<String, Emote> emoteMap, RolePlayCharacter chara, String abilityName, Tradition rep, Trial trial, boolean spell, int taw, byte difficulty, String...coverage) {
		
		StringBuilder result = new StringBuilder();
		
		// is there a specialization of the character for that roll, then add 2 to the taw
		if(	(spell && chara.getSpecials().stream().anyMatch(sp -> sp.getName().equals("Zauberspezialisierung") && sp.getAttribute1().orElse("").equals(abilityName) && sp.getAttribute2().orElse("").equalsIgnoreCase(rep.name()) && List.of(coverage).contains(sp.getAttribute3().orElse(""))))
			|| (!spell && chara.getSpecials().stream().anyMatch(sp -> sp.getName().equals("Talentspezialisierung") && sp.getAttribute1().orElse("").equals(abilityName) && List.of(coverage).contains(sp.getAttribute2().orElse(""))))) {
			taw+=2;
		}
		
		SkillRollResult4 rollResult = Avesbot.getDiceSimulator().rollSkill4(chara, trial, taw, difficulty, spell);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.getRolls());
		String tapStr = spell ? I18n.getInstance().getString(settings.getLocale(), "spP") : I18n.getInstance().getString(settings.getLocale(), "skP");
		
		if(settings.isHideStats())
			result.append(I18n.getInstance().format(settings.getLocale(), "rollTde4SkillStatsHidden", chara.getName(), abilityName, difficulty));
		else
			result.append(I18n.getInstance().format(settings.getLocale(), "rollTde4SkillStatsVisible", chara.getName(), abilityName, taw, chara.getTrialValues(trial), difficulty));
		
		result.append("\n");
		result.append(rollResultStr);
		result.append("\n");
		switch (rollResult.getOutcome()) {
			case SLIP:
				result.append("**").append(I18n.getInstance().getString(settings.getLocale(), "outcomeSlip")).append("**");
				break;
			case SPLENDOR:
				result.append(String.format("%d %s*", taw > 0 ? taw : 1, tapStr));
				result.append("\n");
				result.append("**").append(I18n.getInstance().getString(settings.getLocale(), "outcomeSplendor")).append("**");
				break;
			case FAILURE:
				result.append(String.format("%d %s*", rollResult.getTap(), tapStr));
				result.append("\n");
				result.append("**").append(I18n.getInstance().getString(settings.getLocale(), "outcomeFailure")).append("**");
				break;
			case SUCCESS:
				result.append(String.format("%d %s*", rollResult.getTap(), tapStr));
				result.append("\n");
				result.append("**").append(I18n.getInstance().getString(settings.getLocale(), "outcomeSuccess")).append("**");
				break;
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
	private String rollSkillTde5(GuildSetting settings, Map<String, Emote> emoteMap, RolePlayCharacter chara, String abilityName, Trial trial, boolean spell, int taw, byte difficulty, String...coverage) {
		
		StringBuilder result = new StringBuilder();
		SkillRollResult5 rollResult = Avesbot.getDiceSimulator().rollSkill5(chara, trial, taw, difficulty, spell);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.getRolls());
		
		if(settings.isHideStats())
			result.append(I18n.getInstance().format(settings.getLocale(), "rollTde5SkillStatsHidden", chara.getName(), abilityName, difficulty));
		else
			result.append(I18n.getInstance().format(settings.getLocale(), "rollTde5SkillStatsVisible", chara.getName(), abilityName, taw, chara.getTrialValues(trial), difficulty));
		result.append("\n");
		result.append(rollResultStr);
		result.append("\n");
		
		switch(rollResult.getOutcome()) {
			case SLIP:
				result.append("**").append(I18n.getInstance().getString(settings.getLocale(), "outcomeSlip")).append("**");
				break;
			case SPLENDOR:
				result.append(I18n.getInstance().format(settings.getLocale(), "rollTde5SkillResult", rollResult.getQS()));
				result.append("\n");
				result.append("**").append(I18n.getInstance().getString(settings.getLocale(), "outcomeSplendor")).append("**");
				break;
			case FAILURE:
				result.append("**").append(I18n.getInstance().getString(settings.getLocale(), "outcomeFailure")).append("**");
				break;
			case SUCCESS:
				result.append(I18n.getInstance().format(settings.getLocale(), "rollTde5SkillResult", rollResult.getQS()));
				result.append("\n");
				result.append("**").append(I18n.getInstance().getString(settings.getLocale(), "outcomeSuccess")).append("**");
				break;
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
	default String rollAttribute(GuildSetting settings, Map<String, Emote> emoteMap, RolePlayCharacter chara, Attribute attr, byte difficulty) {
		
		String result;
		
		switch (chara.getRuleset()) {
			case TDE4 :	result = rollAttributeTde4(settings, emoteMap, chara, attr, difficulty); break;
			case TDE5 :	result = rollAttributeTde5(settings, emoteMap, chara, attr, difficulty); break;
			default :	result = I18n.getInstance().getString(settings.getLocale(), "errorUnknownRuleset");
		}
		
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
	private String rollAttributeTde4(GuildSetting settings, Map<String, Emote> emoteMap, RolePlayCharacter chara, Attribute attr, byte difficulty) {
		
		StringBuilder result = new StringBuilder();
		RollResult rollResult = Avesbot.getDiceSimulator().rollAttribute4(chara, difficulty, attr);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.getRolls());
		String rollOutcomeStr = rollResult.getOutcome() == Outcome.SPLENDOR || rollResult.getOutcome() == Outcome.SUCCESS ? 
				"**"+I18n.getInstance().getString(settings.getLocale(), "outcomeSuccess")+"**" : 
				"**"+I18n.getInstance().getString(settings.getLocale(), "outcomeFailure")+"**";
		
		String attrAbbrStr = I18n.getInstance().getString(settings.getLocale(), attr.getAbbrevation().toLowerCase());
		
		if(settings.isHideStats())
			result.append(I18n.getInstance().format(settings.getLocale(), "rollTde4AttributeStatsHidden", chara.getName(), attrAbbrStr, rollResultStr, difficulty, rollOutcomeStr));
		else
			result.append(I18n.getInstance().format(settings.getLocale(), "rollTde4AttributeStatsVisible", chara.getName(), attrAbbrStr, chara.getAttribute(attr), rollResultStr, difficulty, rollOutcomeStr));
		
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
	private String rollAttributeTde5(GuildSetting settings, Map<String, Emote> emoteMap, RolePlayCharacter chara, Attribute attr, byte difficulty) {
		
		StringBuilder result = new StringBuilder();
		RollResult rollResult = Avesbot.getDiceSimulator().rollAttribute5(chara, difficulty, attr);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.getRolls());
		String rollOutcomeStr = "";
		switch(rollResult.getOutcome()) {
			case SLIP :		rollOutcomeStr = "**"+I18n.getInstance().getString(settings.getLocale(), "outcomeSlip")+"**"; break;
			case FAILURE :	rollOutcomeStr = "**"+I18n.getInstance().getString(settings.getLocale(), "outcomeFailure")+"**"; break;
			case SUCCESS :	rollOutcomeStr = "**"+I18n.getInstance().getString(settings.getLocale(), "outcomeSuccess")+"**"; break;
			case SPLENDOR :	rollOutcomeStr = "**"+I18n.getInstance().getString(settings.getLocale(), "outcomeSplendor")+"**"; break;
		}
		
		String attrAbbrStr = I18n.getInstance().getString(settings.getLocale(), attr.getAbbrevation().toLowerCase());
		
		if(settings.isHideStats())
			result.append(I18n.getInstance().format(settings.getLocale(), "rollTde5AttributeStatsHidden", chara.getName(), attrAbbrStr, difficulty, rollResultStr, rollOutcomeStr));
		else
			result.append(I18n.getInstance().format(settings.getLocale(), "rollTde5AttributeStatsVisible", chara.getName(), attrAbbrStr, difficulty, chara.getAttribute(attr)+difficulty, rollResultStr, rollOutcomeStr));
		
		return result.toString();
	}
}