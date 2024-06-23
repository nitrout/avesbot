package de.avesbot.model;

import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Nitrout
 */
public record SimpleRollResult(Outcome outcome, Pair<Integer, Integer>... rolls) implements RollResult {

}