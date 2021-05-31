package de.avesbot.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Nitrout
 */
public class I18n {
	
	private static final String FILE = "de.avesbot.i18n.messages";
	private static final I18n INSTANCE = new I18n();
	
	private final HashMap<Locale, ResourceBundle> bundles;
	
	public static I18n getInstance() {
		return INSTANCE;
	}
	
	public I18n() {
		
		this.bundles = new HashMap<>();
//		this.bundles.put(Locale.GERMANY, loadBundle(Locale.GERMANY));
//		this.bundles.put(Locale.FRANCE, loadBundle(Locale.FRANCE));
//		this.bundles.put(Locale.UK, loadBundle(Locale.UK));
//		this.bundles.put(Locale.US, loadBundle(Locale.US));
	}
	
	public String getString(Locale locale, String str) {
		
		if(!this.bundles.containsKey(locale))
			this.loadLocalization(locale);
		return this.bundles.get(locale).getString(str);
	}
	
	public String format(Locale l, String key, Object...vals) {
		return String.format(l, this.getString(l, key), vals);
	}
	
	private void loadLocalization(Locale l) {
		this.bundles.put(l, ResourceBundle.getBundle(FILE, l));
	}
}