package de.avesbot.callable.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.GuildSetting;

/**
 *
 * @author Nitrout
 */
public class SettingsHideStatsCallable extends SettingsCallable {
	
	public SettingsHideStatsCallable(SlashCommandEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		boolean hideStats = Boolean.parseBoolean(this.commandPars.get("hide").getAsString());
		
		if(member.hasPermission(Permission.ADMINISTRATOR)) {
			
			GuildSetting newSetting = new GuildSetting(settings.getId(), settings.getName(), settings.getDescription(), settings.getIconUrl(), settings.isPromote(), settings.getLocale().toString(), hideStats);
			Avesbot.getStatementManager().updateGuildSetting(newSetting);
			return I18n.getInstance().format(settings.getLocale(), "settingsHiddenStatsChanged", newSetting.isHideStats());
		} else {
			return I18n.getInstance().getString(settings.getLocale(), "errorInsufficientPermissions");
		}
	}
}