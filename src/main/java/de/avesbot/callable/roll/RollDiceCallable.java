package de.avesbot.callable.roll;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.avesbot.Avesbot;
import de.avesbot.model.SymbolDice;
import de.avesbot.util.Formatter;
import de.avesbot.util.Pair;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * A callable to execute a dice roll.
 * @author Nitrout
 */
public class RollDiceCallable extends RollCallable {

	private static final Pattern DICE_PATTERN = Pattern.compile("^([1-9]\\d{0,2})?[dw]([1-9]\\d{0,2})?$", Pattern.CASE_INSENSITIVE);
	public static final int MAX_DICE = Integer.parseInt(Avesbot.getProperties().getProperty("max_dice", "70"));
	public static final SubcommandData SUBCOMMAND;
	
	static {
		SUBCOMMAND = buildTranslatedSubcommand(I18N, "rollDice", "rollDiceDescription");
		
		OptionData diceOption = buildTranslatedOption(I18N, OptionType.STRING, "diceOption", "diceOptionDescription", true);
		SUBCOMMAND.addOptions(diceOption);
	}
	
	/**
	 * Creates a new RollDiceCallable.
	 * @param event the triggering event
	 */
	public RollDiceCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		Optional<SymbolDice> symbolDice = Optional.empty();
		Matcher matcher;
		
		
		matcher = DICE_PATTERN.matcher(this.commandPars.get("dice").getAsString());
		
		if(matcher.matches()) {
			Optional<Integer> num = Optional.ofNullable(Integer.valueOf(matcher.group(1)));
			Optional<Integer> dice = Optional.ofNullable(Integer.valueOf(matcher.group(2)));
			return rollDiceExpression(num.orElse(1), dice.orElse(6));
		}
		
		symbolDice = Avesbot.getStatementManager().getSymbolDice(guild, this.commandPars.get("dice").getAsString());
		if(symbolDice.isPresent()) {
			return symbolDice.map(dice -> rollSymbolDice(dice)).get();
		}
		
		return I18N.getTranslation(settings.locale(), "errorRoll");
	}
	
	private String rollSymbolDice(SymbolDice dice) {
		
		String rollResultStr = Avesbot.getDiceSimulator().rollSymbolDice(dice);
		return I18N.format(settings.locale(), "rollSymbol", member.getEffectiveName(), dice.name(), rollResultStr);
	}
	
	private String rollDiceExpression(int num, int dice) {
		
		if(num > MAX_DICE)
			return I18N.format(settings.locale(), "errorTooManyDice", Integer.valueOf(Avesbot.getProperties().getProperty("max_dice", "70")));
		
		
		Pair<Integer, Integer>[] rolls = Avesbot.getDiceSimulator().rollDice(num, dice);
		String rollResultStr = Formatter.formatRollResult(emoteMap, rolls);
		return I18N.format(settings.locale(), "rollExpression", member.getEffectiveName(), rollResultStr);
	}
}