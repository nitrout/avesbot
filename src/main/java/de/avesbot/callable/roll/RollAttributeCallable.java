package de.avesbot.callable.roll;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.Attribute;
import de.avesbot.model.Outcome;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.RollResult;
import de.avesbot.util.Formatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Executes an attribute trial for the character.
 * @author Nitrout
 */
public class RollAttributeCallable extends RollCallable {
	
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
		
		String result;
		Optional<RoleplayCharacter> chara = Avesbot.getStatementManager().getUsersActiveRolePlayCharacter(member);
		Optional<Attribute> attribute = Optional.empty();
		byte difficulty = 0;
		
		attribute = Optional.of(Attribute.valueOf(this.commandPars.get("attribute").getAsString().toUpperCase()));
		if(this.commandPars.containsKey("difficulty"))
			difficulty = (byte)this.commandPars.get("difficulty").getAsLong();
		
		if(chara.isEmpty()) {
			result = I18n.getInstance().getString(settings.locale(), "errorNoActiveCharacter");
		} else if(attribute.isEmpty()) {
			result = I18n.getInstance().getString(settings.locale(), "errorNoAttribute");
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
		
		result = switch (chara.ruleset()) {
			case TDE4 -> rollAttributeTde4(chara, attr, difficulty);
			case TDE5 -> rollAttributeTde5(chara, attr, difficulty);
			default -> I18n.getInstance().getString(settings.locale(), "errorUnknownRuleset");
		};
		
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
	 * @param chara the character the roll is executed for
	 * @param attr the used attribute for the roll
	 * @param difficulty the difficulty of the trial
	 * @return the string result of the roll
	 */
	protected String rollAttributeTde5(RoleplayCharacter chara, Attribute attr, byte difficulty) {
		
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