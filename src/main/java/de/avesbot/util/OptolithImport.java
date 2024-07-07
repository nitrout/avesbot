package de.avesbot.util;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Nitrout
 */
public class OptolithImport implements Runnable {

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

	private final OptolithCharacter character;
	private final String memberId;

	public OptolithImport(OptolithCharacter character, String memberId) {
		this.character = character;
		this.memberId = memberId;
	}

	@Override
	public void run() {

		var chara = toRoleplayCharacter();
		Optional<String> insertedId = Avesbot.getStatementManager().insertRoleplayCharacter(memberId, chara);
		if (insertedId.isEmpty()) {
			return;
		}
		chara = new RoleplayCharacter(insertedId.get(), chara);
	}

	private RoleplayCharacter toRoleplayCharacter() {

		var attributes = new byte[8];

		var optoAttributes = character.attr().values();
		for (var attr : optoAttributes) {
			var pos = Integer.parseInt(attr.id().substring(attr.id().length() - 1)) - 1; // attributes start with 1, so -1 to get correct array position
			attributes[pos] = (byte) attr.value();
		}

		return new RoleplayCharacter(character.name(), Ruleset.TDE5, attributes, toVantages(), toSpecials());
	}

	private List<Vantage> toVantages() {
		return character.activatable().entrySet().stream()
				.filter(e -> e.getKey().startsWith("ADV_") || e.getKey().startsWith("DISADV_"))
				.map(this::toVantage)
				.toList();
	}

	private Vantage toVantage(Entry<String, Object> vantageEntry) {
		if (vantageEntry.getKey().startsWith("ADV_")) {
			return toAdvantage(vantageEntry);
		} else if (vantageEntry.getKey().startsWith("DISADV_")) {
			return toDisadvantage(vantageEntry);
		}
		return null;
	}

	private Vantage toAdvantage(Entry<String, Object> vantageEntry) {
		var advantage = (Map<String, Object>) YAML_FILES.get(Pair.of(character.locale(), "Advantages.yaml")).get(vantageEntry.getKey());
		return new Vantage((String) advantage.get("name"), "", "");
	}

	private Vantage toDisadvantage(Entry<String, Object> vantageEntry) {
		var disadvantage = (Map<String, Object>) getYaml(character.locale(), "Disadvantages.yaml").get(vantageEntry.getKey());
		return new Vantage((String) disadvantage.get("name"), "", "");
	}

	private List<Special> toSpecials() {
		return character.activatable().entrySet().stream()
				.filter(e -> e.getKey().startsWith("SA_"))
				.map(this::toSpecial)
				.toList();
	}

	private Special toSpecial(Entry<String, Object> specialEntry) {
		var special = (Map<String, Object>) getYaml(character.locale(), "SpecialAbilities.yaml").get(specialEntry.getKey());
		return new Special((String) special.get("name"), "", "", "");
	}
}
