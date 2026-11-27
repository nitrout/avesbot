package de.avesbot.i18n;

import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 *
 * @author Nitrout
 */
public record I18n(String resource) {

	private static final Locale[] AVAILABLE_LOCALES = {Locale.US, Locale.GERMAN};
	
	public String getTranslation(String key) {
		return this.getTranslation(AVAILABLE_LOCALES[0], key);
	}
	
	public String getTranslation(Locale l, String key) {
		return ResourceBundle.getBundle(resource, l).getString(key);
	}
	
	public String format(Locale l, String key, Object...vals) {
		return String.format(l, this.getTranslation(l, key), vals);
	}
	
	public Map<DiscordLocale, String> getLocalizations(String key) {
		EnumMap<DiscordLocale, String> map = new EnumMap<>(DiscordLocale.class);
		for(Locale l : AVAILABLE_LOCALES)
			map.put(DiscordLocale.from(l), getTranslation(l, key));
		
		return map;
	}
}