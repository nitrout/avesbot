package de.avesbot.model;

import org.apache.commons.lang3.tuple.Pair;

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