package de.avesbot.model;

import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.interactions.commands.Command;

/**
 * The base attributes.
 * @author nitrout
 */
public enum Attribute {
	
	COURAGE("COU"), SAGACITY("SGC"), INTUITION("INT"), CHARISMA("CHA"), DEXTERITY("DEX"), AGILITY("AGI"), CONSTITUTION("CON"), STRENGTH("STR");
	
	private static final I18n I18N = new I18n("de.avesbot.i18n.general");
	public static final Command.Choice[] OPTION_CHOICES;
	
	static {
		OPTION_CHOICES = new Command.Choice[values().length];
		for(Attribute t : values()) {
			OPTION_CHOICES[t.ordinal()] = new Command.Choice(I18N.getTranslation(t.getAbbrevation().toLowerCase()), t.name());
			OPTION_CHOICES[t.ordinal()].setNameLocalizations(I18N.getLocalizations(t.getAbbrevation().toLowerCase()));
		}
	}
	
	private final String abbrevation;
	
	private Attribute(String abbrevation) {
		this.abbrevation = abbrevation;
		
	}
	
	public static Attribute abbrevationValueOf(String abbrevation) {
		
		for(Attribute a : Attribute.values()) {
			if(a.getAbbrevation().equals(abbrevation))
				return a;
		}
		
		throw new IllegalArgumentException(abbrevation+" is not a valid abbreavation.");
	}

	/**
	 * @return the abbrevation
	 */
	public String getAbbrevation() {
		return abbrevation;
	}
}