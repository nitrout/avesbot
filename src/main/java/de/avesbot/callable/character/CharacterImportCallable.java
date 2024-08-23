package de.avesbot.callable.character;

import de.avesbot.util.HeldenSoftwareImport;
import de.avesbot.util.OptolithImport;
import java.util.Optional;
import java.util.function.BiFunction;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Imports a character from an xml file.
 * Works currently only for DSA4 ruleset.
 * @author Nitrout
 */
public class CharacterImportCallable extends CharacterCallable {
	
	public static final int EXTENDED_TIMEOUT = 15;
	
	public static final SubcommandData SUBCOMMAND = buildTranslatedSubcommand(I18N, "characterImport", "characterImportDescription");
	
	static {
		OptionData fileOption = buildTranslatedOption(I18N, OptionType.ATTACHMENT, "characterFileOption", "characterFileOptionDescription", true);
		SUBCOMMAND.addOptions(fileOption);
	}


	
	/**
	 * Creates a new ImportCallable.
	 * @param event 
	 */
	public CharacterImportCallable(SlashCommandInteractionEvent event) {
		super(event);
	}
	
	@Override
	public String call() throws Exception {

		var attachment = commandPars.get("file").getAsAttachment();
		var memberId = member.getUser().getId();
		
		return this.getHeroImporter(attachment)
				.map(func -> func.apply(attachment, memberId))
				.map(result -> result ? "SUCCESS" : "FAILED")
				.orElse(String.format("Keine XML-Datei! Bitte lade eine XML-Datei hoch!"));
	}
	
	private Optional<? extends BiFunction<Attachment, String, Boolean>> getHeroImporter(Attachment attachment) {
		if("XML".equalsIgnoreCase(attachment.getFileExtension())) {
			return Optional.of(new HeldenSoftwareImport());
		} else if ("JSON".equalsIgnoreCase(attachment.getFileExtension())) {
			return Optional.of(new OptolithImport());
		}
		
		return Optional.empty();
	}

	@Override
	public int getTimeout() {
		return EXTENDED_TIMEOUT;
	}
}