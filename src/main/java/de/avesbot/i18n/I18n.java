package de.avesbot.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import net.dv8tion.jda.api.interactions.DiscordLocale;

/**
 *
 * @author Nitrout
 */
public record I18n(String ressource) {
	
	private static final Locale[] AVAILABLE_LOCALES = new Locale[] {Locale.US, Locale.GERMAN};
	
	public String getTranslation(String key) {
		return this.getTranslation(AVAILABLE_LOCALES[0], key);
	}
	
	public String getTranslation(Locale l, String key) {
		return ResourceBundle.getBundle(ressource, l).getString(key);
	}
	
	public String format(Locale l, String key, Object...vals) {
		return String.format(l, this.getTranslation(l, key), vals);
	}
	
	public Map<DiscordLocale, String> getLocalizations(String key) {
		HashMap<DiscordLocale, String> map = new HashMap<>();
		for(Locale l : AVAILABLE_LOCALES)
			map.put(DiscordLocale.from(l), getTranslation(l, key));
		
		return map;
	}
}