package de.avesbot.model;

import de.avesbot.util.Pair;

/**
 * Representation of a roll result of dices.
 * 
 * @author nitrout
 */
public class RollResult {
	
	private final Outcome outcome;
	private final Pair<Integer, Integer>[] rolls;
	
	/**
	 * Creates a new RollResult
	 * @param outcome the outcome
	 * @param rolls the results of the dices
	 */
	public RollResult(Outcome outcome, Pair<Integer, Integer>...rolls) {
		this.outcome = outcome;
		this.rolls = rolls;
	}
	
	/**
	 * @return the outcome
	 */
	public Outcome getOutcome() {
		return outcome;
	}
	
	/**
	 * @return the rolls
	 */
	public Pair<Integer, Integer>[] getRolls() {
		return rolls;
	}
}