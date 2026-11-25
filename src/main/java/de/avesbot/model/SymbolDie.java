package de.avesbot.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents a die of symbols.
 * @author Nitrout
 */
public record SymbolDie(String name, List<String> sides) {

	public SymbolDie(String name, String... sides) {
		this(name, List.of(sides));
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		SymbolDie symbolDie = (SymbolDie) o;
		return Objects.equals(name, symbolDie.name) && Objects.equals(sides, symbolDie.sides);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, sides);
	}

	@NotNull
	@Override
	public String toString() {
		return "SymbolDie{" +
				"name='" + name + '\'' +
				", sides=" + String.join(", ", sides) +
				'}';
	}

	/**
	 * Get the side of the dice at the given position.
	 * @param side the position of the side
	 * @return the string representation of the side
	 */
	public String side(int side) {
		return this.sides.get(side);
	}
}