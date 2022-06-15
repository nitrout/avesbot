package de.avesbot.callable.settings;

import net.dv8tion.jda.api.Permission;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.GuildSetting;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 *
 * @author Nitrout
 */
public class SettingsHideStatsCallable extends SettingsCallable {
	
	public SettingsHideStatsCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		boolean hideStats = Boolean.parseBoolean(this.commandPars.get("hide").getAsString());
		
		if(member.hasPermission(Permission.ADMINISTRATOR)) {
			
			GuildSetting newSetting = new GuildSetting(settings.id(), settings.name(), settings.description(), settings.iconUrl(), settings.promote(), settings.locale().toString(), hideStats);
			Avesbot.getStatementManager().updateGuildSetting(newSetting);
			return I18n.getInstance().format(settings.locale(), "settingsHiddenStatsChanged", newSetting.hideStats());
		} else {
			return I18n.getInstance().getString(settings.locale(), "errorInsufficientPermissions");
		}
	}
}