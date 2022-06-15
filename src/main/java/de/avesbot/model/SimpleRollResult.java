package de.avesbot.model;

import de.avesbot.util.Pair;

/**
 *
 * @author Nitrout
 */
public record SimpleRollResult(Outcome outcome, Pair<Integer, Integer>... rolls) implements RollResult {

}