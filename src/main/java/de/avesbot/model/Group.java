package de.avesbot.model;

/**
 * Representation of a group of heroes.
 * @author Nitrout
 */
public class Group {
	
	private String groupId;
	private String userId;
	private String guildId;
	private String name;
	
	/**
	 * Creates a new group.
	 * 
	 * @param groupId the groups id
	 * @param userId
	 * @param guildId
	 * @param name 
	 */
	public Group(String groupId, String userId, String guildId, String name) {
		this.groupId = groupId;
		this.userId = userId;
		this.guildId = guildId;
		this.name = name;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the guildId
	 */
	public String getGuildId() {
		return guildId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}