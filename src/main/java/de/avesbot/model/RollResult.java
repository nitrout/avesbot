package de.avesbot.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

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
	List<Pair<Integer, Integer>> rolls();
}