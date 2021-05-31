package de.avesbot.model;

import java.util.Optional;

/**
 * Represents a (dis)advantage of a character an its optional attributes.
 * @author Nitrout
 */
public class Vantage {
	
	private String name;
	private Optional<String> attribute1;
	private Optional<String> attribute2;
	
	/**
	 * Creates a new vantage.
	 * 
	 * @param name the name of the vantage
	 * @param attribute1 the first optional attribute
	 * @param attribute2 the second optional attribute
	 */
	public Vantage(String name, String attribute1, String attribute2) {
		this.name = name;
		this.attribute1 = Optional.ofNullable(attribute1);
		this.attribute2 = Optional.ofNullable(attribute2);
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
}