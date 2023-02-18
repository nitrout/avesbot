package de.avesbot;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import de.avesbot.callable.CommandCallable;
import de.avesbot.callable.DiceCallable;
import de.avesbot.callable.character.CharacterChooseCallable;
import de.avesbot.callable.character.CharacterCreateCallable;
import de.avesbot.callable.character.CharacterDeleteCallable;
import de.avesbot.callable.character.CharacterInfoCallable;
import de.avesbot.callable.character.CharacterLearnCallable;
import de.avesbot.callable.character.CharacterListCallable;
import de.avesbot.callable.group.GroupAttributeCallable;
import de.avesbot.callable.group.GroupChooseCallable;
import de.avesbot.callable.group.GroupCreateCallable;
import de.avesbot.callable.group.GroupJoinCallable;
import de.avesbot.callable.group.GroupLeaveCallable;
import de.avesbot.callable.group.GroupSkillCallable;
import de.avesbot.callable.roll.RollAttributeCallable;
import de.avesbot.callable.roll.RollDiceCallable;
import de.avesbot.callable.roll.RollSkillCallable;
import de.avesbot.callable.roll.RollSlipCallable;
import de.avesbot.callable.roll.RollSumCallable;
import de.avesbot.callable.roll.RollTrialCallable;
import de.avesbot.callable.settings.SettingsLanguageCallable;
import de.avesbot.callable.settings.SettingsStatsCallable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

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
	
	public static Set<CommandData> getAvailableSlashCommands() {
		
		HashSet<CommandData> commandSet = new HashSet<>();
		
		commandSet.add(CharacterChooseCallable.COMMAND);
		commandSet.add(CharacterCreateCallable.COMMAND);
		commandSet.add(CharacterDeleteCallable.COMMAND);
		commandSet.add(CharacterInfoCallable.COMMAND);
		commandSet.add(CharacterLearnCallable.COMMAND);
		commandSet.add(CharacterListCallable.COMMAND);
		
		commandSet.add(GroupAttributeCallable.COMMAND);
		commandSet.add(GroupChooseCallable.COMMAND);
		commandSet.add(GroupCreateCallable.COMMAND);
		commandSet.add(GroupJoinCallable.COMMAND);
		commandSet.add(GroupLeaveCallable.COMMAND);
		commandSet.add(GroupSkillCallable.COMMAND);
		
		commandSet.add(RollAttributeCallable.COMMAND);
		commandSet.add(RollDiceCallable.COMMAND);
		commandSet.add(RollSkillCallable.COMMAND);
		commandSet.add(RollSlipCallable.COMMAND);
		commandSet.add(RollSumCallable.COMMAND);
		commandSet.add(RollTrialCallable.COMMAND);
		
		commandSet.add(SettingsLanguageCallable.COMMAND);
		commandSet.add(SettingsStatsCallable.COMMAND);
		
		commandSet.add(DiceCallable.COMMAND);
		
		return commandSet;
		
	}
	
	private CommandBook() {
		commandMap = new HashMap<>();
		
		List<Command> commands = Avesbot.getJda().retrieveCommands().complete();
		
		commands.forEach(com -> {
			List<Command.Subcommand> subs = com.getSubcommands();
			if(subs.isEmpty()) {
				try {
					Class c = Avesbot.class.getClassLoader().loadClass(getCommandCallableClassName(com.getName()));
					commandMap.put(com.getName(), c);
				} catch (ClassNotFoundException ex) {
					System.err.println(ex.getMessage());
				}
			} else {
				subs.forEach(sub -> {
					try {
						Class c = Avesbot.class.getClassLoader().loadClass(getCommandCallableClassName(com.getName(), sub.getName()));
						commandMap.put(com.getName()+"/"+sub.getName(), c);
					} catch (ClassNotFoundException ex) {
						System.err.println(ex.getMessage());
					}
				});
			}
		});
	}
	
	/**
	 * Get a new CommandCallable for the given command.
	 * @param event the event for creating the new CommandCallable
	 * @return optional with the CommandCallable or empty if there is no such command
	 */
	public Optional<CommandCallable> getCommand(SlashCommandInteractionEvent event) {
		
		String commandName = event.getFullCommandName();
		
		Optional<CommandCallable> command = Optional.ofNullable(this.commandMap.get(commandName)).map(cc -> {
			CommandCallable cmd = null;
			try {
				cmd = (CommandCallable)cc.getConstructor(SlashCommandInteractionEvent.class).newInstance(event);
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
	
	private String getCommandCallableClassName(String...commands) {
		StringBuilder sb = new StringBuilder();
		for(String comPart : commands) {
			sb.append(comPart.charAt(0));
			sb.append(comPart.substring(1));
		}
		sb.append("Callable");
		
		return sb.toString();
	}
}