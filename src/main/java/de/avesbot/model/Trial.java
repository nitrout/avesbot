package de.avesbot.model;

/**
 * Represents a collection of base attributes fpr a 3D20 standard trial.
 * @author nitrout
 */
public class Trial {
		
	private Attribute[] atts;

	/**
	 * Creates a new standard trial.
	 * 
	 * @param att1 first attribute
	 * @param att2 second attribute
	 * @param att3 third attribute
	 */
	public Trial(Attribute att1, Attribute att2, Attribute att3) {
		this.atts = new Attribute[3];
		this.atts[0] = att1;
		this.atts[1] = att2;
		this.atts[2] = att3;
	}

	/**
	 * Get the first attribute.
	 * @return the first attribute
	 */
	public Attribute getAtt1() {
		return atts[0];
	}

	/**
	 * Get the second attribute.
	 * @return the second attribute
	 */
	public Attribute getAtt2() {
		return atts[1];
	}

	/**
	 * Get the third attribute.
	 * @return the third attribute
	 */
	public Attribute getAtt3() {
		return atts[2];
	}

	/**
	 * get all trial attributes
	 * @return the trials attributes
	 */
	public Attribute[] getAttributes() {
		return atts;
	}

	@Override
	public String toString() {
		
		return String.format("(%s/%s/%s)", atts[0], atts[1], atts[2]);
	}
}