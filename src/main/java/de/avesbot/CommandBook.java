package de.avesbot;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import de.avesbot.callable.roll.RollAttributeCallable;
import de.avesbot.callable.character.CharacterChooseCallable;
import de.avesbot.callable.group.GroupChooseCallable;
import de.avesbot.callable.CommandCallable;
import de.avesbot.callable.DiceCallable;
import de.avesbot.callable.group.GroupAttributeCallable;
import de.avesbot.callable.group.GroupCreateCallable;
import de.avesbot.callable.group.GroupSkillCallable;
import de.avesbot.callable.character.CharacterCreateCallable;
import de.avesbot.callable.character.CharacterDeleteCallable;
import de.avesbot.callable.character.CharacterInfoCallable;
import de.avesbot.callable.group.GroupJoinCallable;
import de.avesbot.callable.group.GroupLeaveCallable;
import de.avesbot.callable.character.CharacterListCallable;
import de.avesbot.callable.character.CharacterTrainCallable;
import de.avesbot.callable.roll.RollDiceCallable;
import de.avesbot.callable.roll.RollSkillCallable;
import de.avesbot.callable.roll.RollSlipCallable;
import de.avesbot.callable.roll.RollSumCallable;
import de.avesbot.callable.roll.RollTrialCallable;
import de.avesbot.callable.settings.SettingsHideStatsCallable;
import de.avesbot.callable.settings.SettingsLanguageCallable;

/**
 * Class for managing the available commands.
 * @author Nitrout
 */
public class CommandBook {
	
	private static final CommandBook INSTANCE = new CommandBook();
	
	private final HashMap<String, Class<? extends CommandCallable>> commandMap;
	
	public static CommandBook getInstance() {
		return INSTANCE;
	}
	
	private CommandBook() {
		commandMap = new HashMap<>();
		commandMap.put("character/choose", CharacterChooseCallable.class);
		commandMap.put("character/create", CharacterCreateCallable.class);
		commandMap.put("character/delete", CharacterDeleteCallable.class);
		commandMap.put("character/info", CharacterInfoCallable.class);
		commandMap.put("character/list", CharacterListCallable.class);
		commandMap.put("character/train", CharacterTrainCallable.class);
		
		commandMap.put("dice", DiceCallable.class);
		
		commandMap.put("group/attribute", GroupAttributeCallable.class);
		commandMap.put("group/choose", GroupChooseCallable.class);
		commandMap.put("group/create", GroupCreateCallable.class);
		commandMap.put("group/join", GroupJoinCallable.class);
		commandMap.put("group/leave", GroupLeaveCallable.class);
		commandMap.put("group/skill", GroupSkillCallable.class);
		
		commandMap.put("roll/attribute", RollAttributeCallable.class);
		commandMap.put("roll/dice", RollDiceCallable.class);
		commandMap.put("roll/skill", RollSkillCallable.class);
		commandMap.put("roll/slip", RollSlipCallable.class);
		commandMap.put("roll/sum", RollSumCallable.class);
		commandMap.put("roll/trial", RollTrialCallable.class);
		
		commandMap.put("settings/hidestats", SettingsHideStatsCallable.class);
		commandMap.put("settings/language", SettingsLanguageCallable.class);
	}
	
	/**
	 * Get a new CommandCallable for the given command.
	 * @param event the event for creating the new CommandCallable
	 * @return optional with the CommandCallable or empty if there is no such command
	 */
	public Optional<CommandCallable> getCommand(SlashCommandEvent event) {
		
		String commandName = event.getCommandPath();
		
		Optional<CommandCallable> command = Optional.ofNullable(this.commandMap.get(commandName)).map(cc -> {
			CommandCallable cmd = null;
			try {
				cmd = (CommandCallable)cc.getConstructor(SlashCommandEvent.class).newInstance(event);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				System.err.println(ex.getMessage());
			}
			return cmd;
		});
		
		
		return command;
	}
	
	public Collection<Class<? extends CommandCallable>> getRegisteredCommands() {
		
		return this.commandMap.values();
	}
}