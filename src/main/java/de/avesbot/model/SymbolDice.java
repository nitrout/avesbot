package de.avesbot.model;

/**
 * Represents a dice of symbols.
 * @author Nitrout
 */
public class SymbolDice {
	
	private final String name;
	private final String[] values;
	
	/**
	 * Creates a new symbol dice.
	 * @param name the name of the dice
	 * @param values the areas of the dice
	 */
	public SymbolDice(String name, String...values) {
		this.name = name;
		this.values = new String[values.length];
		for(int i = 0; i < values.length; i++)
			this.values[i] = values[i].trim();
	}
	
	/**
	 * Get the side of the dice at the given position.
	 * @param side the position of the side
	 * @return the string representation of the side
	 */
	public String getSide(int side) {
		return this.values[side];
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the values
	 */
	public String[] getValues() {
		return values;
	}
}