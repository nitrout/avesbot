package de.avesbot.util;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Nitrout
 */
public class Formatter {

	private Formatter() {
		throw new UnsupportedOperationException("Instantiation not allowed");
	}

	private static String formatEmojilessRollResult(List<Pair<Integer, Integer>> rolls) {

		return rolls.stream().map(roll -> Integer.toString(roll.getLeft())).collect(Collectors.joining(", ", "[", "]"));
	}

	private static String formatEmojiRollResult(Map<String, Emoji> emoteMap, List<Pair<Integer, Integer>> rolls) {

		return rolls.stream().map(roll -> emoteMap.get(roll.getLeft() + "d" + roll.getRight()).getFormatted()).collect(Collectors.joining(" "));
	}

	public static String formatRollResult(Map<String, Emoji> emoteMap, List<Pair<Integer, Integer>> rolls) {
		if (rolls.stream().anyMatch(roll -> !emoteMap.containsKey(roll.getLeft() + "d" + roll.getRight()))) {
			return formatEmojilessRollResult(rolls);
		}
		return formatEmojiRollResult(emoteMap, rolls);
	}
}