package de.avesbot.util;

import de.avesbot.Avesbot;
import de.avesbot.model.Ability;
import de.avesbot.model.Attribute;
import de.avesbot.model.RoleplayCharacter;
import de.avesbot.model.Ruleset;
import de.avesbot.model.Special;
import de.avesbot.model.Tradition;
import de.avesbot.model.Trial;
import de.avesbot.model.Vantage;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.dv8tion.jda.api.entities.Message.Attachment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Nitrout
 */
public class HeldenSoftwareImport implements BiFunction<Attachment, String, Boolean> {
	
	private static final Pattern TRIAL_PATTERN = Pattern.compile("MU|KL|IN|CH|FF|GE|KO|KK", Pattern.CASE_INSENSITIVE);
	private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("Mut|Klugheit|Intuition|Charisma|Fingerfertigkeit|Gewandtheit|Konstitution|Körperkraft", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);

	private static final DocumentBuilder DOCUMENT_BUILDER = getDocumentBuilder();

	private static DocumentBuilder getDocumentBuilder() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		try {
			return dbf.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(HeldenSoftwareImport.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	@Override
	public Boolean apply(Attachment attachment, String memberId) {
		return getXmlFile(attachment).map(doc -> importCharacter(doc, memberId)).orElse(Boolean.FALSE);
	}

	private static Optional<Document> getXmlFile(Attachment attachment) {
		try (var is = attachment.getProxy().download().get()) {
			return Optional.of(DOCUMENT_BUILDER.parse(is));
		} catch (ExecutionException | IOException | SAXException ex) {
			Logger.getLogger(HeldenSoftwareImport.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InterruptedException ex) {
			Logger.getLogger(HeldenSoftwareImport.class.getName()).log(Level.SEVERE, null, ex);
			Thread.currentThread().interrupt();
		}
		return Optional.empty();
	}

	private static Boolean importCharacter(Document doc, String memberId) {
		var chara = toRoleplayCharacter(doc);
		Optional<String> insertedId = Avesbot.getStatementManager().insertRoleplayCharacter(memberId, chara);
		if (insertedId.isEmpty()) {
			return false;
		}
		chara = new RoleplayCharacter(insertedId.get(), chara);

		var abilities = toAbilities(doc);
		for (Ability a : abilities) {
			Avesbot.getStatementManager().insertAbility(chara, a);
		}

		// Get all character's (dis)advantages
		var vantages = toVantages(doc);
		for (Vantage v : vantages) {
			Avesbot.getStatementManager().insertVantage(chara, v);
		}

		// Get all character's special abilities
		var specials = toSpecials(doc);
		for (Special sp : specials) {
			Avesbot.getStatementManager().insertSpecial(chara, sp);
		}

		return true;
	}
	
	private static RoleplayCharacter toRoleplayCharacter(Document doc) {
		Node heroNode = XmlUtil.nodeList2NodeArray(doc.getElementsByTagName("held"))[0];
		String name = heroNode.getAttributes().getNamedItem("name").getNodeValue();

		NodeList attributeBlocks = doc.getElementsByTagName("eigenschaften");
		Node[] attributeNodes = new Node[0];
		for(int i = 0; i < attributeBlocks.getLength(); i++) {

			if(attributeBlocks.item(i).getParentNode().getNodeName().equals("held")) {
				attributeNodes = XmlUtil.nodeList2NodeArray(attributeBlocks.item(i).getChildNodes());
			}
		}
		Byte[] attributes = Stream.of(attributeNodes)
			.filter(node -> ATTRIBUTE_PATTERN.asMatchPredicate().test(node.getAttributes().getNamedItem("name").getNodeValue()))
			.limit(8)
			.map(node -> (byte)(Byte.valueOf(node.getAttributes().getNamedItem("value").getNodeValue()) + Byte.valueOf(node.getAttributes().getNamedItem("mod").getNodeValue())))
			.toArray(Byte[]::new);
		
		return new RoleplayCharacter(name, Ruleset.TDE4, new Vantage[]{}, new Special[]{}, attributes);
	}
	
	private static Vantage[] toVantages(Document doc) {
		
		Node[] advantageNodes = XmlUtil.nodeList2NodeArray(doc.getElementsByTagName("vorteil"));
		return Stream.of(advantageNodes)
			.filter(node -> node.getParentNode().getNodeName().equals("vt"))
			.map(HeldenSoftwareImport::toVantage)
			.toArray(Vantage[]::new);
	}
	
	private static Vantage toVantage(Node node) {
		var name = node.getAttributes().getNamedItem("name").getNodeValue();
		var attribute1 = Optional.ofNullable(node.getAttributes().getNamedItem("value")).map(Node::getNodeValue).orElse("");
		var attribute2 = "";

		if(node.hasChildNodes()) {
			attribute1 = node.getChildNodes().item(0).getAttributes().getNamedItem("value").getNodeValue();
			attribute2 = node.getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue();
		}

		if(name.equals("Schlechte Eigenschaft")) {
			name = node.getChildNodes().item(2).getAttributes().getNamedItem("value").getNodeValue();
			attribute1 = node.getChildNodes().item(1).getAttributes().getNamedItem("value").getNodeValue();
			attribute2 = "";
		}

		return new Vantage(name, attribute1, attribute2);
	}
	
	private static Special[] toSpecials(Document doc) {
		
		Node[] specialNode = XmlUtil.nodeList2NodeArray(doc.getElementsByTagName("sonderfertigkeit"));
		return Stream.of(specialNode)
			.filter(node -> node.getParentNode().getNodeName().equals("sf"))
			.map(HeldenSoftwareImport::toSpecial)
			.toArray(Special[]::new);
	}
	
	private static Special toSpecial(Node node) {
		var name = node.getAttributes().getNamedItem("name").getNodeValue();
		var attribute1 = "";
		var attribute2 = "";
		var attribute3 = "";

		if(node.hasChildNodes() && node.getChildNodes().item(0).hasAttributes()) {
			attribute1 = Optional.ofNullable(node.getChildNodes().item(0).getAttributes().getNamedItem("name")).map(valueNode -> valueNode.getNodeValue()).orElse("");
		}

		if(name.startsWith("Talentspezialisierung")) {
			name = "Talentspezialisierung";
			attribute1 = Optional.ofNullable(node.getChildNodes().item(0)).map(attributeNode -> attributeNode.getAttributes().getNamedItem("name").getNodeValue()).orElse("");
			attribute2 = Optional.ofNullable(node.getChildNodes().item(1)).map(attributeNode -> attributeNode.getAttributes().getNamedItem("name").getNodeValue()).orElse("");
		} else if(name.startsWith("Zauberspezialisierung")) {
			name = "Zauberspezialisierung";
			attribute1 = Optional.ofNullable(node.getChildNodes().item(0)).map(attributeNode -> attributeNode.getAttributes().getNamedItem("name").getNodeValue()).orElse("");
			attribute2 = Optional.ofNullable(node.getChildNodes().item(0)).map(attributeNode -> Tradition.mappedValueOf(attributeNode.getAttributes().getNamedItem("repraesentation").getNodeValue()).name()).orElse("");
			attribute3 = Optional.ofNullable(node.getChildNodes().item(1)).map(attributeNode -> attributeNode.getAttributes().getNamedItem("name").getNodeValue()).orElse("");
		} else if(node.hasChildNodes() && !node.getChildNodes().item(0).hasAttributes()) {
			NodeList attributeList = node.getChildNodes().item(0).getChildNodes();
			attribute1 = Optional.ofNullable(attributeList.item(0)).map(attributeNode -> attributeNode.getAttributes().getNamedItem("value").getNodeValue()).orElse("");
			attribute2 = Optional.ofNullable(attributeList.item(1)).map(attributeNode -> attributeNode.getAttributes().getNamedItem("value").getNodeValue()).orElse("");
			attribute3 = Optional.ofNullable(attributeList.item(2)).map(attributeNode -> attributeNode.getAttributes().getNamedItem("value").getNodeValue()).orElse("");
		}

		return new Special(name, attribute1, attribute2, attribute3);
	}
	
	private static Ability[] toAbilities(Document doc) {
		
		LinkedList<Node> abilityNodeList = new LinkedList<>();
		abilityNodeList.addAll(Arrays.asList(XmlUtil.nodeList2NodeArray(doc.getElementsByTagName("talentliste").item(0).getChildNodes())));
		abilityNodeList.addAll(Arrays.asList(XmlUtil.nodeList2NodeArray(doc.getElementsByTagName("zauberliste").item(0).getChildNodes())));

		return abilityNodeList.stream()
			.filter(node -> TRIAL_PATTERN.asPredicate().test(node.getAttributes().getNamedItem("probe").getNodeValue()))
			.map(HeldenSoftwareImport::toAbility)
			.toArray(Ability[]::new);
	}
	
	private static Ability toAbility(Node node) {
		final var abilityName = node.getAttributes().getNamedItem("name").getNodeValue();
		Matcher trialMatcher = TRIAL_PATTERN.matcher(node.getAttributes().getNamedItem("probe").getNodeValue());
		Trial trial = null;
		byte taw;

		var triialAttributes = trialMatcher.results()
				.map(MatchResult::group)
				.map(String::toUpperCase)
				.map(HeldenSoftwareImport::attributeFromTranslatedLiteral)
				.toArray(Attribute[]::new);
		if (triialAttributes.length == 3) {
			trial = new Trial(triialAttributes[0], triialAttributes[1], triialAttributes[2]);
		}

		try {
			taw = Byte.parseByte(node.getAttributes().getNamedItem("value").getNodeValue());
		}
		catch(NumberFormatException nfe) {
			taw = 0;
		}

		if (node.getNodeName().equals("zauber")) {
			var rep = Optional.ofNullable(node.getAttributes().getNamedItem("repraesentation"))
					.map(Node::getNodeValue)
					.map(Tradition::mappedValueOf)
					.orElse(Tradition.NONE);
			var spellName = Optional.ofNullable(node.getAttributes().getNamedItem("variante"))
					.map(Node::getNodeValue)
					.filter(value -> !value.isEmpty())
					.map(value -> "%s [%s]".formatted(abilityName, value))
					.orElse(abilityName);

			return new Ability(spellName, rep, trial, taw, Ability.Type.SPELL);
		}

		return new Ability(abilityName, Tradition.NONE, trial, taw, Ability.Type.TALENT);
	}

	private static Attribute attributeFromTranslatedLiteral(String attribute) {
		return switch (attribute) {
			case "MU" ->
				Attribute.COURAGE;
			case "KL" ->
				Attribute.SAGACITY;
			case "IN" ->
				Attribute.INTUITION;
			case "CH" ->
				Attribute.CHARISMA;
			case "FF" ->
				Attribute.DEXTERITY;
			case "GE" ->
				Attribute.AGILITY;
			case "KO" ->
				Attribute.CONSTITUTION;
			case "KK" ->
				Attribute.STRENGTH;
			default ->
				null;
		};
	}
	
}
