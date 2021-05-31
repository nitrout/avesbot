package de.avesbot.model;

/**
 * The base attributes.
 * @author nitrout
 */
public enum Attribute {
	
	COURAGE("COU"), SAGACITY("SGC"), INTUITION("INT"), CHARISMA("CHA"), DEXTERITY("DEX"), AGILITY("AGI"), CONSTITUTION("CON"), STRENGTH("STR");
	
	private final String abbrevation;
	
	private Attribute(String abbrevation) {
		this.abbrevation = abbrevation;
		
	}
	
	public static Attribute abbrevationValueOf(String abbrevation) {
		
		for(Attribute a : Attribute.values()) {
			if(a.getAbbrevation().equals(abbrevation))
				return a;
		}
		
		throw new IllegalArgumentException(abbrevation+" is not a valid abbreavation.");
	}

	/**
	 * @return the abbrevation
	 */
	public String getAbbrevation() {
		return abbrevation;
	}
}