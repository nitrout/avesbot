package de.avesbot;

import de.avesbot.model.Outcome;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Ruleset;
import de.avesbot.model.Vantage;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DiceSimulatorTest {

	private static final byte[] BASE_STATS= {10, 10, 10, 10, 10, 10, 10, 10};
	private static final RoleplayCharacter HERO_4 = new RoleplayCharacter("Hero 4", Ruleset.TDE4, BASE_STATS, List.of(), List.of());
	private static final RoleplayCharacter CLUMSY_HERO_4 = new RoleplayCharacter("Hero 4", Ruleset.TDE4, BASE_STATS, List.of(new Vantage("Tollpatsch", "", "")), List.of());
	private static final RoleplayCharacter WILD_MAGIC_HERO_4 = new RoleplayCharacter("Hero 4", Ruleset.TDE4, BASE_STATS, List.of(new Vantage("Wilde Magie", "", "")), List.of());
	private static final RoleplayCharacter SOLID_HERO_4 = new RoleplayCharacter("Hero 4", Ruleset.TDE4, BASE_STATS, List.of(new Vantage("Feste Matrix", "", "")), List.of());

	private static Pair<Integer, Integer>[] skillTrial(int a, int b, int c) {
		return (Pair<Integer, Integer>[]) new Pair[] {
				Pair.of(a, 20),
				Pair.of(b, 20),
				Pair.of(c, 20)
		};
	}

	@Test
	public void testSpellOutcome4() {
		Assert.assertEquals(Outcome.SUCCESS, DiceSimulator.spellOutcome4(HERO_4, skillTrial(9, 9, 9), (byte) 1));
		Assert.assertEquals(Outcome.SUCCESS, DiceSimulator.spellOutcome4(HERO_4, skillTrial(9, 9, 9), (byte) 0));
		Assert.assertEquals(Outcome.FAILURE, DiceSimulator.spellOutcome4(HERO_4, skillTrial(9, 9, 9), (byte) -1));

		Assert.assertEquals(Outcome.SPLENDOR, DiceSimulator.spellOutcome4(HERO_4, skillTrial(1, 1, 9), (byte) -1));
		Assert.assertEquals(Outcome.SPLENDOR, DiceSimulator.spellOutcome4(HERO_4, skillTrial(1, 9, 1), (byte) -1));
		Assert.assertEquals(Outcome.SPLENDOR, DiceSimulator.spellOutcome4(HERO_4, skillTrial(9, 1, 1), (byte) -1));

		Assert.assertEquals(Outcome.SLIP, DiceSimulator.spellOutcome4(HERO_4, skillTrial(20, 20, 9), (byte) 1));
		Assert.assertEquals(Outcome.SLIP, DiceSimulator.spellOutcome4(HERO_4, skillTrial(20, 9, 20), (byte) 1));
		Assert.assertEquals(Outcome.SLIP, DiceSimulator.spellOutcome4(HERO_4, skillTrial(9, 20, 20), (byte) 1));

		Assert.assertEquals(Outcome.SLIP, DiceSimulator.spellOutcome4(WILD_MAGIC_HERO_4, skillTrial(19, 19, 9), (byte) 1));
		Assert.assertEquals(Outcome.SLIP, DiceSimulator.spellOutcome4(WILD_MAGIC_HERO_4, skillTrial(19, 9, 19), (byte) 1));
		Assert.assertEquals(Outcome.SLIP, DiceSimulator.spellOutcome4(WILD_MAGIC_HERO_4, skillTrial(9, 19, 19), (byte) 1));

		Assert.assertEquals(Outcome.SUCCESS, DiceSimulator.spellOutcome4(SOLID_HERO_4, skillTrial(20, 20, 9), (byte) 1));
		Assert.assertEquals(Outcome.FAILURE, DiceSimulator.spellOutcome4(SOLID_HERO_4, skillTrial(20, 20, 9), (byte) -1));
		Assert.assertEquals(Outcome.SLIP, DiceSimulator.spellOutcome4(SOLID_HERO_4, skillTrial(20, 20, 18), (byte) 1));

		Assert.assertEquals(Outcome.SUCCESS, DiceSimulator.spellOutcome4(CLUMSY_HERO_4, skillTrial(19, 19, 9), (byte) 1));
	}

	@Test
	public void testNonSpellOutcome4() {
		Assert.assertEquals(Outcome.SUCCESS, DiceSimulator.nonSpellOutcome4(HERO_4, skillTrial(9, 9, 9), (byte) 1));
		Assert.assertEquals(Outcome.SUCCESS, DiceSimulator.nonSpellOutcome4(HERO_4, skillTrial(9, 9, 9), (byte) 0));
		Assert.assertEquals(Outcome.FAILURE, DiceSimulator.nonSpellOutcome4(HERO_4, skillTrial(9, 9, 9), (byte) -1));

		Assert.assertEquals(Outcome.SPLENDOR, DiceSimulator.nonSpellOutcome4(HERO_4, skillTrial(1, 1, 9), (byte) -1));
		Assert.assertEquals(Outcome.SPLENDOR, DiceSimulator.nonSpellOutcome4(HERO_4, skillTrial(1, 9, 1), (byte) -1));
		Assert.assertEquals(Outcome.SPLENDOR, DiceSimulator.nonSpellOutcome4(HERO_4, skillTrial(9, 1, 1), (byte) -1));

		Assert.assertEquals(Outcome.SLIP, DiceSimulator.nonSpellOutcome4(HERO_4, skillTrial(20, 20, 9), (byte) 1));
		Assert.assertEquals(Outcome.SLIP, DiceSimulator.nonSpellOutcome4(HERO_4, skillTrial(20, 9, 20), (byte) 1));
		Assert.assertEquals(Outcome.SLIP, DiceSimulator.nonSpellOutcome4(HERO_4, skillTrial(9, 20, 20), (byte) 1));

		Assert.assertEquals(Outcome.SLIP, DiceSimulator.nonSpellOutcome4(CLUMSY_HERO_4, skillTrial(19, 19, 9), (byte) 1));
		Assert.assertEquals(Outcome.SLIP, DiceSimulator.nonSpellOutcome4(CLUMSY_HERO_4, skillTrial(19, 9, 19), (byte) 1));
		Assert.assertEquals(Outcome.SLIP, DiceSimulator.nonSpellOutcome4(CLUMSY_HERO_4, skillTrial(9, 19, 19), (byte) 1));

		Assert.assertEquals(Outcome.SLIP, DiceSimulator.nonSpellOutcome4(SOLID_HERO_4, skillTrial(20, 20, 9), (byte) 1));
		Assert.assertEquals(Outcome.SUCCESS, DiceSimulator.nonSpellOutcome4(WILD_MAGIC_HERO_4, skillTrial(19, 19, 9), (byte) 1));
	}
}