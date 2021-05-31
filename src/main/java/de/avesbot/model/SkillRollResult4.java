package de.avesbot.model;

import de.avesbot.util.Pair;

/**
 * Representation of a dice roll result for a skill in DSA4 ruleset.
 * @author Nitrout
 */
public class SkillRollResult4 extends RollResult {
	
	private final int tap;
	
	/**
	 * Creates a new SkillRollResult for DSA4 ruleset.
	 * @param outcome the outcome
	 * @param tap the TaP* of the result
	 * @param rolls the results of the rolls
	 */
	public SkillRollResult4(Outcome outcome, int tap, Pair<Integer, Integer>... rolls) {
		super(outcome, rolls);
		this.tap = tap;
	}
	
	/**
	 * @return the tap
	 */
	public int getTap() {
		return tap;
	}
}