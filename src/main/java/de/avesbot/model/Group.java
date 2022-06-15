package de.avesbot.model;

/**
 * Representation of a group of heroes.
 * @author Nitrout
 */
public record Group(String groupId, String userId, String guildId, String name) {
	
	@Override
	public String toString() {
		return this.name;
	}
}