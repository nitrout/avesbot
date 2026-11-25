package de.avesbot.model;

import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;

import java.util.List;
import java.util.stream.Stream;

/**
 * The base attributes.
 * @author nitrout
 */
public enum Attribute {
	
	COURAGE("COU"), SAGACITY("SGC"), INTUITION("INT"), CHARISMA("CHA"), DEXTERITY("DEX"), AGILITY("AGI"), CONSTITUTION("CON"), STRENGTH("STR");
	
	private static final I18n I18N = new I18n("de.avesbot.i18n.general");
	private static final List<Choice> OPTION_CHOICES = Stream.of(values()).map(Attribute::getTranslatedChoice).toList();

	private final String abbreviation;

	Attribute(String abbreviation) {
		this.abbreviation = abbreviation;
		
	}

	public static List<Choice> optionChoices() {
		return OPTION_CHOICES;
	}

	public static Attribute abbreviationValueOf(String abbreviation) {
		
		for(Attribute a : Attribute.values()) {
			if (a.getAbbreviation().equals(abbreviation))
				return a;
		}

		throw new IllegalArgumentException(String.format("\"%s\" is not a valid abbreviation.", abbreviation));
	}

	/**
	 * @return the abbreviation
	 */
	public String getAbbreviation() {
		return abbreviation;
	}
	
	private static Choice getTranslatedChoice(Attribute a) {
		Choice choice = new Choice(I18N.getTranslation(a.getAbbreviation().toLowerCase()), a.name());
		choice.setNameLocalizations(I18N.getLocalizations(a.getAbbreviation().toLowerCase()));
		
		return choice;
	}
}