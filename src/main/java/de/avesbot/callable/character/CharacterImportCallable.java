package de.avesbot.callable.character;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.avesbot.model.optolith.character.OptolithCharacter;
import de.avesbot.util.HeldenSoftwareImport;
import de.avesbot.util.OptolithImport;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Imports a character from an xml file.
 * Works currently only for DSA4 ruleset.
 * @author Nitrout
 */
public class CharacterImportCallable extends CharacterCallable {
	
	public static final int TIMEOUT = 15;
	
	public static final SubcommandData SUBCOMMAND = buildTranslatedSubcommand(I18N, "characterImport", "characterImportDescription");

	private static final DocumentBuilder DOCUMENT_BUILDER = getDocumentBuilder();
	private static final ObjectMapper JSON_MAPPER = getObjectMapper();
	
	static {
		OptionData fileOption = buildTranslatedOption(I18N, OptionType.ATTACHMENT, "characterFileOption", "characterFileOptionDescription", true);
		SUBCOMMAND.addOptions(fileOption);
	}

	private static DocumentBuilder getDocumentBuilder() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		try {
			return dbf.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(CharacterImportCallable.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	private static ObjectMapper getObjectMapper() {
		var om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return om;
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
		
		this.getHeroImporter().ifPresent(Runnable::run);
		
		return String.format("Keine XML-Datei! Bitte lade eine XML-Datei hoch!");
	}
	
	private Optional<Runnable> getHeroImporter() {
		Attachment attachment = this.commandPars.get("file").getAsAttachment();
		var memberId = member.getUser().getId();
		
		if("XML".equalsIgnoreCase(attachment.getFileExtension())) {
			return getXmlFile(attachment).map(doc -> new HeldenSoftwareImport(doc, memberId));
		} else if ("JSON".equalsIgnoreCase(attachment.getFileExtension())) {
			return getJsonFile(attachment).map(json -> new OptolithImport(json, memberId));
		}
		
		return Optional.empty();
	}
	
	private static Optional<Document> getXmlFile(Attachment attachment) {

		try (var is = attachment.getProxy().download().get()) {
			return Optional.of(DOCUMENT_BUILDER.parse(is));
		} catch (InterruptedException | ExecutionException | IOException | SAXException ex) {
			Logger.getLogger(CharacterImportCallable.class.getName()).log(Level.SEVERE, null, ex);
		}
		return Optional.empty();
	}

	private static Optional<OptolithCharacter> getJsonFile(Attachment attachment) {
		try {
			return Optional.of(JSON_MAPPER.readValue(attachment.getProxy().download().get(), OptolithCharacter.class));
		} catch (InterruptedException | ExecutionException | IOException ex) {
			Logger.getLogger(CharacterImportCallable.class.getName()).log(Level.SEVERE, null, ex);
		}
		return Optional.empty();
	}

	@Override
	public int getTimeout() {
		return TIMEOUT;
	}
}