package de.avesbot.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.avesbot.Avesbot;
import de.avesbot.model.Ability;
import de.avesbot.model.Attribute;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Ruleset;
import de.avesbot.model.Special;
import de.avesbot.model.Tradition;
import de.avesbot.model.Trial;
import de.avesbot.model.Vantage;
import de.avesbot.model.optolith.character.OptolithCharacter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.entities.Message.Attachment;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Nitrout
 */
public class OptolithImport implements BiFunction<Attachment, String, Boolean> {

	private static final ObjectMapper JSON_MAPPER = getObjectMapper();

	private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
	private static final HashMap<Pair<Locale, String>, Map<String, Object>> LOCALIZED_YAML_FILES = new HashMap<>();
	private static final HashMap<String, Map<String, Object>> YAML_FILES = new HashMap<>();

	static {
		YAML_MAPPER.findAndRegisterModules();
	}

	private static Map<String, Object> getYaml(String file) {
		return YAML_FILES.computeIfAbsent(file, OptolithImport::loadYaml);
	}

	private static Map<String, Object> loadYaml(String file) {
		try {
			var stream = Avesbot.getResourceStream("optolith/univ/" + file);
			return (Map<String, Object>) YAML_MAPPER.readValue(stream, List.class).stream()
					.collect(Collectors.toUnmodifiableMap((Map<String, Object> m) -> m.get("id"), m -> m));
		} catch (IOException ex) {
			Logger.getLogger(OptolithImport.class.getName()).log(Level.SEVERE, null, ex);
			return Map.of();
		}
	}

	private static Map<String, Object> getLocalizedYaml(Locale locale, String file) {
		return LOCALIZED_YAML_FILES.computeIfAbsent(Pair.of(locale, file), OptolithImport::loadLocalizedYaml);
	}

	private static Map<String, Object> loadLocalizedYaml(Pair<Locale, String> key) {
		try {
			var stream = Avesbot.getResourceStream("optolith/" + key.getLeft().toLanguageTag() + "/" + key.getRight());
			return (Map<String, Object>) YAML_MAPPER.readValue(stream, List.class).stream()
					.collect(Collectors.toUnmodifiableMap((Map<String, Object> m) -> m.get("id"), m -> m));
		} catch (IOException ex) {
			Logger.getLogger(OptolithImport.class.getName()).log(Level.SEVERE, null, ex);
			return Map.of();
		}
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

		for (Vantage vantage : chara.vantages()) {
			Avesbot.getStatementManager().insertVantage(chara, vantage);
		}

		for (Special special : chara.specials()) {
			Avesbot.getStatementManager().insertSpecial(chara, special);
		}

		for (Ability ability : extractAbilities(character)) {
			Avesbot.getStatementManager().insertAbility(chara, ability);
		}

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
		var advantage = (Map<String, Object>) LOCALIZED_YAML_FILES.get(Pair.of(character.locale(), "Advantages.yaml")).get(vantageEntry.getKey());
		return new Vantage((String) advantage.get("name"), "", "");
	}

	private static Vantage toDisadvantage(OptolithCharacter character, Entry<String, Object> vantageEntry) {
		var disadvantage = (Map<String, Object>) getLocalizedYaml(character.locale(), "Disadvantages.yaml").get(vantageEntry.getKey());
		return new Vantage((String) disadvantage.get("name"), "", "");
	}

	private static List<Special> extractSpecials(OptolithCharacter character) {
		return character.activatable().entrySet().stream()
				.filter(e -> e.getKey().startsWith("SA_"))
				.map(e -> toSpecial(character, e))
				.toList();
	}

	private static Special toSpecial(OptolithCharacter character, Entry<String, Object> specialEntry) {
		var special = (Map<String, Object>) getLocalizedYaml(character.locale(), "SpecialAbilities.yaml").get(specialEntry.getKey());
		return new Special((String) special.get("name"), "", "", "");
	}

	private static List<Ability> extractAbilities(OptolithCharacter character) {
		var list = new LinkedList<Ability>();
		character.talents().entrySet().stream()
				.map(e -> toTalentAbility(character, e))
				.forEach(list::add);
		character.spells().entrySet().stream()
				.map(e -> toSpellAbility(character, e))
				.forEach(list::add);
		character.liturgies().entrySet().stream()
				.map(e -> toLiturgyAbility(character, e))
				.forEach(list::add);
		return List.copyOf(list);
	}

	private static Ability toTalentAbility(OptolithCharacter character, Entry<String, Integer> talentEntry) {
		var talent = (Map<String, Object>) getYaml("Skills.yaml").get(talentEntry.getKey());
		var talentLang = (Map<String, Object>) getLocalizedYaml(character.locale(), "Skills.yaml").get(talentEntry.getKey());
		return new Ability((String) talentLang.get("name"), Tradition.NONE, toTrial(talent), talentEntry.getValue().byteValue(), Ability.Type.TALENT);
	}

	private static Ability toSpellAbility(OptolithCharacter character, Entry<String, Integer> spellEntry) {
		var spell = (Map<String, Object>) getYaml("Spells.yaml").get(spellEntry.getKey());
		var spellLang = (Map<String, Object>) getLocalizedYaml(character.locale(), "Spells.yaml").get(spellEntry.getKey());
		return new Ability((String) spellLang.get("name"), Tradition.NONE, toTrial(spell), spellEntry.getValue().byteValue(), Ability.Type.SPELL);
	}

	private static Ability toLiturgyAbility(OptolithCharacter character, Entry<String, Integer> liturgyEntry) {
		var liturgy = (Map<String, Object>) getYaml("LiturgicalChants.yaml").get(liturgyEntry.getKey());
		var liturgyLang = (Map<String, Object>) getLocalizedYaml(character.locale(), "LiturgicalChants.yaml").get(liturgyEntry.getKey());
		return new Ability((String) liturgyLang.get("name"), Tradition.NONE, toTrial(liturgy), liturgyEntry.getValue().byteValue(), Ability.Type.LITURGY);
	}

	private static Trial toTrial(Map<String, Object> ability) {
		var check1 = (String) ability.get("check1");
		var check2 = (String) ability.get("check2");
		var check3 = (String) ability.get("check3");

		var check1Pos = Integer.parseInt(check1.substring(check1.length() - 1));
		var check2Pos = Integer.parseInt(check2.substring(check2.length() - 1));
		var check3Pos = Integer.parseInt(check3.substring(check3.length() - 1));

		return new Trial(Attribute.values()[check1Pos], Attribute.values()[check2Pos], Attribute.values()[check3Pos]);
	}
}
