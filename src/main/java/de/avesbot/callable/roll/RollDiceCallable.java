package de.avesbot.callable.roll;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.SymbolDice;
import de.avesbot.util.Formatter;
import de.avesbot.util.Pair;

/**
 * A callable to execute a dice roll.
 * @author Nitrout
 */
public class RollDiceCallable extends RollCallable {

	private static final Pattern DICE_PATTERN = Pattern.compile("^([1-9]\\d{0,2})?[dw]([1-9]\\d{0,2})?$", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Creates a new RollDiceCallable.
	 * @param event the triggering event
	 */
	public RollDiceCallable(SlashCommandEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		StringBuilder result = new StringBuilder();
		Optional<SymbolDice> symbolDice = Optional.empty();
		Matcher matcher;
		
		symbolDice = Avesbot.getStatementManager().getSymbolDice(guild, this.commandPars.get("dice").getAsString());
		matcher = DICE_PATTERN.matcher(this.commandPars.get("dice").getAsString());
		
		String rollResultStr;
		
		if(matcher.matches()) {
			int num = matcher.group(1) != null && matcher.group(1).length() > 0 ? Integer.parseInt(matcher.group(1))  : 1;
			int dice = matcher.group(2) != null && matcher.group(2).length() > 0 ? Integer.parseInt(matcher.group(2)) : 6;
			
			if(num <= Integer.parseInt(Avesbot.getProperties().getProperty("max_dice", "70"))) {
				Pair<Integer, Integer>[] rolls = Avesbot.getDiceSimulator().rollDice(num, dice);
				rollResultStr = Formatter.formatRollResult(emoteMap, rolls);
				result.append(I18n.getInstance().format(settings.getLocale(), "roll", member.getEffectiveName(), rollResultStr));
			} else {
				result.append(I18n.getInstance().format(settings.getLocale(), "errorTooManyDice", Integer.parseInt(Avesbot.getProperties().getProperty("max_dice", "70"))));
			}
		}
		else if(symbolDice.isPresent()) {
			SymbolDice dice = symbolDice.get();
			rollResultStr = Avesbot.getDiceSimulator().rollSymbolDice(symbolDice.get());
			
			result.append(I18n.getInstance().format(settings.getLocale(), "rollSymbol", member.getEffectiveName(), dice.getName(), rollResultStr));
		}
		else {
			result.append(I18n.getInstance().getString(settings.getLocale(), "errorRoll"));
		}
		
		return result.toString();
	}
	
}