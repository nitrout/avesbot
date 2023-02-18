package de.avesbot.callable.character;

import java.util.Optional;
import de.avesbot.Avesbot;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Ruleset;
import de.avesbot.model.Special;
import de.avesbot.model.Vantage;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Callable to create a new character.
 * @author Nitrout
 */
public class CharacterCreateCallable extends CharacterCallable {
	
	static {
		
		SubcommandData createCommand = new SubcommandData(I18N.getTranslation("characterCreate"), I18N.getTranslation("characterCreateDescription"));
				
		OptionData nameOption = new OptionData(OptionType.STRING, I18N.getTranslation("characterNameOption"), I18N.getTranslation("characterCreateNameOptionDescription"), true);
		nameOption.setNameLocalizations(I18N.getLocalizations("characterNameOption"));
		nameOption.setDescriptionLocalizations(I18N.getLocalizations("characterCreateNameOptionDescription"));
		
		OptionData rulesetOption = new OptionData(OptionType.STRING, I18N.getTranslation("characterRulesetOption"), I18N.getTranslation("characterCreateRulesetOptionDescription"), true);
		rulesetOption.setNameLocalizations(I18N.getLocalizations("characterRulesetOption"));
		rulesetOption.setDescriptionLocalizations(I18N.getLocalizations("characterCreateRulesetOptionDescription"));
		Choice tde4Choice = new Choice(I18N.getTranslation("characterRulesetOptionChoiceTde4"), "TDE4");
		tde4Choice.setNameLocalizations(I18N.getLocalizations("characterRulesetOptionChoiceTde4"));
		Choice tde5Choice = new Choice(I18N.getTranslation("characterRulesetOptionChoiceTde5"), "TDE5");
		tde5Choice.setNameLocalizations(I18N.getLocalizations("characterRulesetOptionChoiceTde5"));
		rulesetOption.addChoices(tde4Choice, tde5Choice);
		
		OptionData couOption = new OptionData(OptionType.INTEGER, I18N.getTranslation("characterCouOption"), I18N.getTranslation("characterCreateCouOptionDescription"), true);
		couOption.setNameLocalizations(I18N.getLocalizations("characterCouOption"));
		couOption.setDescriptionLocalizations(I18N.getLocalizations("characterCreateCouOptionDescription"));
				
		OptionData sgcOption = new OptionData(OptionType.INTEGER, I18N.getTranslation("characterSgcOption"), I18N.getTranslation("characterCreateSgcOptionDescription"), true);
		sgcOption.setNameLocalizations(I18N.getLocalizations("characterSgcOption"));
		sgcOption.setDescriptionLocalizations(I18N.getLocalizations("characterCreateSgcOptionDescription"));
				
		OptionData intOption = new OptionData(OptionType.INTEGER, I18N.getTranslation("characterIntOption"), I18N.getTranslation("characterCreateIntOptionDescription"), true);
		intOption.setNameLocalizations(I18N.getLocalizations("characterIntOption"));
		intOption.setDescriptionLocalizations(I18N.getLocalizations("characterCreateIntOptionDescription"));
				
		OptionData chaOption = new OptionData(OptionType.INTEGER, I18N.getTranslation("characterChaOption"), I18N.getTranslation("characterCreateChaOptionDescription"), true);
		chaOption.setNameLocalizations(I18N.getLocalizations("characterChaOption"));
		chaOption.setDescriptionLocalizations(I18N.getLocalizations("characterCreateChaOptionDescription"));
				
		OptionData dexOption = new OptionData(OptionType.INTEGER, I18N.getTranslation("characterDexOption"), I18N.getTranslation("characterCreateDexOptionDescription"), true);
		dexOption.setNameLocalizations(I18N.getLocalizations("characterDexOption"));
		dexOption.setDescriptionLocalizations(I18N.getLocalizations("characterCreateDexOptionDescription"));
				
		OptionData agiOption = new OptionData(OptionType.INTEGER, I18N.getTranslation("characterAgiOption"), I18N.getTranslation("characterCreateAgiOptionDescription"), true);
		agiOption.setNameLocalizations(I18N.getLocalizations("characterAgiOption"));
		agiOption.setDescriptionLocalizations(I18N.getLocalizations("characterCreateAgiOptionDescription"));
				
		OptionData conOption = new OptionData(OptionType.INTEGER, I18N.getTranslation("characterConOption"), I18N.getTranslation("characterCreateConOptionDescription"), true);
		conOption.setNameLocalizations(I18N.getLocalizations("characterConOption"));
		conOption.setDescriptionLocalizations(I18N.getLocalizations("characterCreateConOptionDescription"));
				
		OptionData strOption = new OptionData(OptionType.INTEGER, I18N.getTranslation("characterStrOption"), I18N.getTranslation("characterCreateStrOptionDescription"), true);
		strOption.setNameLocalizations(I18N.getLocalizations("characterStrOption"));
		strOption.setDescriptionLocalizations(I18N.getLocalizations("characterCreateStrOptionDescription"));
				
		createCommand.addOptions(
			nameOption, rulesetOption,
			couOption, sgcOption, intOption, chaOption,
			dexOption, agiOption, conOption, strOption
		);
		COMMAND.addSubcommands(createCommand);
	}
	
	/**
	 * Creates a new character creation callable.
	 * @param event
	 */
	public CharacterCreateCallable(SlashCommandInteractionEvent event) {
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
		byte courage = (byte)this.commandPars.get("COU").getAsLong();
		byte sagacity = (byte)this.commandPars.get("SGC").getAsLong();
		byte intuition = (byte)this.commandPars.get("INT").getAsLong();
		byte charisma = (byte)this.commandPars.get("CHA").getAsLong();
		byte dexterity = (byte)this.commandPars.get("DEX").getAsLong();
		byte agility = (byte)this.commandPars.get("AGI").getAsLong();
		byte constitution = (byte)this.commandPars.get("CON").getAsLong();
		byte strength = (byte)this.commandPars.get("STR").getAsLong();
		
		RoleplayCharacter chara = new RoleplayCharacter(name, ruleset, courage, sagacity, intuition, charisma, dexterity, agility, constitution, strength, new Vantage[]{}, new Special[]{});
		Optional<String> charaId = Avesbot.getStatementManager().insertRoleplayCharacter(member.getId(), chara);
		
		if(charaId.isPresent())
			return I18N.format(settings.locale(), "characterCreated", chara);
		else
			return I18N.getTranslation(settings.locale(), "errorCharacterCreation");
	}
}