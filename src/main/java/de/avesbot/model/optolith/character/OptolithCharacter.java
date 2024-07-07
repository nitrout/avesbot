package de.avesbot.model.optolith.character;

import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Nitrout
 */
public record OptolithCharacter(Locale locale, String name, OptolithAttributeData attr, Map<String, Object> activatable,
		Map<String, Integer> talents, Map<String, Integer> ct, Map<String, Integer> spells, Map<String, Integer> liturgies) {

}
