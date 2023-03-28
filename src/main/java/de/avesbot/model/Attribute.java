package de.avesbot.model;

import de.avesbot.i18n.I18n;
import java.util.stream.Stream;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;

/**
 * The base attributes.
 * @author nitrout
 */
public enum Attribute {
	
	COURAGE("COU"), SAGACITY("SGC"), INTUITION("INT"), CHARISMA("CHA"), DEXTERITY("DEX"), AGILITY("AGI"), CONSTITUTION("CON"), STRENGTH("STR");
	
	private static final I18n I18N = new I18n("de.avesbot.i18n.general");
	public static final Choice[] OPTION_CHOICES = Stream.of(values())
																.map(Attribute::getTranslatedChoice)
																.toArray(Choice[]::new);
	
	private final String abbrevation;
	
	private Attribute(String abbrevation) {
		this.abbrevation = abbrevation;
		
	}
	
	public static Attribute abbrevationValueOf(String abbrevation) {
		
		for(Attribute a : Attribute.values()) {
			if(a.getAbbrevation().equals(abbrevation))
				return a;
		}
		
		throw new IllegalArgumentException(String.format("\"%s\" is not a valid abbrevation.", abbrevation));
	}

	/**
	 * @return the abbrevation
	 */
	public String getAbbrevation() {
		return abbrevation;
	}
	
	private static Choice getTranslatedChoice(Attribute a) {
		Choice choice = new Choice(I18N.getTranslation(a.getAbbrevation().toLowerCase()), a.name());
		choice.setNameLocalizations(I18N.getLocalizations(a.getAbbrevation().toLowerCase()));
		
		return choice;
	}
}