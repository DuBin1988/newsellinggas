package com.aote.helper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlHelper {

	public static String findElemSignValue(Element parent, String attrname,
			String attrvalue, String signname) {
		String result = "";
		// згжаевдк
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			Node node = parent.getChildNodes().item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element enode = (Element) node;
				String nodeAttrVal = enode.getAttribute(attrname);
				if (nodeAttrVal.equals(attrvalue)) {
					result = enode.getAttribute(signname);
				}
			}
		}
		return result;
	}
}
