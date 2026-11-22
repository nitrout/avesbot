package de.avesbot;

import de.avesbot.model.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

import static java.lang.Math.ceilDiv;
import static java.lang.Math.clamp;

/**
 * Simulator for dice rolls.
 * @author nitrout
 */
public class DiceSimulator {
	
	private final Random rand;
	
	public DiceSimulator() {
		rand = new Random();
	}
	
	/**
	 * Roll one or more dices.
	 * @param num the number of dices to roll
	 * @param dice the amount of areas of the dices
	 * @return the dice rolls
	 */
	public List<Pair<Integer, Integer>> rollDice(int num, int dice) {
		return rand.ints(num, 1, dice + 1)
				.mapToObj(i -> Pair.of(i, dice))
				.toList();
	}
	
	/**
	 * Roll dices and sum up the results.
	 * @param num the number of dices to roll
	 * @param die the amount of areas of the dices
	 * @param mod the amount which will be added after the sum up.
	 * @return the sum of the dices and the mod
	 */
	public Pair<List<Pair<Integer, Integer>>, Integer> sumDice(int num, int die, int mod) {

		List<Pair<Integer, Integer>> dice = rollDice(num, die);

		return Pair.of(dice, dice.stream().mapToInt(Pair::getLeft).sum() + mod);
	}
	
	/**
	 * Rolls a symbol dice.
	 * @param dice the symbol dice to be rolled
	 * @return the name of the rolled symbol
	 */
	public String rollSymbolDice(SymbolDice dice) {
		
		return dice.side(this.rand.nextInt(dice.values().length));
	}
	
	/**
	 * Executes a character attribute roll in DSA4 ruleset.
	 * @param chara the character executing the attribute roll
	 * @param difficulty difficulty modyfing the dice roll result
	 * @param att the attribute comparing with the dice roll
	 * @return the result of the attribute roll
	 */
	public RollResult rollAttribute4(RoleplayCharacter chara, int difficulty, Attribute att) {

		Pair<Integer, Integer> roll = rollDice(1, 20).getFirst();
		Outcome outcome;
		
		if(roll.getLeft() == 1) {
			outcome = Outcome.SPLENDOR;
		} else if(roll.getLeft() == 20) {
			outcome = Outcome.SLIP;
		} else if(roll.getLeft()+difficulty > chara.getAttribute(att)) {
			outcome = Outcome.FAILURE;
		} else {
			outcome = Outcome.SUCCESS;
		}
		
		return new SimpleRollResult(outcome, roll);
	}
	
	/**
	 * Executes a character attribute roll in DSA4 ruleset with a diffculty modifier of 0.
	 * @param chara the character executing the attribute roll
	 * @param att the attribute comparing with the dice roll
	 * @return the result of the attribute roll
	 */
	public RollResult rollAttribute4(RoleplayCharacter chara, Attribute att) {
		return rollAttribute4(chara, 0, att);
	}
	
	/**
	 * Executes a character attribute roll in DSA5 ruleset.
	 * @param chara the character executing the attribute roll
	 * @param difficulty difficulty modyfing the effective attribute value
	 * @param att the attribute comparing with the dice roll
	 * @return the result of the attribute roll
	 */
	public RollResult rollAttribute5(RoleplayCharacter chara, int difficulty, Attribute att) {

		List<Pair<Integer, Integer>> rolls = rollDice(2, 20);
		RollResult result;
		
		int effectiveValue = chara.getAttribute(att)+difficulty;

		if (rolls.get(0).getLeft() == 1 && rolls.get(1).getLeft() <= effectiveValue) {
			result = new SimpleRollResult(Outcome.SPLENDOR, rolls);
		} else if (rolls.get(0).getLeft() == 20 && rolls.get(1).getLeft() > effectiveValue) {
			result = new SimpleRollResult(Outcome.SLIP, rolls);
		} else if (rolls.get(0).getLeft() > effectiveValue) {
			result = new SimpleRollResult(Outcome.FAILURE, rolls.get(0));
		} else {
			result = new SimpleRollResult(Outcome.SUCCESS, rolls.get(0));
		}
		
		return result;
	}
	
	/**
	 * Executes a character attribute roll in DSA5 ruleset with a difficulty modifier of 0.
	 * @param chara the character executing the attribute roll
	 * @param att the attribute comparing with the dice roll
	 * @return the result of the attribute roll
	 */
	public RollResult rollAttribute5(RoleplayCharacter chara, Attribute att) {
		return rollAttribute5(chara, 0, att);
	}
	
	/**
	 * Executes a skill dice roll in DSA4 ruleset.
	 * @param chara the character executing the skill roll
	 * @param trial the trial of the skill roll
	 * @param taw the skill value of the character
	 * @param difficulty the difficulty of the skill roll
	 * @param spell is the skill a spell
	 * @return the result of the skill roll
	 */
	public SkillRollResult4 rollSkill4(RoleplayCharacter chara, Trial trial, int taw, byte difficulty, boolean spell) {
		
		byte tap = (byte)(taw - difficulty);
		List<Pair<Integer, Integer>> rolls = rollDice(3, 20);
		
		RoleplayCharacter trialChara = chara.modifyAttributes(tap < 0 ? tap : 0);
		if(tap < 0) {
			tap = 0;
		}
		
		for(int i = 0; i < 3; i++) {
			if (rolls.get(i).getLeft() > trialChara.getAttribute(trial.getAttributes()[i])) {
				tap = (byte) (tap - (rolls.get(i).getLeft() - trialChara.getAttribute(trial.getAttributes()[i])));
			}
		}

		final var outcome = spell
				? spellOutcome4(trialChara, rolls, tap)
				: nonSpellOutcome4(trialChara, rolls, tap);
		
		return new SkillRollResult4(outcome, tap <= taw ? tap : taw, rolls);
	}

