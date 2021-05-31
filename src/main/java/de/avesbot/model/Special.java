package de.avesbot.model;

import java.util.Optional;

/**
 * Represents a special ability of a character.
 * @author Nitrout
 */
public class Special {
	
	private String name;
	private Optional<String> attribute1;
	private Optional<String> attribute2;
	private Optional<String> attribute3;
	
	/**
	 * Creates a new special ability.
	 * 
	 * @param name the name of the special ability
	 * @param attribute1 the first optional attribute
	 * @param attribute2 the second optional attribute
	 * @param attribute3 the third optional attribute
	 */
	public Special(String name, String attribute1, String attribute2, String attribute3) {
		this.name = name;
		this.attribute1 = Optional.ofNullable(attribute1);
		this.attribute2 = Optional.ofNullable(attribute2);
		this.attribute3 = Optional.ofNullable(attribute3);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the attribute1
	 */
	public Optional<String> getAttribute1() {
		return attribute1;
	}
	
	/**
	 * @return the attribute2
	 */
	public Optional<String> getAttribute2() {
		return attribute2;
	}
	
	/**
	 * @return the attribute3
	 */
	public Optional<String> getAttribute3() {
		return attribute3;
	}
}