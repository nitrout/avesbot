package de.avesbot.callable;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.SymbolDice;

/**
 * Creates a new symbol dice for a guild.
 * @author Nitrout
 */
public class DiceCallable extends CommandCallable {
	
	public static final CommandData COMMAND = new CommandData("dice", "Creates a new symbol dice");
	
	static {
		COMMAND
				.addOptions(new OptionData(OptionType.STRING, "name", "The name of the dice.", true),
							new OptionData(OptionType.STRING, "areas", "The comma-separated list of the dice areas", true)
				);
	}
	
	/**
	 * Creates a new DiceCallable.
	 * @param event 
	 */
	public DiceCallable(SlashCommandEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String name = this.commandPars.get("name").getAsString();
		String[] values = this.commandPars.get("areas").getAsString().split(",");
		
		SymbolDice dice = new SymbolDice(name, values);
		
		Avesbot.getStatementManager().insertSymbolDice(guild, dice);
		
		return I18n.getInstance().format(settings.getLocale(), "diceCreated", name);
	}
}