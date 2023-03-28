package de.avesbot.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import de.avesbot.model.Ability;
import de.avesbot.model.Attribute;
import de.avesbot.model.Group;
import de.avesbot.model.GuildSetting;
import de.avesbot.model.Tradition;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Ruleset;
import de.avesbot.model.Special;
import de.avesbot.model.SymbolDice;
import de.avesbot.model.Trial;
import de.avesbot.model.Vantage;

/**
 * This class manages alle statements being exceuted by Aves.
 * You should only use the prepared statements of this class wo work with the db.
 * @author Nitrout
 */
public class StatementManager {
	
	private static final String CHAR_INSERT = "INSERT INTO `character` SET `user_id` = ?, `name` = ?, `cou` = ?, `sgc` = ?, `int` = ?, `cha` = ?, `dex` = ?, `agi` = ?, `con` = ?, `str` = ?, ruleset = ? ON DUPLICATE KEY UPDATE `cou` = ?, `sgc` = ?, `int` = ?, `cha` = ?, `dex` = ?, `agi` = ?, `con` = ?, `str` = ?, ruleset = ?";
	private static final String CHAR_DELETE = "DELETE FROM `character` WHERE `character_id` = ? LIMIT 1";
	private static final String CHAR_ACTIVE_SELECT = "SELECT * FROM `character` WHERE `user_id` = ? AND `active` = 'y' LIMIT 1";
	private static final String CHAR_NAME_SELECT = "SELECT * FROM `character` WHERE `user_id` = ? AND `name` LIKE ? LIMIT 1";
	private static final String CHAR_LIST_SELECT = "SELECT * FROM `character` WHERE `user_id` = ?";
	private static final String CHAR_ENABLE = "UPDATE `character` SET `active` = 'y' WHERE `user_id` = ? AND `name` LIKE ? LIMIT 1";
	private static final String CHAR_DISABLE = "UPDATE `character` SET `active` = 'n' WHERE `user_id` = ?";
	private static final String CHAR_VANTAGES_SELECT = "SELECT * FROM `vantage` WHERE `character_id` = ?";
	private static final String VANTAGE_INSERT = "INSERT INTO `vantage` SET `character_id` = ?, `name` = ?, `attribute1` = ?, `attribute2` = ? ON DUPLICATE KEY UPDATE `attribute1` = ?, `attribute2` = ?";
	private static final String CHAR_SPECIAL_SELECT = "SELECT * FROM `special` WHERE `character_id` = ?";
	private static final String SPECIAL_INSERT = "INSERT INTO `special` SET `character_id` = ?, `name` = ?, `attribute1` = ?, `attribute2` = ?, `attribute3` = ? ON DUPLICATE KEY UPDATE `attribute1` = ?, `attribute2` = ?, `attribute3` = ?";
	private static final String ABILITY_INSERT = "INSERT INTO `ability` SET `character_id` = ?, `ability_name` = ?, `rep` = ?, `trial1` = ?, `trial2` = ?, `trial3` = ?, `sr` = ?, type = ? ON DUPLICATE KEY UPDATE `trial1` = ?, `trial2` = ?, `trial3` = ?, `sr` = ?, type = ?";
	private static final String ABILITY_SELECT = "SELECT * FROM `ability` WHERE `character_id` = ? AND `ability_name` LIKE ? AND (`rep` = ? OR '"+Tradition.NONE.name().toLowerCase()+"' = ?) LIMIT 1";
	private static final String CHAR_ABILITY_LIST_SELECT = "SELECT DISTINCT ability_name FROM `ability` WHERE `character_id` = ?";
	private static final String GROUP_ABILITY_LIST_SELECT = "SELECT DISTINCT ability_name FROM `ability` JOIN `group_member` USING(`character_id`) WHERE `group_id` = ?";
	private static final String GUILD_INSERT = "INSERT INTO `guild` SET `guild_id` = ?, `name` = ?, `description` = ?, `icon` = ? ON DUPLICATE KEY UPDATE `name` = ?, `description` = ?, `icon` = ?";
	private static final String GUILD_SETTING_SELECT = "SELECT * FROM `guild` WHERE `guild_id` = ? LIMIT 1";
	private static final String GUILD_SETTING_UPDATE = "UPDATE `guild` SET `locale` = ?, `hide_stats` = ? WHERE `guild_id` = ?";
	private static final String SYMBOL_DICE_INSERT = "INSERT INTO `symbol_dice` SET `guild_id` = ?, `name` = ?, `values` = ? ON DUPLICATE KEY UPDATE `values` = ?";
	private static final String SYMBOL_DICE_SELECT = "SELECT * FROM `symbol_dice` WHERE `name` LIKE ? AND (`guild_id` IS NULL OR `guild_id` = ?) LIMIT 1";
	private static final String GROUP_INSERT = "INSERT INTO `group` SET `user_id` = ?, `guild_id` = ?, `name` = ?";
	private static final String GROUP_ACTIVE_SELECT = "SELECT * FROM `group` WHERE `user_id` = ? AND `guild_id` = ? AND `active` = 'y' LIMIT 1";
	private static final String GROUP_NAME_SELECT = "SELECT * FROM `group` WHERE `guild_id` = ? AND `name` LIKE ? LIMIT 1";
	private static final String GROUP_ENABLE = "UPDATE `group` SET `active` = 'y' WHERE `user_id` = ? AND `guild_id` = ? AND `name` LIKE ? LIMIT 1";
	private static final String GROUP_DISABLE = "UPDATE `group` SET `active` = 'n' WHERE `user_id` = ? AND `guild_id` = ?";
	private static final String GROUP_MEMBER_INSERT = "INSERT INTO `group_member` SET `group_id` = ?, `character_id` = ?";
	private static final String GROUP_MEMBER_DELETE = "DELETE FROM `group_member` WHERE `group_id` = ? AND `character_id` = ?";
	private static final String GROUP_MEMBER_LIST_SELECT = "SELECT `character`.* FROM `character` JOIN `group_member` USING(`character_id`) WHERE `group_id` = ?";
	
