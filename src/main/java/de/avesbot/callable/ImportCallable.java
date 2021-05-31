package de.avesbot.callable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import de.avesbot.Avesbot;
import de.avesbot.model.Ability;
import de.avesbot.model.Attribute;
import de.avesbot.model.Tradition;
import de.avesbot.model.RolePlayCharacter;
import de.avesbot.model.Ruleset;
import de.avesbot.model.Special;
import de.avesbot.model.Trial;
import de.avesbot.model.Vantage;
import de.avesbot.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Imports a character from an xml file.
 * Works currently only for DSA4 ruleset.
 * @deprecated not compatible with the new slash command api
 * @author Nitrout
 */
@Deprecated
public class ImportCallable extends CommandCallable {
	
	public static final String MANUAL = "**!import**\t- Als Kommentar zu einem Dateiupload startet dies einen Charakterimport aus einer XML-Datei der Heldensoftware";
	private static final Pattern TRIAL_PATTERN = Pattern.compile("\\((MU|KL|IN|CH|FF|GE|KO|KK)/(MU|KL|IN|CH|FF|GE|KO|KK)/(MU|KL|IN|CH|FF|GE|KO|KK)\\)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("Mut|Klugheit|Intuition|Charisma|Fingerfertigkeit|Gewandtheit|Konstitution|Körperkraft", Pattern.CASE_INSENSITIVE);
	
	private DocumentBuilder db;
	
	/**
	 * Creates a new ImportCallable.
	 * @param event 
	 */
	public ImportCallable(MessageReceivedEvent event) {
		
		super(event);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		try {
			this.db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	@Override
	public String call() throws Exception {
		
		StringBuilder result = new StringBuilder();
		
		if(attachments.size() > 0) {
			
			if("XML".equalsIgnoreCase(attachments.get(0).getFileExtension())) {
				
				try(InputStream is = attachments.get(0).retrieveInputStream().get()) {
					
					Document doc = db.parse(is);
					
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
					
					
					RolePlayCharacter chara = new RolePlayCharacter(name, Ruleset.TDE4, new Vantage[]{}, new Special[]{}, attributes);
					Optional<String> insertedId = Avesbot.getStatementManager().insertRoleplayCharacter(member.getId(), chara);
					
					if(insertedId.isPresent()) {
						
						chara = new RolePlayCharacter(insertedId.get(), chara);
						
						// Get all character's abilities and spells
						LinkedList<Node> abilityNodeList = new LinkedList<>();
						abilityNodeList.addAll(Arrays.asList(XmlUtil.nodeList2NodeArray(doc.getElementsByTagName("talentliste").item(0).getChildNodes())));
						abilityNodeList.addAll(Arrays.asList(XmlUtil.nodeList2NodeArray(doc.getElementsByTagName("zauberliste").item(0).getChildNodes())));
						
						Node[] abilityNodes = abilityNodeList.stream()
							.filter(node -> TRIAL_PATTERN.asPredicate().test(node.getAttributes().getNamedItem("probe").getNodeValue()))
							.toArray(Node[]::new);
						
						Ability[] abilities = this.parseAbilities(abilityNodes);
						for(Ability a : abilities)
							Avesbot.getStatementManager().insertAbility(chara, a);
						
						// Get all character's (dis)advantages
						Node[] advantageNodes = XmlUtil.nodeList2NodeArray(doc.getElementsByTagName("vorteil"));
						Vantage[] vantages = this.parseVantages(advantageNodes);
						for(Vantage v : vantages)
							Avesbot.getStatementManager().insertVantage(chara, v);
						
						// Get all character's special abilities
						Node[] specialNode = XmlUtil.nodeList2NodeArray(doc.getElementsByTagName("sonderfertigkeit"));
						Special[] specials = this.parseSpecials(specialNode);
						for(Special sp : specials)
							Avesbot.getStatementManager().insertSpecial(chara, sp);
						
						result.append(String.format("Charakter importiert!"));
					} else {
						result.append(String.format("Charakter konnte nicht erstellt werden!"));
					}
					
				} catch (InterruptedException | ExecutionException | IOException | SAXException ex) {
					System.err.println(ex.getMessage());
				}
			} else {
				result.append(String.format("Keine XML-Datei! Bitte lade eine XML-Datei hoch!"));
			}
		} else {
			result.append(String.format("Datei fehlt! Bitte lade eine XML-Datei hoch mit diesem Befehl als Kommentar!"));
		}
		
		return result.toString();
	}
	
	private Vantage[] parseVantages(Node[] advantageNodes) {
		
		Vantage[] advantages = Stream.of(advantageNodes)
			.filter(node -> node.getParentNode().getNodeName().equals("vt"))
			.map(node -> {
				String name = node.getAttributes().getNamedItem("name").getNodeValue();
				String attribute1 = "";
				String attribute2 = "";

				attribute1 = Optional.ofNullable(node.getAttributes().getNamedItem("value")).map(valueNode -> valueNode.getNodeValue()).orElse("");
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
			})
			.toArray(Vantage[]::new);
		
		return advantages;
	}
	
	private Special[] parseSpecials(Node[] advantageNodes) {
		
		Special[] specials = Stream.of(advantageNodes)
			.filter(node -> node.getParentNode().getNodeName().equals("sf"))
			.map(node -> {
				String name = node.getAttributes().getNamedItem("name").getNodeValue();
				String attribute1 = "";
				String attribute2 = "";
				String attribute3 = "";
				
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
			})
			.toArray(Special[]::new);
		
		return specials;
	}
	
	private Ability[] parseAbilities(Node[] abilityNodes) {
		
		LinkedList<Ability> abilityList = new LinkedList<>();
		
		for(Node node : abilityNodes) {
			String abilityName = node.getAttributes().getNamedItem("name").getNodeValue();
			Matcher trialMatcher = TRIAL_PATTERN.matcher(node.getAttributes().getNamedItem("probe").getNodeValue());
			Trial trial;
			byte taw;
			Ability.Type type = node.getNodeName().equals("zauber") ? Ability.Type.SPELL : Ability.Type.TALENT;
			Tradition rep = Tradition.NONE;

			if(trialMatcher.find()) {
				trial = new Trial(Attribute.valueOf(trialMatcher.group(1).toUpperCase()), Attribute.valueOf(trialMatcher.group(2).toUpperCase()), Attribute.valueOf(trialMatcher.group(3).toUpperCase()));
			} else {
				continue;
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
			abilityList.add(new Ability(abilityName, rep, trial, taw, type));
		}
		
		return abilityList.toArray(Ability[]::new);
	}
}