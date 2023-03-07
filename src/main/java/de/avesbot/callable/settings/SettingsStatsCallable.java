package de.avesbot.callable.settings;

import net.dv8tion.jda.api.Permission;
import de.avesbot.Avesbot;
import de.avesbot.model.GuildSetting;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 *
 * @author Nitrout
 */
public class SettingsStatsCallable extends SettingsCallable {
	
	static {
		SubcommandData subcommand = buildTranslatedSubcommand(I18N, "stats", "settingsStatsDescription");
		
		OptionData valueOption =buildTranslatedOption(I18N, OptionType.STRING, "valueOption", "valueOptionDescription", true);
		valueOption.addChoices(buildTranslatedChoice(I18N, "hideChoice", "true"), buildTranslatedChoice(I18N, "showChoice", "false"));
		subcommand.addOptions(valueOption);
		
		COMMAND.addSubcommands(subcommand);
	}
	
	public SettingsStatsCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		boolean stats = this.commandPars.get("value").getAsBoolean();
		
		if(member.hasPermission(Permission.ADMINISTRATOR)) {
			
			GuildSetting newSetting = new GuildSetting(settings.id(), settings.name(), settings.description(), settings.iconUrl(), settings.promote(), settings.locale().toString(), stats);
			Avesbot.getStatementManager().updateGuildSetting(newSetting);
			return I18N.format(settings.locale(), "settingsStatsChanged", newSetting.hideStats());
		} else {
			return I18N.getTranslation(settings.locale(), "errorInsufficientPermissions");
		}
	}
}