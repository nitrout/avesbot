package de.avesbot.model;

import java.util.Locale;

/**
 * Represantation of aves settings of a guild.
 * 
 * @author Nitrout
 */
public class GuildSetting {
	
	public static final GuildSetting DEFAULT = new GuildSetting("", "", "", "", false, "en_US", false);
	
	private final String id;
	private final String name;
	private final String description;
	private final String iconUrl;
	private final boolean promote;
	private final Locale locale;
	private final boolean hideStats;
	
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
	public GuildSetting(String id, String name, String description, String icon, boolean promote, String localeStr, boolean hideStats) {
		
		this.id = id;
		this.name = name;
		this.description = description;
		this.iconUrl = icon;
		this.promote = promote;
		String[] localeParts = localeStr.split("_");
		this.locale = new Locale(localeParts[0], localeParts[1]);
		this.hideStats = hideStats;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the iconUrl
	 */
	public String getIconUrl() {
		return iconUrl;
	}

	/**
	 * @return the promote
	 */
	public boolean isPromote() {
		return promote;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @return the hideStats
	 */
	public boolean isHideStats() {
		return hideStats;
	}
}