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
import de.avesbot.callable.character.CharacterImportCallable;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

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
	
	public List<CommandData> getAvailableSlashCommands() {
		
		return getCommandList().stream()
				//.map(c -> c.getSuperclass())
				.map(c -> {
					try {
						return (CommandData)(c.getField("COMMAND").get(null));
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
						Logger.getLogger(CommandBook.class.getName()).log(Level.SEVERE, null, ex);
					}
					return null;
				})
				.distinct()
				.toList();
	}
	
	private void registerSubcommands() {
		
			getCommandList().forEach(c -> {
			try {
				SlashCommandData cmd = (SlashCommandData)c.getField("COMMAND").get(null);
				SubcommandData subCmd = (SubcommandData)c.getField("SUBCOMMAND").get(null);
				cmd.addSubcommands(subCmd);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
				Logger.getLogger(CommandBook.class.getName()).log(Level.SEVERE, null, ex);
			}
		} );
	}
	
	List<Class<? extends CommandCallable>> getCommandList() {
		return List.of(
				CharacterChooseCallable.class, CharacterCreateCallable.class, CharacterDeleteCallable.class, CharacterImportCallable.class, CharacterInfoCallable.class, CharacterLearnCallable.class, CharacterListCallable.class,
				GroupAttributeCallable.class, GroupChooseCallable.class, GroupCreateCallable.class, GroupJoinCallable.class, GroupLeaveCallable.class, GroupSkillCallable.class,
				RollAttributeCallable.class, RollDiceCallable.class, RollSkillCallable.class, RollSlipCallable.class, RollSumCallable.class, RollTrialCallable.class,
				SettingsLanguageCallable.class, SettingsStatsCallable.class,
				DiceCallable.class
		);
	}
	
	private CommandBook() {
		commandMap = new HashMap<>();
		
		List<Command> commands = Avesbot.getJda().retrieveCommands().complete();
		
		commands.forEach(com -> {
			List<Command.Subcommand> subs = com.getSubcommands();
			if(subs.isEmpty()) {
				try {
					Class c = Class.forName(getCommandCallableClassName(com.getName()));
					commandMap.put(com.getName(), c);
				} catch (ClassNotFoundException ex) {
					Logger.getLogger(CommandBook.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else {
				subs.forEach(sub -> {
					try {
						Class c = Class.forName(getCommandCallableClassName(com.getName(), sub.getName()));
						commandMap.put(com.getName()+" "+sub.getName(), c);
					} catch (ClassNotFoundException ex) {
						Logger.getLogger(CommandBook.class.getName()).log(Level.SEVERE, null, ex);
					}
				});
			}
		});
		
		registerSubcommands();
	}
	
	/**
	 * Get a new CommandCallable for the given command.
	 * @param event the event for creating the new CommandCallable
	 * @return optional with the CommandCallable or empty if there is no such command
	 */
	public Optional<CommandCallable> getCommand(SlashCommandInteractionEvent event) {
		
		String commandName = event.getFullCommandName();
		
		return Optional.ofNullable(this.commandMap.get(commandName)).map(cc -> {
			CommandCallable cmd = null;
			try {
				cmd = (CommandCallable)cc.getConstructor(SlashCommandInteractionEvent.class).newInstance(event);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				Logger.getLogger(CommandBook.class.getName()).log(Level.SEVERE, null, ex);
			}
			return cmd;
		});
	}
	
	public Collection<Class<? extends CommandCallable>> getRegisteredCommands() {
		
		return this.commandMap.values();
	}
	
	private String getCommandCallableClassName(String...commands) {
		StringBuilder sb = new StringBuilder();
		sb.append("de.avesbot.callable.");
		if(commands.length > 1) {
			sb.append(commands[0]);
			sb.append(".");
		}
		for(String comPart : commands) {
			sb.append(Character.toUpperCase(comPart.charAt(0)));
			sb.append(comPart.substring(1));
		}
		sb.append("Callable");
		
		return sb.toString();
	}
}