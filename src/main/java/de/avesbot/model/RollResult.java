package de.avesbot.model;

import de.avesbot.util.Pair;

/**
 * Representation of a roll result of dices.
 * 
 * @author nitrout
 */
public interface RollResult {
	
	/**
	 * @return the outcome
	 */
	Outcome outcome();
	
	/**
	 * @return the rolls
	 */
	Pair<Integer, Integer>[] rolls();
}