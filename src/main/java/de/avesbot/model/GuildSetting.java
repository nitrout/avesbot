package de.avesbot.model;

import java.util.Locale;

/**
 * Represantation of aves settings of a guild.
 * 
 * @author Nitrout
 */
public record GuildSetting(String id, String name, String description, String iconUrl, boolean promote, Locale locale, boolean hideStats) {
	
	public static final GuildSetting DEFAULT = new GuildSetting("", "", "", "", false, "en_US", false);
	
	/**
	 * Creates a new representation of guild settings
	 * 
	 * @param id discord id of the guild
	 * @param name name of the guild
	 * @param description description of the guild
	 * @param icon icon url of the guild
	 * @param promote indicates if the guild promoted on aves website
	 * @param localeStr locale settings as string like xx_XX
	 * @param hideStats are the character's stats hidden in the roll commands
	 */
	public GuildSetting(String id, String name, String description, String iconUrl, boolean promote, String localeStr, boolean hideStats) {
		
		this(id, name, description, iconUrl, promote, new Locale(localeStr.substring(0, localeStr.indexOf("_")), localeStr.substring(localeStr.indexOf("_")+1)), hideStats);
	}
}