package de.avesbot.model;

import de.avesbot.util.Pair;

/**
 * Representation of a dice roll result for a skill in DSA4 ruleset.
 * @author Nitrout
 */
public record SkillRollResult4(Outcome outcome, int tap, Pair<Integer, Integer>... rolls) implements RollResult {
	
}