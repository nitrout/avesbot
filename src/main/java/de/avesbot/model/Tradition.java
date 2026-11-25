package de.avesbot.model;

import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * The dsa magic representations.
 * @author Nitrout
 */
public enum Tradition {
	NONE,
	BORBARAD("Borbarad", "Borbaradianer", "BOR"),
	DRUID("Druide", "DRU"),
	ELF("Elf", "ELF"),
	GEODE("Geode", "GEO"),
	WITCH("Hexe", "HEX"),
	ACHAZ("Achaz", "ACH"),
	GUILD_MAGE("Magier", "MAG"),
	ILLUSIONIST("Scharlatan", "SRL"),
	TRICKSTER("Schelm", "SCH"),
	OTHER;
	
	private static final I18n I18N = new I18n("de.avesbot.i18n.general");
	private static final List<Choice> OPTION_CHOICES = Stream.of(values())
																.map(Tradition::getTranslatedChoice)
			.toList();

	public static List<Choice> optionChoices() {
		return OPTION_CHOICES;
	}

	final HashSet<String> mappings;
	
	Tradition(String...mappings) {
		this.mappings = new HashSet<>();
		this.mappings.addAll(Arrays.asList(mappings));
	}
	
	/**
	 * Returns one of the enums values based on its name or one of its mapping names.
	 * @param str
	 * @return the enum matching the string or enum OTH if none is matching
	 */
	public static Tradition mappedValueOf(String str) {
		
		Tradition result;
		
		try {
			result = valueOf(str);
		}
		catch(IllegalArgumentException ex) {
			result = Stream.of(values()).filter(e -> e.mappings.contains(str)).findAny().orElse(OTHER);
		}
		
		return result;
	}
	
	private static Choice getTranslatedChoice(Tradition t) {
		Choice choice = new Choice(I18N.getTranslation(t.name().toLowerCase()), t.name().toLowerCase());
		choice.setNameLocalizations(I18N.getLocalizations(t.name().toLowerCase()));
		
		return choice;
	}
}