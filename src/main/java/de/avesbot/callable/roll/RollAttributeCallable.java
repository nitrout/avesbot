package de.avesbot.callable.roll;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Attribute;
import de.avesbot.model.Outcome;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.RollResult;
import de.avesbot.util.Formatter;

/**
 * Executes an attribute trial for the character.
 * @author Nitrout
 */
public class RollAttributeCallable extends RollCallable {
	
	/**
	 * Creates a new attribute trial callable.
	 * @param event
	 */
	public RollAttributeCallable(SlashCommandEvent event) {
		super(event);
	}
	
	/**
	 * Executes the callable.
	 * @return the result
	 * @throws Exception 
	 */
	@Override
	public String call() throws Exception {
		
		String result;
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		Optional<Attribute> attribute = Optional.empty();
		byte difficulty = 0;
		
		attribute = Optional.of(Attribute.valueOf(this.commandPars.get("attribute").getAsString().toUpperCase()));
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		
		if(chara.isEmpty()) {
			result = I18n.getInstance().getString(settings.getLocale(), "errorNoActiveCharacter");
		} else if(attribute.isEmpty()) {
			result = I18n.getInstance().getString(settings.getLocale(), "errorNoAttribute");
		} else {
			result = rollAttribute(chara.get(), attribute.get(), difficulty);
		}
		
		return result;
	}
	
	
	/**
	 * Executes an attribute roll.
	 * @param chara the character the roll is executed for
	 * @param attr the used attribute for the roll
	 * @param difficulty the difficulty of the trial
	 * @return the string result of the roll
	 */
	protected String rollAttribute(RoleplayCharacter chara, Attribute attr, byte difficulty) {
		
		String result;
		
		switch (chara.getRuleset()) {
			case TDE4 :	result = rollAttributeTde4(chara, attr, difficulty); break;
			case TDE5 :	result = rollAttributeTde5(chara, attr, difficulty); break;
			default :	result = I18n.getInstance().getString(settings.getLocale(), "errorUnknownRuleset");
		}
		
		return result;
	}
	
	/**
	 * Executes an attribute roll for DSA4 ruleset.
	 * @param chara the character the roll is executed for
	 * @param attr the used attribute for the roll
	 * @param difficulty the difficulty of the trial
	 * @return the string result of the roll
	 */
	protected String rollAttributeTde4(RoleplayCharacter chara, Attribute attr, byte difficulty) {
		
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
	 * @param chara the character the roll is executed for
	 * @param attr the used attribute for the roll
	 * @param difficulty the difficulty of the trial
	 * @return the string result of the roll
	 */
	protected String rollAttributeTde5(RoleplayCharacter chara, Attribute attr, byte difficulty) {
		
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