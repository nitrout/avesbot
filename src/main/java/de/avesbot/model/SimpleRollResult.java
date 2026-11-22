package de.avesbot.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 *
 * @author Nitrout
 */
public record SimpleRollResult(Outcome outcome, List<Pair<Integer, Integer>> rolls) implements RollResult {
	public SimpleRollResult(Outcome outcome, Pair<Integer, Integer> roll) {
		this(outcome, List.of(roll));
	}
}