package de.avesbot.model;

import java.util.List;
import org.apache.commons.lang3.NotImplementedException;

/**
 * This class represents a hero player character.
 *
 * @author nitrout
 */
public record RoleplayCharacter(String id, String name, Ruleset ruleset, byte cou, byte sgc, byte intu, byte cha, byte dex, byte agi, byte con, byte str,
		List<Vantage> vantages, List<Special> specials) {

	/**
	 * represents one of the spellcaster (dis)advantages.
	 *
	 * @deprecated replaced with Vantage
	 */
	@Deprecated
	public enum SpellcasterMod {
		NONE, WILD, SOLID
	}

	/**
	 * Creates a new character.
	 *
	 * @param id id of the character
	 * @param name name of the character
	 * @param rule ruleset of the character DSA4/DSA5
	 * @param cou the courage attribute
	 * @param sgc the sagacity attribute
	 * @param intu the intuition attribute
	 * @param cha the charisma attribute
	 * @param dex the dexterity attribute
	 * @param agi the agility attribute
	 * @param con the constitution attribute
	 * @param str the strength attribute
	 * @param vantages the (dis)advantages of the character
	 * @param specials the special abilities of the character
	 */
	public RoleplayCharacter(String id, String name, Ruleset rule, byte cou, byte sgc, byte intu, byte cha, byte dex, byte agi, byte con, byte str,
			Vantage[] vantages, Special[] specials) {
		this(id, name, rule, cou, sgc, intu, cha, dex, agi, con, str, List.of(vantages), List.of(specials));
	}

	/**
	 * Creates a new character
	 *
	 * @param name name of the character
	 * @param rule ruleset of the character DSA4/DSA5
	 * @param cou the courage attribute
	 * @param sgc the sagacity attribute
	 * @param intu the intuition attribute
	 * @param cha the charisma attribute
	 * @param dex the dexterity attribute
	 * @param agi the agility attribute
	 * @param con the constitution attribute
	 * @param str the strength attribute
	 * @param vantages the (dis)advantages of the character
	 * @param specials the special abilities of the character
	 */
	public RoleplayCharacter(String name, Ruleset rule, byte cou, byte sgc, byte intu, byte cha, byte dex, byte agi, byte con, byte str, Vantage[] vantages,
			Special[] specials) {
		this("0", name, rule, cou, sgc, intu, cha, dex, agi, con, str, vantages, specials);
	}

	/**
	 * Creates a new character.
	 *
	 * @param id id of the character
	 * @param name name of the character
	 * @param rule ruleset of the character DSA4/DSA5
	 * @param vantages the (dis)advantages of the character
	 * @param specials the special abilities of the character
	 * @param attributes the character attributes intuition order: courage, sagacity, intuition, charisma, dexterity, agility, constitution, strength
	 */
	public RoleplayCharacter(String id, String name, Ruleset rule, Vantage[] vantages, Special[] specials, byte... attributes) {
		this(id, name, rule,
				attributes[Attribute.COURAGE.ordinal()], attributes[Attribute.SAGACITY.ordinal()], attributes[Attribute.INTUITION.ordinal()], attributes[Attribute.CHARISMA.ordinal()],
				attributes[Attribute.DEXTERITY.ordinal()], attributes[Attribute.AGILITY.ordinal()], attributes[Attribute.CONSTITUTION.ordinal()], attributes[Attribute.STRENGTH.ordinal()],
				vantages, specials);
	}

	/**
	 * Creates a new character.
	 *
	 * @param name name of the character
	 * @param rule ruleset of the character DSA4/DSA5
	 * @param vantages the (dis)advantages of the character
	 * @param specials the special abilities of the character
	 * @param attributes the character attributes intuition order: courage, sagacity, intuition, charisma, dexterity, agility, constitution, strength
	 */
	public RoleplayCharacter(String name, Ruleset rule, Vantage[] vantages, Special[] specials, byte... attributes) {
		this("0", name, rule, vantages, specials, attributes);
	}

	/**
	 * Creates a new character.
	 *
	 * @param id id of the character
	 * @param name name of the character
	 * @param rule ruleset of the character DSA4/DSA5
	 * @param vantages the (dis)advantages of the character
	 * @param specials the special abilities of the character
	 * @param attributes the character attributes intuition order: courage, sagacity, intuition, charisma, dexterity, agility, constitution, strength
	 */
	public RoleplayCharacter(String id, String name, Ruleset rule, Vantage[] vantages, Special[] specials, Byte... attributes) {
		this(id, name, rule,
				attributes[Attribute.COURAGE.ordinal()].byteValue(), attributes[Attribute.SAGACITY.ordinal()].byteValue(),
				attributes[Attribute.INTUITION.ordinal()].byteValue(), attributes[Attribute.CHARISMA.ordinal()].byteValue(),
				attributes[Attribute.DEXTERITY.ordinal()].byteValue(), attributes[Attribute.AGILITY.ordinal()].byteValue(),
				attributes[Attribute.CONSTITUTION.ordinal()].byteValue(), attributes[Attribute.STRENGTH.ordinal()].byteValue(),
				vantages, specials);
	}

	/**
	 * Creates a new character.
	 *
	 * @param name name of the character
	 * @param rule ruleset of the character DSA4/DSA5
	 * @param vantages the (dis)advantages of the character
	 * @param specials the special abilities of the character
	 * @param attributes the character attributes intuition order: courage, sagacity, intuition, charisma, dexterity, agility, constitution, strength
	 */
	public RoleplayCharacter(String name, Ruleset rule, Vantage[] vantages, Special[] specials, Byte... attributes) {
		this("0", name, rule, vantages, specials, attributes);
	}

	/**
	 * Creates a copy of the character with a new id.
	 *
	 * @param id the new id
	 * @param chara original character
	 */
	public RoleplayCharacter(String id, RoleplayCharacter chara) {
		this(id, chara.name, chara.ruleset, chara.cou, chara.sgc, chara.intu, chara.cha, chara.dex, chara.agi, chara.con, chara.str, chara.vantages.toArray(Vantage[]::new), chara.specials.toArray(Special[]::new));
	}

	/**
	 *
	 * @param att the attribute to get
	 * @return the attribute
	 */
	public byte getAttribute(Attribute att) {
		byte result = 0;
		result = switch (att) {
			case COURAGE ->
				this.cou;
			case SAGACITY ->
				this.sgc;
			case INTUITION ->
				this.intu;
			case CHARISMA ->
				this.cha;
			case DEXTERITY ->
				this.dex;
			case AGILITY ->
				this.agi;
			case CONSTITUTION ->
				this.con;
			case STRENGTH ->
				this.str;
		};
		return result;
	}

	/**
	 * @deprecated use method hasVantage instead
	 * @return the clumsy
	 */
	@Deprecated
	public boolean isClumsy() {
		return this.hasVantage("Tollpatsch");
	}

	/**
	 * @deprecated use vantage instead
	 * @param clumsy the clumsy to set
	 */
	@Deprecated
	public void setClumsy(boolean clumsy) {
		throw new NotImplementedException();
	}

	/**
	 * @deprecated use method hasVantage instead
	 * @return the spellcasterMod
	 */
	@Deprecated
	public SpellcasterMod getSpellcasterMod() {
		if (this.hasVantage("Feste Matrix")) {
			return SpellcasterMod.SOLID;
		} else if (this.hasVantage("Wilde Magie")) {
			return SpellcasterMod.WILD;
		} else {
			return SpellcasterMod.NONE;
		}
	}

	/**
	 * @deprecated use vantage instead
	 * @param spellcasterMod the spellcasterMod to set
	 */
	@Deprecated
	public void setSpellcasterMod(SpellcasterMod spellcasterMod) {
		throw new NotImplementedException();
	}

	/**
	 * Modifies all attributes.
	 *
	 * @param mod the amount to modify
	 */
	public RoleplayCharacter modifyAttributes(byte mod) {

		return new RoleplayCharacter(id, name, ruleset, (byte) (cou + mod), (byte) (sgc + mod), (byte) (intu + mod), (byte) (cha + mod), (byte) (dex + mod), (byte) (agi + mod), (byte) (con + mod), (byte) (str + mod), vantages, specials);
	}

	/**
	 * Get the complete list of character's (dis)advantages.
	 *
	 * @return the vantages
	 */
	public List<Vantage> getVantages() {
		return vantages;
	}

	/**
	 * Adds all the given (dis)advantages to the character.
	 *
	 * @param vantages the vantages to add
	 */
	public void addVantages(List<Vantage> vantages) {
		this.vantages.addAll(vantages);
	}

	/**
	 * Checks if the character has the given dis)advantage.
	 *
	 * @param vantageName the name of the vantage to check
	 * @return true if the character has the vantage
	 */
	public boolean hasVantage(String vantageName) {
		return this.vantages.stream().anyMatch(vantage -> vantage.name().equals(vantageName));
	}

	/**
	 * Get the complete list of character's special abilities.
	 *
	 * @return the vantages
	 */
	public List<Special> getSpecials() {
		return specials;
	}

	/**
	 * Adds all the given special abilities to the character.
	 *
	 * @param specials the vantages to add
	 */
	public void addSpecials(List<Special> specials) {
		this.specials.addAll(specials);
	}

	/**
	 * Checks if the character has the given special ability.
	 *
	 * @param specialName the name of the vantage to check
	 * @return true if the character has the vantage
	 */
	public boolean hasSpecial(String specialName) {
		return this.specials.stream().anyMatch(special -> special.name().equals(specialName));
	}

	@Override
	public String toString() {

		return String.format("%s[COU:%d SGC:%d INT:%d CHA:%d DEX:%d AGI:%d CON:%d STR:%d]",
				name, cou, sgc, intu, cha, dex, agi, con, str);
	}

	/**
	 * Returns a string with the trial attribute values of the character.
	 *
	 * @param t
	 * @return
	 */
	public String getTrialValues(Trial t) {

		return String.format("(%d/%d/%d)", this.getAttribute(t.attribute1()), this.getAttribute(t.attribute2()), this.getAttribute(t.attribute3()));
	}
}
