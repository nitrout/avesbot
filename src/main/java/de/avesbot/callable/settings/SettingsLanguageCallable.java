package de.avesbot.callable.settings;

import net.dv8tion.jda.api.Permission;
import de.avesbot.Avesbot;
import de.avesbot.i18n.I18n;
import de.avesbot.model.GuildSetting;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Callable to set a new localisation for the guild.
 * @author Nitrout
 */
public class SettingsLanguageCallable extends SettingsCallable {
	
	public SettingsLanguageCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String localeStr = this.commandPars.get("locale").getAsString();
		
		if(member.hasPermission(Permission.ADMINISTRATOR)) {
			
			GuildSetting newSetting = new GuildSetting(settings.id(), settings.name(), settings.description(), settings.iconUrl(), settings.promote(), localeStr, settings.hideStats());
			Avesbot.getStatementManager().updateGuildSetting(newSetting);
			return I18n.getInstance().format(newSetting.locale(), "settingsLocaleChanged", newSetting.locale().toString());
		} else {
			return I18n.getInstance().getString(settings.locale(), "errorInsufficientPermissions");
		}
	}
}