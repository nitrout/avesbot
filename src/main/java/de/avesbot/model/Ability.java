package de.avesbot.model;

/**
 * The ability of a character
 * @author nitrout
 */
public class Ability {
	
	public enum Type {
		TALENT, SPELL, LITURGY;
	}
	
	private String id;
	private String name;
	private Tradition rep;
	private Trial trial;
	private byte taw;
	private Type type;
	
	/**
	 * Creates ability from database.
	 * 
	 * @param id the abilities id
	 * @param name name of the ability
	 * @param rep the representation of the ability
	 * @param trial the trial of the ability
	 * @param taw the abilities taw
	 * @param type type of the ability
	 */
	public Ability(String id, String name, Tradition rep, Trial trial, byte taw, Type type) {
		this.id = id;
		this.name = name;
		this.rep = rep;
		this.trial = trial;
		this.taw = taw;
		this.type = type;
	}
	
	/**
	 * Creates a new ability.
	 * 
	 * @param name name of the ability
	 * @param rep the representation of the ability
	 * @param trial the trial of the ability
	 * @param taw the abilities taw
	 * @param type type of the ability
	 */
	public Ability(String name, Tradition rep, Trial trial, byte taw, Type type) {
		this("0", name, rep, trial, taw, type);
	}

	/**
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param rep the rep to set
	 */
	public void setRep(Tradition rep) {
		this.rep = rep;
	}
	
	/**
	 * @return the rep
	 */
	public Tradition getRep() {
		return rep;
	}

	/**
	 * 
	 * @return the trial
	 */
	public Trial getTrial() {
		return trial;
	}

	/**
	 * 
	 * @param trial the trial to set
	 */
	public void setTrial(Trial trial) {
		this.trial = trial;
	}

	/**
	 * 
	 * @return the taw
	 */
	public byte getTaw() {
		return taw;
	}

	/**
	 * 
	 * @param taw the taw to set
	 */
	public void setTaw(byte taw) {
		this.taw = taw;
	}

	/**
	 * @return the spell
	 * @deprecated 
	 */
	@Deprecated
	public boolean isSpell() {
		return this.getType() == Type.SPELL;
	}

	/**
	 * @param spell the spell to set
	 * @deprecated 
	 */
	@Deprecated
	public void setSpell(boolean spell) {
		this.setType(spell ? Type.SPELL : Type.TALENT);
	}
	
		/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 
	 * @return the string representation
	 */
	@Override
	public String toString() {
		
		return String.format("%s %s%s%s\t%d", getType(), name, rep != Tradition.NONE ? "["+rep.name()+"]" : "", trial, taw);
	}
}
