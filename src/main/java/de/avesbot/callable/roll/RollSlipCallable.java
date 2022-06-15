package de.avesbot.callable.roll;

import de.avesbot.Avesbot;
import de.avesbot.util.Formatter;
import de.avesbot.util.Pair;
import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * A callable to execute a battle slip roll.
 * @author Nitrout
 */
public class RollSlipCallable extends RollCallable {
	
	/**
	 * Creates a new SlipCallable.
	 * @param event 
	 */
	public RollSlipCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String result = "";
		Pair<Pair<Integer, Integer>[], Integer> sumResult = Avesbot.getDiceSimulator().sumDice(2, 6, 0);
		Pair<Integer, Integer>[] rolls = sumResult.getLeft();
		int sum = sumResult.getRight();
		
		if(sum == 2) {
			result = I18n.getInstance().format(settings.locale(), "slip4_2", Formatter.formatRollResult(emoteMap, rolls));
		}
		else if(sum >= 3 && sum <= 5) {
			result = I18n.getInstance().format(settings.locale(), "slip4_3", Formatter.formatRollResult(emoteMap, rolls));
		}
		else if(sum >= 6 && sum <= 8) {
			result = I18n.getInstance().format(settings.locale(), "slip4_6", Formatter.formatRollResult(emoteMap, rolls));
		}
		else if(sum >= 9 && sum <= 10) {
			result = I18n.getInstance().format(settings.locale(), "slip4_9", Formatter.formatRollResult(emoteMap, rolls));
		}
		else if(sum == 11) {
			result = I18n.getInstance().format(settings.locale(), "slip4_11", Formatter.formatRollResult(emoteMap, rolls));
		}
		else if(sum == 12) {
			result = I18n.getInstance().format(settings.locale(), "slip4_12", Formatter.formatRollResult(emoteMap, rolls));
		}
		
		return result;
	}
}