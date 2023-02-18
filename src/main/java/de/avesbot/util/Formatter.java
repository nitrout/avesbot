package de.avesbot.util;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.emoji.Emoji;

/**
 *
 * @author Nitrout
 */
public class Formatter {
	
	private static String formatEmojilessRollResult(Pair<Integer, Integer>...rolls) {
		
		return Stream.of(rolls).map(roll -> Integer.toString(roll.getLeft())).collect(Collectors.joining(", ", "[", "]"));
	}
	
	private static String formatEmojiRollResult(Map<String, Emoji> emoteMap, Pair<Integer, Integer>...rolls) {
		
		return Stream.of(rolls).map(roll -> emoteMap.get(roll.getLeft()+"d"+roll.getRight()).getFormatted()).collect(Collectors.joining(" "));
	}
	
	public static String formatRollResult(Map<String, Emoji> emoteMap, Pair<Integer, Integer>...rolls) {
		if(Stream.of(rolls).allMatch(roll -> emoteMap.containsKey(roll.getLeft()+"d"+roll.getRight()))) {
			return formatEmojiRollResult(emoteMap, rolls);
		} else {
			return formatEmojilessRollResult(rolls);
		}
	}
}