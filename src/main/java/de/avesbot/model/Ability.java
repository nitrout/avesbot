package de.avesbot.model;

import de.avesbot.i18n.I18n;
import net.dv8tion.jda.api.interactions.commands.Command;

/**
 * The ability of a character
 * @author nitrout
 */
public record Ability(String id, String name, Tradition rep, Trial trial, byte taw, Type type) {
	
	private static final I18n I18N = new I18n("de.avesbot.i18n.general");
	
	public enum Type {
		TALENT, SPELL, LITURGY;
		
		public static final Command.Choice[] OPTION_CHOICES;
	
		static {
			OPTION_CHOICES = new Command.Choice[values().length];
			for(Type t : values()) {
				OPTION_CHOICES[t.ordinal()] = new Command.Choice(I18N.getTranslation(t.name().toLowerCase()), t.name());
				OPTION_CHOICES[t.ordinal()].setNameLocalizations(I18N.getLocalizations(t.name().toLowerCase()));
			}
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
	@Override
	public String toString() {
		
		return String.format("%s %s%s%s\t%d", type, name, rep != Tradition.NONE ? "["+rep.name()+"]" : "", trial, taw);
	}
}
