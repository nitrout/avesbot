package de.avesbot.callable.roll;

import de.avesbot.Avesbot;
import de.avesbot.util.Formatter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A callable to roll and sum up the dices.
 * @author Nitrout
 */
public class RollSumCallable extends RollCallable {

	private static final Pattern SUM_DICE_PATTERN = Pattern.compile("^([1-9]\\d{0,2})?[dw]([1-9]\\d{0,2})?([+-]\\d{1,3})?$", Pattern.CASE_INSENSITIVE);
	public static final SubcommandData SUBCOMMAND;
	
	static {
		SUBCOMMAND = buildTranslatedSubcommand(I18N, "rollSum", "rollSumDescription");
		
		OptionData sumDiceOption = buildTranslatedOption(I18N, OptionType.STRING, "sumDiceOption", "sumDiceOptionDescription", true);
		SUBCOMMAND.addOptions(sumDiceOption);
	}
	
	/**
	 * Creates a new SumCallable.
	 * @param event 
	 */
	public RollSumCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String result;
		Matcher matcher = SUM_DICE_PATTERN.matcher(this.commandPars.get("dice").getAsString());
		
		if(matcher.matches()) {

			int num = matcher.group(1) != null && !matcher.group(1).isEmpty() ? Integer.parseInt(matcher.group(1)) : 1;
			int dice = matcher.group(2) != null && !matcher.group(2).isEmpty() ? Integer.parseInt(matcher.group(2)) : 6;
			int mod = matcher.group(3) != null && !matcher.group(3).isEmpty() ? Integer.parseInt(matcher.group(3)) : 0;
			
			if(num <= Integer.parseInt(Avesbot.getProperties().getProperty("max_dice", "70"))) {
				String rollResult;
				List<Pair<Integer, Integer>> rolls = Avesbot.getDiceSimulator().rollDice(num, dice);
				rollResult = Formatter.formatRollResult(emoteMap, rolls.<Pair<Integer, Integer>>toArray(Pair[]::new));

				result = I18N.format(settings.locale(), "rollSumMessage", member.getEffectiveName(), rollResult, mod, rolls.stream().mapToInt(Pair::getLeft).sum()+mod);
			} else {
				result = I18N.format(settings.locale(), "errorTooManyDice", Integer.valueOf(Avesbot.getProperties().getProperty("max_dice", "70")));
			}
		}
		else {
			result = I18N.getTranslation(settings.locale(), "errorRoll");
		}
		
		return result;
	}
}