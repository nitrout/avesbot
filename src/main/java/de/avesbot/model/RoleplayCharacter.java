package de.avesbot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;

/**
 * This class represents a hero player character.
 * 
 * @author nitrout
 */
public class RoleplayCharacter {
	
	/**
	 * represents one of the spellcaster (dis)advantages.
	 * @deprecated replaced with Vantage
	 */
	@Deprecated
	public enum SpellcasterMod {
		NONE, WILD, SOLID
	}
	
	private String id;
	private String name;
	private byte courage;
	private byte sagacity;
	private byte intuition;
	private byte charisma;
	private byte dexterity;
	private byte agility;
	private byte constitution;
	private byte strength;
	private ArrayList<Vantage> vantages;
	private ArrayList<Special> specials;
	private Ruleset ruleset;
	
	/**
	 * Creates a new character.
	 * 
	 * @param id id of the character
	 * @param name name of the character
	 * @param rule ruleset of the character DSA4/DSA5
	 * @param cou the courage attribute
	 * @param sag the sagacity attribute
	 * @param intu the intuition attribute
	 * @param cha the charisma attribute
	 * @param dex the dexterity attribute
	 * @param agi the agility attribute
	 * @param con the constitution attribute
	 * @param str the strength attribute
	 * @param vantages the (dis)advantages of the character
	 * @param specials the special abilities of the character
	 */
	public RoleplayCharacter(String id, String name, Ruleset rule, byte cou, byte sag, byte intu, byte cha, byte dex, byte agi, byte con, byte str, Vantage[] vantages, Special[] specials) {
		this.id = id;
		this.name = name;
		this.courage = cou;
		this.sagacity = sag;
		this.intuition = intu;
		this.charisma = cha;
		this.dexterity = dex;
		this.agility = agi;
		this.constitution = con;
		this.strength = str;
		this.vantages = new ArrayList<>();
		this.vantages.addAll(Arrays.asList(vantages));
		this.specials = new ArrayList<>();
		this.specials.addAll(Arrays.asList(specials));
		this.ruleset = rule;
	}
	
