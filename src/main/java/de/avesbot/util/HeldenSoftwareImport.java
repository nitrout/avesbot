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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Nitrout
 */
public class HeldenSoftwareImport implements Runnable {
	
	private static final Pattern TRIAL_PATTERN = Pattern.compile("\\((MU|KL|IN|CH|FF|GE|KO|KK)/(MU|KL|IN|CH|FF|GE|KO|KK)/(MU|KL|IN|CH|FF|GE|KO|KK)\\)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("Mut|Klugheit|Intuition|Charisma|Fingerfertigkeit|Gewandtheit|Konstitution|Körperkraft", Pattern.CASE_INSENSITIVE);
	
	private final Document doc;
	private final String memberId;
	
	public HeldenSoftwareImport(Document doc, String memberId) {
		this.doc = doc;
		this.memberId = memberId;
	}

	@Override
	public void run() {
		
		var chara = toRoleplayCharacter(doc);
		Optional<String> insertedId = Avesbot.getStatementManager().insertRoleplayCharacter(memberId, chara);
		if(insertedId.isEmpty()) {
			return;
		}
		chara = new RoleplayCharacter(insertedId.get(), chara);

		var abilities = toAbilities(doc);
		for(Ability a : abilities)
			Avesbot.getStatementManager().insertAbility(chara, a);

		// Get all character's (dis)advantages
		var vantages = toVantages(doc);
		for(Vantage v : vantages)
			Avesbot.getStatementManager().insertVantage(chara, v);

		// Get all character's special abilities
		var specials = toSpecials(doc);
		for(Special sp : specials)
			Avesbot.getStatementManager().insertSpecial(chara, sp);
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
		var attribute1 = Optional.ofNullable(node.getAttributes().getNamedItem("value")).map(valueNode -> valueNode.getNodeValue()).orElse("");
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
		String abilityName = node.getAttributes().getNamedItem("name").getNodeValue();
		Matcher trialMatcher = TRIAL_PATTERN.matcher(node.getAttributes().getNamedItem("probe").getNodeValue());
		Trial trial = null;
		byte taw;
		Ability.Type type = node.getNodeName().equals("zauber") ? Ability.Type.SPELL : Ability.Type.TALENT;
		Tradition rep = Tradition.NONE;

		if(trialMatcher.find()) {
			trial = new Trial(Attribute.valueOf(trialMatcher.group(1).toUpperCase()), Attribute.valueOf(trialMatcher.group(2).toUpperCase()), Attribute.valueOf(trialMatcher.group(3).toUpperCase()));
		}

		try {
			taw = Byte.parseByte(node.getAttributes().getNamedItem("value").getNodeValue());
		}
		catch(NumberFormatException nfe) {
			taw = 0;
		}

		if(node.getNodeName().equals("zauber") && Optional.ofNullable(node.getAttributes().getNamedItem("repraesentation")).isPresent()) {
			rep = Optional.of(Tradition.mappedValueOf(node.getAttributes().getNamedItem("repraesentation").getNodeValue())).get();
		}
		
		return new Ability(abilityName, rep, trial, taw, type);
	}
	
}
