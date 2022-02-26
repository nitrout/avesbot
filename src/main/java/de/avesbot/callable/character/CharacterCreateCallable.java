package de.avesbot.callable.character;

import java.util.Optional;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import de.avesbot.Avesbot;
import de.avesbot.callable.CommandCallable;
import de.avesbot.i18n.I18n;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Ruleset;
import de.avesbot.model.Special;
import de.avesbot.model.Vantage;

/**
 * Callable to create a new character.
 * @author Nitrout
 */
public class CharacterCreateCallable extends CommandCallable {
	
	public static final String MANUAL = "**!char[acter]** Name (DSA4|DSA5) (true|false) (none|wild|solid) MU KL IN CH FF GE KO KK\t- Erstellt einen Charakter mit Name und den Vor-/Nachtteilen Tollpatsch(true/false), Wilde Magie oder Feste Matrix, sowie den angegebenen Werten";
	@Deprecated
	private static final Pattern CHARACTER_PATTERN = Pattern.compile("(.*?) (DSA4|DSA5) (true|false) (none|wild|solid) (\\d{1,2}) (\\d{1,2}) (\\d{1,2}) (\\d{1,2}) (\\d{1,2}) (\\d{1,2}) (\\d{1,2}) (\\d{1,2})");
	
	/**
	 * Creates a new character creation callable.
	 * @param event
	 */
	public CharacterCreateCallable(MessageReceivedEvent event) {
		super(event);
	}
	
	/**
	 * Execution of the character creation.
	 * @return the creation result
	 * @throws Exception 
	 */
	@Override
	public String call() throws Exception {
		
		String name = this.commandPars.get("name").getAsString();
		Ruleset ruleset = Ruleset.valueOf(this.commandPars.get("ruleset").getAsString());
		byte courage = (byte)this.commandPars.get("courage").getAsLong();
		byte sagacity = (byte)this.commandPars.get("sagacity").getAsLong();
		byte intuition = (byte)this.commandPars.get("intuition").getAsLong();
		byte charisma = (byte)this.commandPars.get("charisma").getAsLong();
		byte dexterity = (byte)this.commandPars.get("dexterity").getAsLong();
		byte agility = (byte)this.commandPars.get("agility").getAsLong();
		byte constitution = (byte)this.commandPars.get("constitution").getAsLong();
		byte strength = (byte)this.commandPars.get("strength").getAsLong();
		
		RoleplayCharacter chara = new RoleplayCharacter(name, ruleset, courage, sagacity, intuition, charisma, dexterity, agility, constitution, strength, new Vantage[]{}, new Special[]{});
		Optional<String> charaId = Avesbot.getStatementManager().insertRoleplayCharacter(member.getId(), chara);
		
		if(charaId.isPresent())
			return I18n.getInstance().format(settings.getLocale(), "characterCreated", chara);
		else
			return I18n.getInstance().getString(settings.getLocale(), "errorCharacterCreation");
	}
}