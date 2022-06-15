package de.avesbot.model;

import de.avesbot.util.Pair;

/**
 * Representation of a dice roll result for a skill in DSA5 ruleset.
 * @author Nitrout
 */
public record SkillRollResult5(Outcome outcome, int qs, Pair<Integer, Integer>... rolls) implements RollResult {
	
}