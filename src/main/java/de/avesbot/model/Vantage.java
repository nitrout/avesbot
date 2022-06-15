package de.avesbot.model;

import java.util.Optional;

/**
 * Represents a (dis)advantage of a character an its optional attributes.
 * @author Nitrout
 */
public record Vantage(String name, Optional<String> attribute1, Optional<String> attribute2) {
	
	/**
	 * Creates a new vantage.
	 * 
	 * @param name the name of the vantage
	 * @param attribute1 the first optional attribute
	 * @param attribute2 the second optional attribute
	 */
	public Vantage(String name, String attribute1, String attribute2) {
		this(name, Optional.ofNullable(attribute1), Optional.ofNullable(attribute2));
	}
}