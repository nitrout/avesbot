package de.avesbot.model;

import de.avesbot.util.Pair;

/**
 * Representation of a dice roll result for a skill in DSA5 ruleset.
 * @author Nitrout
 */
public class SkillRollResult5 extends RollResult {
	
	private final int qs;
	
	/**
	 * Creates a new SkillRollResult fpr DSA5 ruleset.
	 * @param outcome
	 * @param qs
	 * @param rolls 
	 */
	public SkillRollResult5(Outcome outcome, int qs, Pair<Integer, Integer>... rolls) {
		super(outcome, rolls);
		this.qs = qs;
	}
	
	/**
	 * @return the tap
	 */
	public int getQS() {
		return qs;
	}
}