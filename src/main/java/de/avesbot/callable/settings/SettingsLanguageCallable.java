package de.avesbot.callable.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.GuildSetting;

/**
 * Callable to set a new localisation for the guild.
 * @author Nitrout
 */
public class SettingsLanguageCallable extends SettingsCallable {
	
	public SettingsLanguageCallable(SlashCommandEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String localeStr = this.commandPars.get("locale").getAsString();
		
		if(member.hasPermission(Permission.ADMINISTRATOR)) {
			
			GuildSetting newSetting = new GuildSetting(settings.getId(), settings.getName(), settings.getDescription(), settings.getIconUrl(), settings.isPromote(), localeStr, settings.isHideStats());
			Avesbot.getStatementManager().updateGuildSetting(newSetting);
			return I18n.getInstance().format(newSetting.getLocale(), "settingsLocaleChanged", newSetting.getLocale().toString());
		} else {
			return I18n.getInstance().getString(settings.getLocale(), "errorInsufficientPermissions");
		}
	}
}