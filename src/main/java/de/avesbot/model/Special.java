package de.avesbot.model;

import java.util.Optional;

/**
 * Represents a special ability of a character.
 * @author Nitrout
 */
public record Special(String name, Optional<String> attribute1, Optional<String> attribute2, Optional<String> attribute3) {
	
	/**
	 * Creates a new special ability.
	 * 
	 * @param name the name of the special ability
	 * @param attribute1 the first optional attribute
	 * @param attribute2 the second optional attribute
	 * @param attribute3 the third optional attribute
	 */
	public Special(String name, String attribute1, String attribute2, String attribute3) {
		this(name, Optional.ofNullable(attribute1), Optional.ofNullable(attribute2), Optional.ofNullable(attribute3));
	}
}