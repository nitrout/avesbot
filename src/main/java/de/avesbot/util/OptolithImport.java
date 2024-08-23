package de.avesbot.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.avesbot.Avesbot;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Ruleset;
import de.avesbot.model.Special;
import de.avesbot.model.Vantage;
import de.avesbot.model.optolith.character.OptolithCharacter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.Message.Attachment;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Nitrout
 */
public class OptolithImport implements BiFunction<Attachment, String, Boolean> {

	private static final ObjectMapper JSON_MAPPER = getObjectMapper();

	private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
	private static final HashMap<Pair<Locale, String>, Map<String, Object>> YAML_FILES = new HashMap<>();

	static {
		YAML_MAPPER.findAndRegisterModules();
	}

	private static Map<String, Object> getYaml(Locale locale, String file) {

		var key = Pair.of(locale, file);
		if (!YAML_FILES.containsKey(key)) {
			try {
				var yaml = YAML_MAPPER.readValue(Avesbot.class.getResourceAsStream("de/avesbot/optolith/" + locale.toLanguageTag() + "/" + file), Map.class);
				YAML_FILES.put(key, yaml);
			} catch (IOException ex) {
				Logger.getLogger(OptolithImport.class.getName()).log(Level.SEVERE, null, ex);
				return Map.of();
			}
		}

		return YAML_FILES.get(key);

	}

	private static ObjectMapper getObjectMapper() {
		var om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return om;
	}

	@Override
	public Boolean apply(Attachment attachment, String memberId) {
		return getOptolithCharacter(attachment).map(character -> importCharacter(character, memberId)).orElse(Boolean.FALSE);
	}

	private static Optional<OptolithCharacter> getOptolithCharacter(Attachment attachment) {
		try {
			return Optional.of(JSON_MAPPER.readValue(attachment.getProxy().download().get(), OptolithCharacter.class));
		} catch (ExecutionException | IOException ex) {
			Logger.getLogger(OptolithImport.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InterruptedException ex) {
			Logger.getLogger(OptolithImport.class.getName()).log(Level.SEVERE, null, ex);
			Thread.currentThread().interrupt();
		}
		return Optional.empty();
	}

	private static Boolean importCharacter(OptolithCharacter character, String memberId) {
		var chara = createRoleplayCharacter(character);
		Optional<String> insertedId = Avesbot.getStatementManager().insertRoleplayCharacter(memberId, chara);
		if (insertedId.isEmpty()) {
			return false;
		}
		chara = new RoleplayCharacter(insertedId.get(), chara);

		return true;
	}

	private static RoleplayCharacter createRoleplayCharacter(OptolithCharacter character) {

		var attributes = new byte[8];

		var optoAttributes = character.attr().values();
		for (var attr : optoAttributes) {
			var pos = Integer.parseInt(attr.id().substring(attr.id().length() - 1)) - 1; // attributes start with 1, so -1 to get correct array position
			attributes[pos] = (byte) attr.value();
		}

		return new RoleplayCharacter(character.name(), Ruleset.TDE5, attributes, extractVantages(character), extractSpecials(character));
	}

	private static List<Vantage> extractVantages(OptolithCharacter character) {
		return character.activatable().entrySet().stream()
				.filter(e -> e.getKey().startsWith("ADV_") || e.getKey().startsWith("DISADV_"))
				.map(e -> toVantage(character, e))
				.toList();
	}

	private static Vantage toVantage(OptolithCharacter character, Entry<String, Object> vantageEntry) {
		if (vantageEntry.getKey().startsWith("ADV_")) {
			return toAdvantage(character, vantageEntry);
		} else if (vantageEntry.getKey().startsWith("DISADV_")) {
			return toDisadvantage(character, vantageEntry);
		}
		return null;
	}

	private static Vantage toAdvantage(OptolithCharacter character, Entry<String, Object> vantageEntry) {
		var advantage = (Map<String, Object>) YAML_FILES.get(Pair.of(character.locale(), "Advantages.yaml")).get(vantageEntry.getKey());
		return new Vantage((String) advantage.get("name"), "", "");
	}

	private static Vantage toDisadvantage(OptolithCharacter character, Entry<String, Object> vantageEntry) {
		var disadvantage = (Map<String, Object>) getYaml(character.locale(), "Disadvantages.yaml").get(vantageEntry.getKey());
		return new Vantage((String) disadvantage.get("name"), "", "");
	}

	private static List<Special> extractSpecials(OptolithCharacter character) {
		return character.activatable().entrySet().stream()
				.filter(e -> e.getKey().startsWith("SA_"))
				.map(e -> toSpecial(character, e))
				.toList();
	}

	private static Special toSpecial(OptolithCharacter character, Entry<String, Object> specialEntry) {
		var special = (Map<String, Object>) getYaml(character.locale(), "SpecialAbilities.yaml").get(specialEntry.getKey());
		return new Special((String) special.get("name"), "", "", "");
	}
}
