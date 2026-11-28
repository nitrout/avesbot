package de.avesbot.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;

/**
 *
 * @author nitrout
 */
public class XmlUtil {

	private XmlUtil() {
		throw new UnsupportedOperationException("Instantiation not allowed");
	}
	
	public static Node[] getChildrenNodes(Node t) {
		return nodeList2NodeArray(t.getChildNodes());
	}
	
	public static Node[] nodeList2NodeArray(NodeList nl) {
		Node[] list = new Node[nl.getLength()];
		for(int i = 0; i < nl.getLength(); i++) {
			list[i] = nl.item(i);
		}
		return list;
	}
	
	public static Optional<Node> findParentNodeWithTagName(Node node, String tagName) {
		
		Optional<Node> result = Optional.ofNullable(node.getParentNode());
		
		if(result.isPresent() && !result.get().getNodeName().equals(tagName))
			return findParentNodeWithTagName(result.get(), tagName);
		else
			return result;
	}
}
