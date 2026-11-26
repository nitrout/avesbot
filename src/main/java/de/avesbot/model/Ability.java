package de.avesbot.model;

import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

/**
 * The ability of a character
 * @author nitrout
 */
public record Ability(String id, String name, Tradition rep, Trial trial, byte taw, Type type) {
	
	private static final I18n I18N = new I18n("de.avesbot.i18n.general");
	public static final String NAME_FIELD = "ability_name";
	
	public enum Type {
		TALENT, SPELL, LITURGY;

		private static final List<Choice> OPTION_CHOICES = Stream.of(values()).map(Type::toOptionChoice).toList();

		public static List<Choice> optionChoices() {
			return OPTION_CHOICES;
		}

		private static Choice toOptionChoice(Type type) {
			return new Choice(I18N.getTranslation(type.name().toLowerCase()), type.name())
					.setNameLocalizations(I18N.getLocalizations(type.name().toLowerCase()));
		}
	}
	
	/**
	 * Creates a new ability.
	 * 
	 * @param name name of the ability
	 * @param rep the representation of the ability
	 * @param trial the trial of the ability
	 * @param taw the abilities taw
	 * @param type type of the ability
	 */
	public Ability(String name, Tradition rep, Trial trial, byte taw, Type type) {
		this("0", name, rep, trial, taw, type);
	}

	/**
	 * 
	 * @return the string representation
	 */
	@NotNull
	@Override
	public String toString() {
		return String.format("%s %s%s%s\t%d", type, name, rep == Tradition.NONE ? "" : "[" + rep.name() + "]", trial, taw);
	}
}
