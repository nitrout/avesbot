package de.avesbot.model;

/**
 * Represents a dice of symbols.
 * @author Nitrout
 */
public record SymbolDice(String name, String...values) {
	
	/**
	 * Get the side of the dice at the given position.
	 * @param side the position of the side
	 * @return the string representation of the side
	 */
	public String side(int side) {
		return this.values[side];
	}
}