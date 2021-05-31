package de.avesbot.util;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.Emote;

/**
 *
 * @author Nitrout
 */
public class Formatter {
	
	private static String formatEmotelessRollResult(Pair<Integer, Integer>...rolls) {
		
		return Stream.of(rolls).map(roll -> Integer.toString(roll.getLeft())).collect(Collectors.joining(", ", "[", "]"));
	}
	
	private static String formatEmoteRollResult(Map<String, Emote> emoteMap, Pair<Integer, Integer>...rolls) {
		
		return Stream.of(rolls).map(roll -> emoteMap.get(roll.getLeft()+"d"+roll.getRight()).getAsMention()).collect(Collectors.joining(" "));
	}
	
	public static String formatRollResult(Map<String, Emote> emoteMap, Pair<Integer, Integer>...rolls) {
		if(Stream.of(rolls).allMatch(roll -> emoteMap.containsKey(roll.getLeft()+"d"+roll.getRight()))) {
			return formatEmoteRollResult(emoteMap, rolls);
		} else {
			return formatEmotelessRollResult(rolls);
		}
	}
}