	/**
	 * Prepares alle available Statements
	 * @throws SQLException 
	 */
	public StatementManager()  throws SQLException {
		
	}
	
	/**
	 * Adds a new character to the database for the given user or updates an existing one with the same name.
	 * 
	 * @param memberId is of the user
	 * @param chara the character information
	 * @return the id of the inserted/updated character
	 */
	public Optional<String> insertRoleplayCharacter(String memberId, RoleplayCharacter chara) {
		
		Optional<String> insertId = Optional.empty();
		
		try(PreparedStatement charInsStmnt = Database.prepareStatement(CHAR_INSERT); PreparedStatement charNameSelectStmnt = Database.prepareStatement(CHAR_NAME_SELECT);) {
			charInsStmnt.setString(1, memberId);
			charInsStmnt.setString(2, chara.name());
			for(int i = 0; i < Attribute.values().length; i++) {
				charInsStmnt.setByte(i+3, chara.getAttribute(Attribute.values()[i]));
				charInsStmnt.setByte(i+12,  chara.getAttribute(Attribute.values()[i]));
			}
			charInsStmnt.setString(11, chara.ruleset().name().toLowerCase());
			charInsStmnt.setString(20, chara.ruleset().name().toLowerCase());
			charInsStmnt.executeUpdate();
			ResultSet insertResult = charInsStmnt.getGeneratedKeys();
			if(insertResult.next()) {
				insertId = Optional.ofNullable(insertResult.getString(1));
			}
			else {
				charNameSelectStmnt.setString(1, memberId);
				charNameSelectStmnt.setString(2, chara.name());
				ResultSet updateResult = charNameSelectStmnt.executeQuery();
				if(updateResult.next()) {
					insertId = Optional.ofNullable(updateResult.getString("character_id"));
				}
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return insertId;
	}
	
	/**
	 * Deletes the given character from the db.
	 * 
	 * @param chara the character who/which will be deleted
	 * @return true if the deletion was successful
	 */
	public boolean deleteRoleplayCharacter(RoleplayCharacter chara) {
		
		int updatedRows = 0;
		
		try(PreparedStatement charDelStmnt = Database.prepareStatement(CHAR_DELETE)) {
			
			charDelStmnt.setString(1, chara.id());
			updatedRows = charDelStmnt.executeUpdate();
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return updatedRows > 0;
	}
	
	/**
	 * Adds or updates an ability for the given character.
	 * 
	 * @param chara the characer the ability will be added for
	 * @param ability the ability which will be added
	 */
	public void insertAbility(RoleplayCharacter chara, Ability ability) {
		
		try(PreparedStatement abilityInsStmnt = Database.prepareStatement(ABILITY_INSERT);) {
			abilityInsStmnt.setString(1, chara.id());
			abilityInsStmnt.setString(2, ability.name());
			abilityInsStmnt.setString(3, ability.rep().name());
			abilityInsStmnt.setString(4, ability.trial().attribute1().getAbbrevation());
			abilityInsStmnt.setString(5, ability.trial().attribute2().getAbbrevation());
			abilityInsStmnt.setString(6, ability.trial().attribute3().getAbbrevation());
			abilityInsStmnt.setByte(7, ability.taw());
			abilityInsStmnt.setString(8, ability.type().toString().toLowerCase());
			abilityInsStmnt.setString(9, ability.trial().attribute1().getAbbrevation());
			abilityInsStmnt.setString(10, ability.trial().attribute2().getAbbrevation());
			abilityInsStmnt.setString(11, ability.trial().attribute3().getAbbrevation());
			abilityInsStmnt.setByte(12, ability.taw());
			abilityInsStmnt.setString(13, ability.type().toString().toLowerCase());
			abilityInsStmnt.execute();
		} catch(SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	/**
	 * Adds or updates a (dis)advantage for the given character.
	 * 
	 * @param chara the characer the vantage will be added for
	 * @param vantage the vantage which will be added or updated
	 */
	public void insertVantage(RoleplayCharacter chara, Vantage vantage) {
		
		try(PreparedStatement vantageInsStmnt = Database.prepareStatement(VANTAGE_INSERT);) {
			vantageInsStmnt.setString(1, chara.id());
			vantageInsStmnt.setString(2, vantage.name());
			vantageInsStmnt.setString(3, vantage.attribute1().orElse(""));
			vantageInsStmnt.setString(4, vantage.attribute2().orElse(""));
			vantageInsStmnt.setString(5, vantage.attribute1().orElse(""));
			vantageInsStmnt.setString(6, vantage.attribute2().orElse(""));
			vantageInsStmnt.execute();
		} catch(SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	/**
	 * Adds or updates an ability for the given character.
	 * 
	 * @param chara the characer the ability will be added for
	 * @param special the vantage which will be added
	 */
	public void insertSpecial(RoleplayCharacter chara, Special special) {
		
		try(PreparedStatement specialInsStmnt = Database.prepareStatement(SPECIAL_INSERT);) {
			specialInsStmnt.setString(1, chara.id());
			specialInsStmnt.setString(2, special.name());
			specialInsStmnt.setString(3, special.attribute1().orElse(""));
			specialInsStmnt.setString(4, special.attribute2().orElse(""));
			specialInsStmnt.setString(5, special.attribute3().orElse(""));
			specialInsStmnt.setString(6, special.attribute1().orElse(""));
			specialInsStmnt.setString(7, special.attribute2().orElse(""));
			specialInsStmnt.setString(8, special.attribute3().orElse(""));
			specialInsStmnt.execute();
		} catch(SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	/**
	 * Get the active character of an user.
	 * 
	 * @param user the user of the active character
	 * @return the active character or empty
	 */
	public Optional<RoleplayCharacter> getUsersActiveRolePlayCharacter(Member user) {
		
		Optional<RoleplayCharacter> result = Optional.empty();
		
		try(PreparedStatement charSelectStmnt = Database.prepareStatement(CHAR_ACTIVE_SELECT);) {
			charSelectStmnt.setString(1, user.getId());
			ResultSet charaResult = charSelectStmnt.executeQuery();
			
			if(charaResult.next()) {
				
				Ruleset ruleset = Ruleset.valueOf(charaResult.getString("ruleset").toUpperCase());
				
				Vantage[] vantages = this.getRoleplayCharacterVantages(charaResult.getString("character_id"));
				Special[] specials = this.getRoleplayCharacterSpecials(charaResult.getString("character_id"));
				
				result = Optional.of(new RoleplayCharacter(	charaResult.getString("character_id"), charaResult.getString("name"), ruleset,
															charaResult.getByte("cou"), charaResult.getByte("sgc"), charaResult.getByte("int"), charaResult.getByte("cha"),
															charaResult.getByte("dex"), charaResult.getByte("agi"), charaResult.getByte("con"), charaResult.getByte("str"),
															vantages, specials));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Get a character of an user by its name.
	 * 
	 * @param user the user of the character
	 * @param name the name of the character
	 * @return the found character or empty if no matching character is found
	 */
	public Optional<RoleplayCharacter> getRolePlayCharacterByName(Member user, String name){
		
		Optional<RoleplayCharacter> result = Optional.empty();
		
		try(PreparedStatement charNameSelectStmnt = Database.prepareStatement(CHAR_NAME_SELECT);) {
			
			charNameSelectStmnt.setString(1, user.getId());
			charNameSelectStmnt.setString(2, name+"%");
			ResultSet charaResult = charNameSelectStmnt.executeQuery();
			
			if(charaResult.next()) {
				
				Ruleset ruleset = Ruleset.valueOf(charaResult.getString("ruleset").toUpperCase());
				
				Vantage[] vantages = this.getRoleplayCharacterVantages(charaResult.getString("character_id"));
				Special[] specials = this.getRoleplayCharacterSpecials(charaResult.getString("character_id"));
				
				result = Optional.of(new RoleplayCharacter(	charaResult.getString("character_id"), charaResult.getString("name"), ruleset,
															charaResult.getByte("cou"), charaResult.getByte("sgc"), charaResult.getByte("int"), charaResult.getByte("cha"),
															charaResult.getByte("dex"), charaResult.getByte("agi"), charaResult.getByte("con"), charaResult.getByte("str"),
															vantages, specials));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Get a list of all characters of an user.
	 * 
	 * @param user the owning user of the characters
	 * @return the list of characters
	 */
	public RoleplayCharacter[] getRolePlayCharacterList(Member user){
		
		LinkedList<RoleplayCharacter> result = new LinkedList<>();
		
		try(PreparedStatement charListSelectStmnt = Database.prepareStatement(CHAR_LIST_SELECT);) {
			charListSelectStmnt.setString(1, user.getId());
			ResultSet charaResult = charListSelectStmnt.executeQuery();
			
			while(charaResult.next()) {
				
				Ruleset ruleset = Ruleset.valueOf(charaResult.getString("ruleset").toUpperCase());
				
				Vantage[] vantages = this.getRoleplayCharacterVantages(charaResult.getString("character_id"));
				Special[] specials = this.getRoleplayCharacterSpecials(charaResult.getString("character_id"));
				
				result.add(new RoleplayCharacter(	charaResult.getString("character_id"), charaResult.getString("name"), ruleset,
													charaResult.getByte("cou"), charaResult.getByte("sgc"), charaResult.getByte("int"), charaResult.getByte("cha"),
													charaResult.getByte("dex"), charaResult.getByte("agi"), charaResult.getByte("con"), charaResult.getByte("str"),
													vantages, specials));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return result.toArray(RoleplayCharacter[]::new);
	}
	
	/**
	 * Get the (dis)advantages of a character.
	 * @param characterId the character id
	 * @return the list of (dis)advantages
	 */
	private Vantage[] getRoleplayCharacterVantages(String characterId) {
		
		LinkedList<Vantage> vantageList = new LinkedList<>();
		try(PreparedStatement charVantageSelectStmnt = Database.prepareStatement(CHAR_VANTAGES_SELECT);) {
			charVantageSelectStmnt.setString(1, characterId);
			ResultSet vantageResult = charVantageSelectStmnt.executeQuery();

			while(vantageResult.next()) {
				vantageList.add(new Vantage(vantageResult.getString("name"), vantageResult.getString("attribute1"), vantageResult.getString("attribute2")));
			}
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return vantageList.toArray(Vantage[]::new);
	}
	
	/**
	 * Get the special abilities of a character.
	 * @param characterId the character id
	 * @return the list of special abilities
	 */
	private Special[] getRoleplayCharacterSpecials(String characterId) {
		
		LinkedList<Special> specialList = new LinkedList<>();
		try(PreparedStatement charSpecialSelectStmnt = Database.prepareStatement(CHAR_SPECIAL_SELECT);) {
			charSpecialSelectStmnt.setString(1, characterId);
			ResultSet specialResult = charSpecialSelectStmnt.executeQuery();
			
			while(specialResult.next()) {
				specialList.add(new Special(specialResult.getString("name"), specialResult.getString("attribute1"), specialResult.getString("attribute2"), specialResult.getString("attribute3")));
			}
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return specialList.toArray(Special[]::new);
	}
	
	/**
	 * Activates the character with the given name of the given user.
	 * 
	 * @param user the user of the character
	 * @param name the name of the character
	 * @return true on success, false otherwise
	 */
	public boolean enableUsersRoleplayCharacter(Member user, String name) {
		
		int updatedRows = 0;
		
		try(PreparedStatement charDisableStmnt = Database.prepareStatement(CHAR_DISABLE); PreparedStatement charEnableStmnt = Database.prepareStatement(CHAR_ENABLE);) {
			
			charDisableStmnt.setString(1, user.getId());
			charDisableStmnt.executeUpdate();
			
			charEnableStmnt.setString(1, user.getId());
			charEnableStmnt.setString(2, name+"%");
			updatedRows = charEnableStmnt.executeUpdate();
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return updatedRows > 0;
	}
	
	/**
	 * Gets an ability of a character with the given name and representation.
	 * 
	 * @param chara the character with the ability
	 * @param abilityName name of the ability
	 * @param rep the representation
	 * @return the searched ability or empty if no matching ability is found
	 */
	public Optional<Ability> getCharacterAbility(RoleplayCharacter chara, String abilityName, Tradition rep) {
		
		Optional<Ability> result = Optional.empty();
		try(PreparedStatement abilitySelectStmnt = Database.prepareStatement(ABILITY_SELECT);) {
			
			abilitySelectStmnt.setString(1, chara.id());
			abilitySelectStmnt.setString(2, abilityName+"%");
			abilitySelectStmnt.setString(3, rep.name().toLowerCase());
			abilitySelectStmnt.setString(4, rep.name().toLowerCase());
			ResultSet abilityResult = abilitySelectStmnt.executeQuery();
			if(abilityResult.next()) {
				
				Trial trial = new Trial(Attribute.abbrevationValueOf(abilityResult.getString("trial1")), Attribute.abbrevationValueOf(abilityResult.getString("trial2")), Attribute.abbrevationValueOf(abilityResult.getString("trial3")));
				Ability.Type type = Ability.Type.valueOf(abilityResult.getString("type").toUpperCase());
				Tradition abilityRep = Tradition.valueOf(abilityResult.getString("rep").toUpperCase());
				result = Optional.of(new Ability(abilityResult.getString("ability_id"), abilityResult.getString("ability_name"), abilityRep, trial, abilityResult.getByte("sr"), type));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Get a list of all abilities of a character.
	 * 
	 * @param chara the character
	 * @return the list of ability names of the character
	 */
	public String[] getCharacterAbilityList(RoleplayCharacter chara) {
		
		ArrayList<String> result = new ArrayList<>();
		
		try(PreparedStatement charAbilityListSelectStmnt = Database.prepareStatement(CHAR_ABILITY_LIST_SELECT);) {
			
			charAbilityListSelectStmnt.setString(1, chara.id());
			ResultSet abilityResult = charAbilityListSelectStmnt.executeQuery();
			while(abilityResult.next()) {
				result.add(abilityResult.getString("ability_name"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return result.toArray(String[]::new);
	}
	
	/**
	 * Get a list of abilities of all characters in the specified group.
	 * 
	 * @param group the group
	 * @return a list of all available ability names
	 */
	public String[] getGroupAbilityList(Group group) {
		
		ArrayList<String> result = new ArrayList<>();
		
		try(PreparedStatement groupAbilityListSelectStmnt = Database.prepareStatement(GROUP_ABILITY_LIST_SELECT);) {
			
			groupAbilityListSelectStmnt.setString(1, group.groupId());
			ResultSet abilityResult = groupAbilityListSelectStmnt.executeQuery();
			while(abilityResult.next()) {
				result.add(abilityResult.getString("ability_name"));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return result.toArray(String[]::new);
	}
	
	/**
	 * Adds or updates the given guild to the database.
	 * 
	 * @param guild the guild being updated or added.
	 */
	public void insertGuild(Guild guild) {
		
		String guildId = guild.getId();
		String name = guild.getName();
		Optional<String> description = Optional.ofNullable(guild.getDescription());
		String iconUrl = guild.getIconUrl();
		
		try(PreparedStatement guildInsStmnt = Database.prepareStatement(GUILD_INSERT);) {
			
			guildInsStmnt.setString(1, guildId);
			guildInsStmnt.setString(2, name);
			guildInsStmnt.setString(3, description.orElse(""));
			guildInsStmnt.setString(4, iconUrl);
			guildInsStmnt.setString(5, name);
			guildInsStmnt.setString(6, description.orElse(""));
			guildInsStmnt.setString(7, iconUrl);
			
			guildInsStmnt.executeUpdate();
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	/**
	 * Get the Settings of the given guild.
	 * 
	 * @param guild
	 * @return the guild settings or empty
	 */
	public Optional<GuildSetting> getGuildSetting(Guild guild) {
		
		Optional<GuildSetting> guildSetting = Optional.empty();
		String guildId = guild.getId();
		
		try(PreparedStatement guildSelectStmnt = Database.prepareStatement(GUILD_SETTING_SELECT);) {
			
			guildSelectStmnt.setString(1, guildId);
			
			ResultSet guildResult = guildSelectStmnt.executeQuery();
			if(guildResult.next()) {
				
				guildSetting = Optional.of(new GuildSetting(guildResult.getString("guild_id"), guildResult.getString("name"), guildResult.getString("description"),
															guildResult.getString("icon"), guildResult.getString("promote").equals("y"), guildResult.getString("locale"),
															guildResult.getString("hide_stats").equals("y")));
			}
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return guildSetting;
	}
	
	/**
	 * Changes the prefix for the given guild.
	 * 
	 * @param setting the guild's settings to update
	 */
	public void updateGuildSetting(GuildSetting setting) {
		
		try(PreparedStatement guildPrefixUpdateStmnt = Database.prepareStatement(GUILD_SETTING_UPDATE);) {
			
			guildPrefixUpdateStmnt.setString(1, setting.locale().toString());
			guildPrefixUpdateStmnt.setString(2, setting.hideStats() ? "y" : "n");
			guildPrefixUpdateStmnt.setString(3, setting.id());
			
			guildPrefixUpdateStmnt.executeUpdate();
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	/**
	 * Inserts or updates a given symbol dice into the database for the guild.
	 * 
	 * @param guild the guild the dice will be added for
	 * @param dice the dice which will be added
	 */
	public void insertSymbolDice(Guild guild, SymbolDice dice) {
		
		String guildId = guild.getId();
		String values = Stream.of(dice.values()).collect(Collectors.joining(","));
		
		try(PreparedStatement symbolDiceInsStmnt = Database.prepareStatement(SYMBOL_DICE_INSERT);) {
			
			symbolDiceInsStmnt.setString(1, guildId);
			symbolDiceInsStmnt.setString(2, dice.name());
			symbolDiceInsStmnt.setString(3, values);
			symbolDiceInsStmnt.setString(4, values);
			
			symbolDiceInsStmnt.executeUpdate();
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	/**
	 * Get the gloabl available or guild specific symbol dice.
	 * 
	 * @param guild the guild of the dice
	 * @param name the name of the dice
	 * @return the found symbol dice or empty
	 */
	public Optional<SymbolDice> getSymbolDice(Guild guild, String name) {
		
		Optional<SymbolDice> dice = Optional.empty();
		String guildId = guild.getId();
		
		try(PreparedStatement symbolDiceSelectStmnt = Database.prepareStatement(SYMBOL_DICE_SELECT);) {
			
			symbolDiceSelectStmnt.setString(1, name+"%");
			symbolDiceSelectStmnt.setString(2, guildId);
			
			ResultSet diceResult = symbolDiceSelectStmnt.executeQuery();
			if(diceResult.next()) {
				
				dice = Optional.of(new SymbolDice(diceResult.getString("name"), diceResult.getString("values").split(",")));
			}
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return dice;
	}
	
	/**
	 * Adds a new group int the given guild available for the given user.
	 * 
	 * @param user the owner of the group
	 * @param guild the guild the group will be added for
	 * @param groupName the name of the group
	 */
	public void insertGroup(Member user, Guild guild, String groupName) {
		
		try(PreparedStatement groupInsStmnt = Database.prepareStatement(GROUP_INSERT);) {
			
			groupInsStmnt.setString(1, user.getId());
			groupInsStmnt.setString(2, guild.getId());
			groupInsStmnt.setString(3, groupName);
			
			groupInsStmnt.executeUpdate();
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	/**
	 * Get the active group of an user.
	 * 
	 * @param guild the guild to look for the active group
	 * @param user the user of the active group
	 * @return the active group or empty if no active group
	 */
	public Optional<Group> getUsersActiveGroup(Guild guild, Member user){
		
		Optional<Group> result = Optional.empty();
		
		try(PreparedStatement groupActiveSelectStmnt = Database.prepareStatement(GROUP_ACTIVE_SELECT);) {
			
			groupActiveSelectStmnt.setString(1, user.getId());
			groupActiveSelectStmnt.setString(2, guild.getId());
			ResultSet groupResult = groupActiveSelectStmnt.executeQuery();
			
			if(groupResult.next()) {
				
				result = Optional.of(new Group(groupResult.getString("group_id"), groupResult.getString("user_id"), groupResult.getString("guild_id"), groupResult.getString("name")));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Activates the group with given name for the given user in given guild.
	 * 
	 * @param user the user the the group will be activated
	 * @param guild the guild of the group
	 * @param name the name of the group
	 * @return true on success, false otherwise
	 */
	public boolean enableUsersGroup(Member user, Guild guild, String name) {
		
		int updatedRows = 0;
		
		try(PreparedStatement groupDisableStmnt = Database.prepareStatement(GROUP_DISABLE); PreparedStatement groupEnableStmnt = Database.prepareStatement(GROUP_ENABLE);) {
			
			groupDisableStmnt.setString(1, user.getId());
			groupDisableStmnt.setString(2, guild.getId());
			groupDisableStmnt.executeUpdate();
			
			groupEnableStmnt.setString(1, user.getId());
			groupEnableStmnt.setString(2, guild.getId());
			groupEnableStmnt.setString(3, name+"%");
			updatedRows = groupEnableStmnt.executeUpdate();
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return updatedRows > 0;
	}
	
	/**
	 * Gets a group by its name of a guild.
	 * 
	 * @param guild the guild of the group
	 * @param name the name of the group
	 * @return the group with given name or empty if not found
	 */
	public Optional<Group> getGuildGroupByName(Guild guild, String name) {
		
		Optional<Group> result = Optional.empty();
		
		try(PreparedStatement groupNameSelectStmnt = Database.prepareStatement(GROUP_NAME_SELECT);) {
			
			groupNameSelectStmnt.setString(1, guild.getId());
			groupNameSelectStmnt.setString(2, name+"%");
			ResultSet groupResult = groupNameSelectStmnt.executeQuery();
			
			if(groupResult.next()) {
				
				result = Optional.of(new Group(groupResult.getString("group_id"), groupResult.getString("user_id"), groupResult.getString("guild_id"), groupResult.getString("name")));
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Adds a character to the given group in the database.
	 * 
	 * @param group the group
	 * @param character the character
	 * @return true on success, false otherwise
	 */
	public boolean insertGroupMember(Group group, RoleplayCharacter character) {
		int updatedRows = 0;
		try(PreparedStatement groupMemberInsStmnt = Database.prepareStatement(GROUP_MEMBER_INSERT);) {
			
			groupMemberInsStmnt.setString(1, group.groupId());
			groupMemberInsStmnt.setString(2, character.id());
			updatedRows = groupMemberInsStmnt.executeUpdate();
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return updatedRows > 0;
	}
	
	/**
	 * Removes a character from the given group in the database.
	 * 
	 * @param group the group
	 * @param character the character
	 * @return true on succes, false otherwise
	 */
	public boolean deleteGroupMember(Group group, RoleplayCharacter character) {
		int updatedRows = 0;
		try(PreparedStatement groupMemberDelStmnt = Database.prepareStatement(GROUP_MEMBER_DELETE);) {
			
			groupMemberDelStmnt.setString(1, group.groupId());
			groupMemberDelStmnt.setString(2, character.id());
			updatedRows = groupMemberDelStmnt.executeUpdate();
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return updatedRows > 0;
	}
	
	/**
	 * Returns a list of all characters of the given group.
	 * 
	 * @param group the group
	 * @return list of characters
	 */
	public RoleplayCharacter[] getGroupMemberList(Group group) {
		
		LinkedList<RoleplayCharacter> result = new LinkedList<>();
		
		try(PreparedStatement groupMemberListSelectStmnt = Database.prepareStatement(GROUP_MEMBER_LIST_SELECT);) {
			
			groupMemberListSelectStmnt.setString(1, group.groupId());
			ResultSet charaResult = groupMemberListSelectStmnt.executeQuery();
			
			while(charaResult.next()) {
				
				Ruleset ruleset = Ruleset.valueOf(charaResult.getString("ruleset").toUpperCase());
				
				Vantage[] vantages = this.getRoleplayCharacterVantages(charaResult.getString("character_id"));
				Special[] specials = this.getRoleplayCharacterSpecials(charaResult.getString("character_id"));
				
				result.add(new RoleplayCharacter(	charaResult.getString("character_id"), charaResult.getString("name"), ruleset,
													charaResult.getByte("cou"), charaResult.getByte("sgc"), charaResult.getByte("int"), charaResult.getByte("cha"),
													charaResult.getByte("dex"), charaResult.getByte("agi"), charaResult.getByte("con"), charaResult.getByte("str"),
													vantages, specials));
			}
			
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
		
		return result.toArray(RoleplayCharacter[]::new);
	}
}