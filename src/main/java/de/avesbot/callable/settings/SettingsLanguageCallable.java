package de.avesbot.callable.settings;

import net.dv8tion.jda.api.Permission;
import de.avesbot.Avesbot;
import static de.avesbot.callable.settings.SettingsCallable.COMMAND;
import de.avesbot.i18n.I18n;
import de.avesbot.model.GuildSetting;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Callable to set a new localisation for the guild.
 * @author Nitrout
 */
public class SettingsLanguageCallable extends SettingsCallable {
	
	private final static Pattern LOCALE_PATTERN = Pattern.compile("^[a-z]{2}(_[A-Z]{2})?$");
	
	static {
		SubcommandData subcommand = buildTranslatedSubcommand(I18N, "settingsLanguage", "settingsLanguageDescription");
		
		OptionData localeOption = buildTranslatedOption(I18N, OptionType.STRING, "localeOption", "localeOptionDescription", true);
		subcommand.addOptions(localeOption);
		
		COMMAND.addSubcommands(subcommand);
	}
	
	public SettingsLanguageCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {
		
		String localeStr = this.commandPars.get("locale").getAsString();
		
		if(!member.hasPermission(Permission.ADMINISTRATOR))
			return I18N.getTranslation(settings.locale(), "errorInsufficientPermissions");
		if(!LOCALE_PATTERN.asPredicate().test(localeStr))
			return I18N.getTranslation(settings.locale(), "errorMalformedLocale");
			
		GuildSetting newSetting = new GuildSetting(settings.id(), settings.name(), settings.description(), settings.iconUrl(), settings.promote(), localeStr, settings.hideStats());
		Avesbot.getStatementManager().updateGuildSetting(newSetting);
		return I18N.format(newSetting.locale(), "settingsLocaleChanged", newSetting.locale().toString());
	}
}