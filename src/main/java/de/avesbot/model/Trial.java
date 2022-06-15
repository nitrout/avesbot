package de.avesbot.model;

/**
 * Represents a collection of base attributes fpr a 3D20 standard trial.
 * @author nitrout
 */
public record Trial(Attribute attribute1, Attribute attribute2, Attribute attribute3) {

	/**
	 * get all trial attributes
	 * @return the trials attributes
	 */
	public Attribute[] getAttributes() {
		return new Attribute[] {attribute1, attribute2, attribute3};
	}

	@Override
	public String toString() {
		
		return String.format("(%s/%s/%s)", attribute1, attribute2, attribute3);
	}
}