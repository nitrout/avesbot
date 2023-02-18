package de.avesbot.callable;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.SymbolDice;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Creates a new symbol dice for a guild.
 * @author Nitrout
 */
public class DiceCallable extends CommandCallable {
	
	protected static final I18n I18N = new I18n("de.avesbot.i18n.messages");
	public static final SlashCommandData COMMAND = buildTranslatedSlashCommand(I18N, "dice", "diceDescription");
	
	static {
		
		OptionData nameOption = new OptionData(OptionType.STRING, "nameOption", "nameOptionDescription", true);
		OptionData areaOption = new OptionData(OptionType.STRING, "areasOption", "areasOptionDescription", true);
		
		COMMAND.addOptions(nameOption, areaOption);
	}
	
	/**
	 * Creates a new DiceCallable.
	 * @param event 
	 */
	public DiceCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String name = this.commandPars.get("name").getAsString();
		String[] values = this.commandPars.get("areas").getAsString().split(",");
		
		SymbolDice dice = new SymbolDice(name, values);
		
		Avesbot.getStatementManager().insertSymbolDice(guild, dice);
		
		return I18N.format(settings.locale(), "diceCreated", name);
	}
}