	protected static Outcome spellOutcome4(RoleplayCharacter chara, List<Pair<Integer, Integer>> rolls, byte tap) {

		final var hasMulti20 = rolls.stream().filter(p -> p.getLeft() >= 20).count() > 1;
		final var isWildMagicSlip = chara.getSpellcasterMod() == RoleplayCharacter.SpellcasterMod.WILD && rolls.stream().filter(p -> p.getLeft() >= 19).count() > 1;
		final var isSlip = chara.getSpellcasterMod() == RoleplayCharacter.SpellcasterMod.NONE && hasMulti20;
		final var isSolidSlip = chara.getSpellcasterMod() == RoleplayCharacter.SpellcasterMod.SOLID && hasMulti20 && rolls.stream().noneMatch(p -> p.getLeft() < 18);

		if(isWildMagicSlip || isSlip || isSolidSlip)
			return Outcome.SLIP;
		else if (rolls.stream().filter(p -> p.getLeft() == 1).count() > 1)
			return Outcome.SPLENDOR;
		else if(tap < 0)
			return Outcome.FAILURE;
		else
			return Outcome.SUCCESS;
	}

	protected static Outcome nonSpellOutcome4(RoleplayCharacter chara, List<Pair<Integer, Integer>> rolls, byte tap) {
		final var isSlip = rolls.stream().filter(p -> p.getLeft() >= (chara.isClumsy() ? 19 : 20)).count() > 1;

		if(isSlip)
			return Outcome.SLIP;
		else if (rolls.stream().filter(p -> p.getLeft() == 1).count() > 1)
			return Outcome.SPLENDOR;
		else if(tap < 0)
			return Outcome.FAILURE;
		else
			return Outcome.SUCCESS;
	}
	
	/**
	 * Executes a skill dice roll in DSA4 ruleset.
	 * @param chara the character executing the skill roll
	 * @param ability the character's ability of the skill roll
	 * @param difficulty the difficulty of the skill roll
	 * @return the result of the skill roll
	 */
	public SkillRollResult4 rollSkill4(RoleplayCharacter chara, Ability ability, byte difficulty) {

		return rollSkill4(chara, ability.trial(), ability.taw(), difficulty, ability.type() == Ability.Type.SPELL);
	}
	
	/**
	 * Executes a skill dice roll in DSA4 ruleset with a difficulty of 0.
	 * @param chara the character executing the skill roll
	 * @param ability the character's ability of the skill roll
	 * @return the result of the skill roll
	 */
	public SkillRollResult4 rollSkill4(RoleplayCharacter chara, Ability ability) {
		
		return rollSkill4(chara, ability, (byte)0);
	}
	
	/**
	 * Executes a skill dice roll in DSA5 ruleset.
	 * @param chara the character executing the skill roll
	 * @param trial the trial of the skill roll
	 * @param taw the skill value of the character
	 * @param difficulty the difficulty of the skill roll
	 * @param spell is the skill a spell
	 * @return the result of the skill roll
	 */
	public SkillRollResult5 rollSkill5(RoleplayCharacter chara, Trial trial, int taw, byte difficulty, boolean spell) {
		
		byte tap = (byte)(taw);
		byte qs;
		List<Pair<Integer, Integer>> rolls = rollDice(3, 20);
		Outcome outcome;
		
		RoleplayCharacter trialChara = chara.modifyAttributes(difficulty);
		
		for(int i = 0; i < 3; i++) {
			if (rolls.get(i).getLeft() > trialChara.getAttribute(trial.getAttributes()[i])) {
				tap = (byte) (tap - (rolls.get(i).getLeft() - trialChara.getAttribute(trial.getAttributes()[i])));
			}
		}

		if (rolls.stream().filter(p -> p.getLeft() >= 20).count() > 1)
			outcome = Outcome.SLIP;
		else if (rolls.stream().filter(p -> p.getLeft() == 1).count() > 1)
			outcome = Outcome.SPLENDOR;
		else if(tap < 0)
			outcome = Outcome.FAILURE;
		else
			outcome = Outcome.SUCCESS;

		if(tap < 0) {
			qs = 0;
		} else {
			qs = (byte) clamp(ceilDiv(tap, 3), 1, 6);
		}
		
		return new SkillRollResult5(outcome, qs, rolls);
	}
	
	/**
	 * Executes a skill dice roll in DSA5 ruleset.
	 * @param chara the character executing the skill roll
	 * @param ability the character's ability of the skill roll
	 * @param difficulty the difficulty of the skill roll
	 * @return the result of the skill roll
	 */
	public SkillRollResult5 rollSkill5(RoleplayCharacter chara, Ability ability, byte difficulty) {

		return rollSkill5(chara, ability.trial(), ability.taw(), difficulty, ability.type() == Ability.Type.SPELL);
	}
	
	/**
	 * Executes a skill dice roll in DSA5 ruleset with a difficulty of 0.
	 * @param chara the character executing the skill roll
	 * @param ability the character's ability of the skill roll
	 * @return the result of the skill roll
	 */
	public SkillRollResult5 rollSkill5(RoleplayCharacter chara, Ability ability) {
		
		return rollSkill5(chara, ability, (byte)0);
	}
}