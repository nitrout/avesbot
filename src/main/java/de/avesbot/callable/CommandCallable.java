package de.avesbot.callable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent.OptionData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import de.avesbot.Avesbot;
import de.avesbot.model.GuildSetting;

/**
 *
 * @author Nitrout
 */
public abstract class CommandCallable implements Callable<String> {
	
	public static final CommandData COMMAND = null;
	
	protected final Guild guild;
	protected final Member member;
	protected final String[] pars;
	protected final HashMap<String, OptionData> commandPars;
	protected final List<Message.Attachment> attachments;
	protected final GuildSetting settings;
	
	public CommandCallable(MessageReceivedEvent event) {
		this.guild = event.getGuild();
		this.member = event.getMember();
		
		String[] messageParts = event.getMessage().getContentRaw().split(" ");
		this.pars = Arrays.copyOfRange(messageParts, 1, messageParts.length);
		this.commandPars = new HashMap<>();
		
		this.attachments = event.getMessage().getAttachments();
		this.settings = Avesbot.getStatementManager().getGuildSetting(guild).orElse(GuildSetting.DEFAULT);
	}
	
	public CommandCallable(SlashCommandEvent event) {
		this.guild = event.getGuild();
		this.member = event.getMember();
		this.pars = new String[] {};
		this.commandPars = new HashMap<>();
		event.getOptions().forEach(e -> this.commandPars.put(e.getName(), e));
		
		this.attachments = List.of();
		this.settings = Avesbot.getStatementManager().getGuildSetting(guild).orElse(GuildSetting.DEFAULT);
	}
}