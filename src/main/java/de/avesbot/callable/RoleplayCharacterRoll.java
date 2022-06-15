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
import de.avesbot.model.RoleplayCharacter;
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
	default String rollSKill(GuildSetting settings, Map<String, Emote> emoteMap, RoleplayCharacter chara, Ability ability, byte difficulty, String...coverage) {
		
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
	default String rollSkill(GuildSetting settings, Map<String, Emote> emoteMap, RoleplayCharacter chara, String abilityName, Tradition rep, Trial trial, boolean spell, int taw, byte difficulty, String...coverage) {
		
		String result;
		
		result = switch (chara.ruleset()) {
			case TDE4 -> rollSkillTde4(settings, emoteMap, chara, abilityName, rep, trial, spell, taw, difficulty, coverage);
			case TDE5 -> rollSkillTde5(settings, emoteMap, chara, abilityName, trial, spell, taw, difficulty, coverage);
			default -> I18n.getInstance().getString(settings.locale(), "errorUnknownRuleset");
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
	private String rollSkillTde4(GuildSetting settings, Map<String, Emote> emoteMap, RoleplayCharacter chara, String abilityName, Tradition rep, Trial trial, boolean spell, int taw, byte difficulty, String...coverage) {
		
		StringBuilder result = new StringBuilder();
		
		// is there a specialization of the character for that roll, then add 2 to the taw
		if(	(spell && chara.getSpecials().stream().anyMatch(sp -> sp.name().equals("Zauberspezialisierung") && sp.attribute1().orElse("").equals(abilityName) && sp.attribute2().orElse("").equalsIgnoreCase(rep.name()) && List.of(coverage).contains(sp.attribute3().orElse(""))))
			|| (!spell && chara.getSpecials().stream().anyMatch(sp -> sp.name().equals("Talentspezialisierung") && sp.attribute1().orElse("").equals(abilityName) && List.of(coverage).contains(sp.attribute2().orElse(""))))) {
			taw+=2;
		}
		
		SkillRollResult4 rollResult = Avesbot.getDiceSimulator().rollSkill4(chara, trial, taw, difficulty, spell);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.rolls());
		String tapStr = spell ? I18n.getInstance().getString(settings.locale(), "spP") : I18n.getInstance().getString(settings.locale(), "skP");
		
		if(settings.hideStats())
			result.append(I18n.getInstance().format(settings.locale(), "rollTde4SkillStatsHidden", chara.name(), abilityName, difficulty));
		else
			result.append(I18n.getInstance().format(settings.locale(), "rollTde4SkillStatsVisible", chara.name(), abilityName, taw, chara.getTrialValues(trial), difficulty));
		
		result.append("\n");
		result.append(rollResultStr);
		result.append("\n");
		switch (rollResult.outcome()) {
			case SLIP -> result.append("**").append(I18n.getInstance().getString(settings.locale(), "outcomeSlip")).append("**");
			case SPLENDOR -> {
				result.append(String.format("%d %s*", taw > 0 ? taw : 1, tapStr));
				result.append("\n");
				result.append("**").append(I18n.getInstance().getString(settings.locale(), "outcomeSplendor")).append("**");
			}
			case FAILURE -> {
				result.append(String.format("%d %s*", rollResult.tap(), tapStr));
				result.append("\n");
				result.append("**").append(I18n.getInstance().getString(settings.locale(), "outcomeFailure")).append("**");
			}
			case SUCCESS -> {
				result.append(String.format("%d %s*", rollResult.tap(), tapStr));
				result.append("\n");
				result.append("**").append(I18n.getInstance().getString(settings.locale(), "outcomeSuccess")).append("**");
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
	private String rollSkillTde5(GuildSetting settings, Map<String, Emote> emoteMap, RoleplayCharacter chara, String abilityName, Trial trial, boolean spell, int taw, byte difficulty, String...coverage) {
		
		StringBuilder result = new StringBuilder();
		SkillRollResult5 rollResult = Avesbot.getDiceSimulator().rollSkill5(chara, trial, taw, difficulty, spell);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.rolls());
		
		if(settings.hideStats())
			result.append(I18n.getInstance().format(settings.locale(), "rollTde5SkillStatsHidden", chara.name(), abilityName, difficulty));
		else
			result.append(I18n.getInstance().format(settings.locale(), "rollTde5SkillStatsVisible", chara.name(), abilityName, taw, chara.getTrialValues(trial), difficulty));
		result.append("\n");
		result.append(rollResultStr);
		result.append("\n");
		
		switch(rollResult.outcome()) {
			case SLIP -> result.append("**").append(I18n.getInstance().getString(settings.locale(), "outcomeSlip")).append("**");
			case SPLENDOR -> {
				result.append(I18n.getInstance().format(settings.locale(), "rollTde5SkillResult", rollResult.qs()));
				result.append("\n");
				result.append("**").append(I18n.getInstance().getString(settings.locale(), "outcomeSplendor")).append("**");
			}
			case FAILURE -> result.append("**").append(I18n.getInstance().getString(settings.locale(), "outcomeFailure")).append("**");
			case SUCCESS -> {
				result.append(I18n.getInstance().format(settings.locale(), "rollTde5SkillResult", rollResult.qs()));
				result.append("\n");
				result.append("**").append(I18n.getInstance().getString(settings.locale(), "outcomeSuccess")).append("**");
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
	default String rollAttribute(GuildSetting settings, Map<String, Emote> emoteMap, RoleplayCharacter chara, Attribute attr, byte difficulty) {
		
		String result;
		
		result = switch (chara.ruleset()) {
			case TDE4 -> rollAttributeTde4(settings, emoteMap, chara, attr, difficulty);
			case TDE5 -> rollAttributeTde5(settings, emoteMap, chara, attr, difficulty);
			default -> I18n.getInstance().getString(settings.locale(), "errorUnknownRuleset");
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
	private String rollAttributeTde4(GuildSetting settings, Map<String, Emote> emoteMap, RoleplayCharacter chara, Attribute attr, byte difficulty) {
		
		StringBuilder result = new StringBuilder();
		RollResult rollResult = Avesbot.getDiceSimulator().rollAttribute4(chara, difficulty, attr);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.rolls());
		String rollOutcomeStr = rollResult.outcome()== Outcome.SPLENDOR || rollResult.outcome()== Outcome.SUCCESS ? 
				"**"+I18n.getInstance().getString(settings.locale(), "outcomeSuccess")+"**" : 
				"**"+I18n.getInstance().getString(settings.locale(), "outcomeFailure")+"**";
		
		String attrAbbrStr = I18n.getInstance().getString(settings.locale(), attr.getAbbrevation().toLowerCase());
		
		if(settings.hideStats())
			result.append(I18n.getInstance().format(settings.locale(), "rollTde4AttributeStatsHidden", chara.name(), attrAbbrStr, rollResultStr, difficulty, rollOutcomeStr));
		else
			result.append(I18n.getInstance().format(settings.locale(), "rollTde4AttributeStatsVisible", chara.name(), attrAbbrStr, chara.getAttribute(attr), rollResultStr, difficulty, rollOutcomeStr));
		
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
	private String rollAttributeTde5(GuildSetting settings, Map<String, Emote> emoteMap, RoleplayCharacter chara, Attribute attr, byte difficulty) {
		
		StringBuilder result = new StringBuilder();
		RollResult rollResult = Avesbot.getDiceSimulator().rollAttribute5(chara, difficulty, attr);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rollResult.rolls());
		String rollOutcomeStr = "";
		switch(rollResult.outcome()) {
			case SLIP -> rollOutcomeStr = "**"+I18n.getInstance().getString(settings.locale(), "outcomeSlip")+"**";
			case FAILURE -> rollOutcomeStr = "**"+I18n.getInstance().getString(settings.locale(), "outcomeFailure")+"**";
			case SUCCESS -> rollOutcomeStr = "**"+I18n.getInstance().getString(settings.locale(), "outcomeSuccess")+"**";
			case SPLENDOR -> rollOutcomeStr = "**"+I18n.getInstance().getString(settings.locale(), "outcomeSplendor")+"**";
		}
		
		String attrAbbrStr = I18n.getInstance().getString(settings.locale(), attr.getAbbrevation().toLowerCase());
		
		if(settings.hideStats())
			result.append(I18n.getInstance().format(settings.locale(), "rollTde5AttributeStatsHidden", chara.name(), attrAbbrStr, difficulty, rollResultStr, rollOutcomeStr));
		else
			result.append(I18n.getInstance().format(settings.locale(), "rollTde5AttributeStatsVisible", chara.name(), attrAbbrStr, difficulty, chara.getAttribute(attr)+difficulty, rollResultStr, rollOutcomeStr));
		
		return result.toString();
	}
}