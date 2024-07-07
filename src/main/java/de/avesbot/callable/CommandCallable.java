package de.avesbot.callable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.GuildSetting;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 *
 * @author Nitrout
 */
public abstract class CommandCallable implements Callable<String> {
	
	//public static final SlashCommandData COMMAND = null;
	public static final int TIMEOUT = 2;
	
	protected final Guild guild;
	protected final Member member;
	protected final String[] pars;
	protected final HashMap<String, OptionMapping> commandPars;
	protected final List<Message.Attachment> attachments;
	protected final GuildSetting settings;
	
	protected CommandCallable(MessageReceivedEvent event) {
		this.guild = event.getGuild();
		this.member = event.getMember();
		
		String[] messageParts = event.getMessage().getContentRaw().split(" ");
		this.pars = Arrays.copyOfRange(messageParts, 1, messageParts.length);
		this.commandPars = new HashMap<>();
		
		this.attachments = event.getMessage().getAttachments();
		this.settings = Avesbot.getStatementManager().getGuildSetting(guild).orElse(GuildSetting.DEFAULT);
	}
	
	protected CommandCallable(SlashCommandInteractionEvent event) {
		this.guild = event.getGuild();
		this.member = event.getMember();
		this.pars = new String[] {};
		this.commandPars = new HashMap<>();
		event.getOptions().forEach(e -> this.commandPars.put(e.getName(), e));
		
		this.attachments = List.of();
		this.settings = Avesbot.getStatementManager().getGuildSetting(guild).orElse(GuildSetting.DEFAULT);
	}
	
	public int getTimeout() {
		
		return TIMEOUT;
	}
	
	protected static SlashCommandData buildTranslatedSlashCommand(I18n i18n, String name, String description) {
		
		SlashCommandData slashCommand =  Commands.slash(i18n.getTranslation(name), i18n.getTranslation(description));
		slashCommand.setNameLocalizations(i18n.getLocalizations(name));
		slashCommand.setDescriptionLocalizations(i18n.getLocalizations(description));
		
		return slashCommand;
	}
	
	protected static SubcommandData buildTranslatedSubcommand(I18n i18n, String name, String description) {
		
		SubcommandData subcommand = new SubcommandData(i18n.getTranslation(name), i18n.getTranslation(description));
		subcommand.setNameLocalizations(i18n.getLocalizations(name));
		subcommand.setDescriptionLocalizations(i18n.getLocalizations(description));
		
		return subcommand;
	}
	
	protected static OptionData buildTranslatedOption(I18n i18n, OptionType type, String name, String description, boolean required) {
		
		OptionData option = new OptionData(type, i18n.getTranslation(name), i18n.getTranslation(description), required);
		option.setNameLocalizations(i18n.getLocalizations(name));
		option.setDescriptionLocalizations(i18n.getLocalizations(description));
		
		return option;
	}
	
	protected static Choice buildTranslatedChoice(I18n i18n, String name, String value) {
		
		Choice choice = new Choice(i18n.getTranslation(name), value);
		choice.setNameLocalizations(i18n.getLocalizations(name));
		
		return choice;
	}
	
	public static CommandData toCommandData(Class<? extends CommandCallable> callableClass) {
		try {
			return (CommandData)FieldUtils.readStaticField(callableClass, "COMMAND");
		} catch(IllegalAccessException ex) {
			Logger.getLogger(CommandCallable.class.getName()).log(Level.SEVERE, callableClass.getName()+" has no accesible static field COMMAND", ex);
			return null;
		}
	}
}