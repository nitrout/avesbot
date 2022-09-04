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
public class I18n {
	
	private static final String FILE = "de.avesbot.i18n.messages";
	private static final Locale[] AVAILABLE_LOCALES = new Locale[] {Locale.ENGLISH, Locale.GERMAN};
	
	private final HashMap<Locale, ResourceBundle> bundles;
	
	public I18n(String ressource) {
		
		this.bundles = new HashMap<>();
		for(Locale l : AVAILABLE_LOCALES)
			this.bundles.put(l, ResourceBundle.getBundle(ressource, l));
	}
	
	public String getTranslation(String key) {
		return this.getTranslation(AVAILABLE_LOCALES[0], key);
	}
	
	public String getTranslation(Locale l, String key) {
		
		if(this.bundles.containsKey(l))
			return this.bundles.get(l).getString(key);
		else
			return this.bundles.get(AVAILABLE_LOCALES[0]).getString(key);
	}
	
	public String format(Locale l, String key, Object...vals) {
		return String.format(l, this.getTranslation(l, key), vals);
	}
	
	public Map<DiscordLocale, String> getLocalizations(String key) {
		HashMap<DiscordLocale, String> map = new HashMap<>();
		for(Locale l : AVAILABLE_LOCALES)
			map.put(DiscordLocale.from(l), key);
		
		return map;
	}
}