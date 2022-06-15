package de.avesbot.model;

/**
 * The ability of a character
 * @author nitrout
 */
public record Ability(String id, String name, Tradition rep, Trial trial, byte taw, Type type) {
	
	public enum Type {
		TALENT, SPELL, LITURGY;
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