	/**
	 * Creates a new character
	 * 
	 * @param name name of the character
	 * @param rule ruleset of the character DSA4/DSA5
	 * @param cou the courage attribute
	 * @param sag the sagacity attribute
	 * @param intu the intuition attribute
	 * @param cha the charisma attribute
	 * @param dex the dexterity attribute
	 * @param agi the agility attribute
	 * @param con the constitution attribute
	 * @param str the strength attribute
	 * @param vantages the (dis)advantages of the character
	 * @param specials the special abilities of the character
	 */
	public RoleplayCharacter(String name, Ruleset rule, byte cou, byte sag, byte intu, byte cha, byte dex, byte agi, byte con, byte str, Vantage[] vantages, Special[] specials) {
		this("0", name, rule, cou, sag, intu, cha, dex, agi, con, str, vantages, specials);
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
	public RoleplayCharacter(String id, String name, Ruleset rule, Vantage[] vantages, Special[] specials, byte...attributes) {
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
	public RoleplayCharacter(String name, Ruleset rule, Vantage[] vantages, Special[] specials, byte...attributes) {
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
	public RoleplayCharacter(String id, String name, Ruleset rule, Vantage[] vantages, Special[] specials, Byte...attributes) {
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
	public RoleplayCharacter(String name, Ruleset rule, Vantage[] vantages, Special[] specials, Byte...attributes) {
		this("0", name, rule, vantages, specials, attributes);
	}
	
	/**
	 * Creates a copy of the character with a new id.
	 * 
	 * @param id the new id
	 * @param chara original character
	 */
	public RoleplayCharacter(String id, RoleplayCharacter chara) {
		this(id, chara.name, chara.ruleset, chara.courage, chara.sagacity, chara.intuition, chara.charisma, chara.dexterity, chara.agility, chara.constitution, chara.strength, chara.vantages.toArray(Vantage[]::new), chara.specials.toArray(Special[]::new));
	}
	
	/**
	 * @return the courage
	 */
	public byte getCourage() {
		return courage;
	}
	
	/**
	 * @param courage the courage to set
	 */
	public void setCourage(byte courage) {
		this.courage = courage;
	}
	
	/**
	 * @return the sagacity
	 */
	public byte getSagacity() {
		return sagacity;
	}
	
	/**
	 * @param sagacity the sagacity to set
	 */
	public void setSagacity(byte sagacity) {
		this.sagacity = sagacity;
	}
	
	/**
	 * @return the intuition
	 */
	public byte getIntuition() {
		return intuition;
	}
	
	/**
	 * @param intuition the intuition to set
	 */
	public void setIntuition(byte intuition) {
		this.intuition = intuition;
	}
	
	/**
	 * @return the charisma
	 */
	public byte getCharisma() {
		return charisma;
	}
	
	/**
	 * @param charisma the charisma to set
	 */
	public void setCharisma(byte charisma) {
		this.charisma = charisma;
	}
	
	/**
	 * @return the dexterity
	 */
	public byte getDexterity() {
		return dexterity;
	}
	
	/**
	 * @param dexterity the dexterity to set
	 */
	public void setDexterity(byte dexterity) {
		this.dexterity = dexterity;
	}
	
	/**
	 * @return the agility
	 */
	public byte getAgility() {
		return agility;
	}
	
	/**
	 * @param agility the agility to set
	 */
	public void setAgility(byte agility) {
		this.agility = agility;
	}
	
	/**
	 * @return the constitution
	 */
	public byte getConstitution() {
		return constitution;
	}
	
	/**
	 * @param constitution the constitution to set
	 */
	public void setConstitution(byte constitution) {
		this.constitution = constitution;
	}
	
	/**
	 * @return the strength
	 */
	public byte getStrength() {
		return strength;
	}
	
	/**
	 * @param strength the strength to set
	 */
	public void setStrength(byte strength) {
		this.strength = strength;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @param att the attribute to get
	 * @return the attribute 
	 */
	public byte getAttribute(Attribute att) {
		byte result = 0;
		switch(att) {
			case COURAGE : result = this.getCourage(); break;
			case SAGACITY : result = this.getSagacity(); break;
			case INTUITION : result = this.getIntuition(); break;
			case CHARISMA : result = this.getCharisma(); break;
			case DEXTERITY : result = this.getDexterity(); break;
			case AGILITY : result = this.getAgility(); break;
			case CONSTITUTION : result = this.getConstitution(); break;
			case STRENGTH : result = this.getStrength(); break;
		}
		return result;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
		if(this.hasVantage("Feste Matrix")) {
			return SpellcasterMod.SOLID;
		} else if(this.hasVantage("Wilde Magie")) {
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
	 * @return the ruleset
	 */
	public Ruleset getRuleset() {
		return ruleset;
	}

	/**
	 * @param ruleset the ruleset to set
	 */
	public void setRuleset(Ruleset ruleset) {
		this.ruleset = ruleset;
	}
	
	/**
	 * Modifies all attributes.
	 * @param mod the amount to modify
	 */
	public void modifyAttributes(byte mod) {
		this.courage += mod;
		this.sagacity += mod;
		this.intuition += mod;
		this.charisma += mod;
		this.dexterity += mod;
		this.agility += mod;
		this.constitution += mod;
		this.strength += mod;
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
	public void setVantages(List<Vantage> vantages) {
		this.vantages.addAll(vantages);
	}
	
	/**
	 * Checks if the character has the given dis)advantage.
	 * 
	 * @param vantageName the name of the vantage to check
	 * @return true if the character has the vantage
	 */
	public boolean hasVantage(String vantageName) {
		return this.vantages.stream().anyMatch(vantage -> vantage.getName().equals(vantageName));
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
	public void setSpecials(List<Special> specials) {
		this.specials.addAll(specials);
	}
	
	/**
	 * Checks if the character has the given special ability.
	 * 
	 * @param specialName the name of the vantage to check
	 * @return true if the character has the vantage
	 */
	public boolean hasSpecial(String specialName) {
		return this.specials.stream().anyMatch(special -> special.getName().equals(specialName));
	}
	
	@Override
	public String toString() {
		
		return String.format("%s[COU:%d SGC:%d INT:%d CHA:%d DEX:%d AGI:%d CON:%d STR:%d]",
								name, courage, sagacity, intuition, charisma, dexterity, agility, constitution, strength);
	}
	
	/**
	 * Returns a string with the trial attribute values of the character.
	 * @param t
	 * @return 
	 */
	public String getTrialValues(Trial t) {
		
		return String.format("(%d/%d/%d)", this.getAttribute(t.getAtt1()), this.getAttribute(t.getAtt2()), this.getAttribute(t.getAtt3()));
	}
}
