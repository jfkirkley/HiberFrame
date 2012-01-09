package org.maxml.xpath;

// This file uses 4 space indents, no tabs.

// // Imported JAVA API for XML Parsing 1.0 classes
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

// // Imported Serializer classes

import org.apache.axis.utils.XMLUtils;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.DocumentType;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import org.maxml.util.FileUtils;
import org.maxml.util.StringInputStream;
import org.maxml.util.Tracer;
import org.maxml.util.Util;
//import org.maxml.xpath.XPathAPI;

import org.xml.sax.EntityResolver;

class MyResolver implements EntityResolver {
	public InputSource resolveEntity(String publicId, String systemId) {
		if (systemId != null) {
			// return a special input source
			InputSource is = new /* noIB */InputSource(systemId);

			return is;
		} else {
			// use the default behaviour
			return null;
		}
	}
}

/**
 * Very basic utility for applying an XPath epxression to an xml file and
 * printing information / about the execution of the XPath object and the nodes
 * it finds. Takes 2 arguments: (1) an xml filename (2) an XPath expression to
 * apply to the file Examples: java ApplyXPath foo.xml / java ApplyXPath foo.xml
 * /doc/name[1]/@last
 * 
 * @see XPathAPI
 */
public class ApplyXPath {

	public final static String	PATH_SEPARATOR	= "/";

	public final static String	_CLASSNAME		= "org.maxml.ApplyXPath";
	protected String			filename		= null;
	protected String			xpath			= null;
	protected static Node		doc;

	private static ApplyXPath	instance		= null;

	public static ApplyXPath i() {
		if (instance == null) {
			instance = new ApplyXPath();
		}
		return instance;
	}

	private void getDoc() {
		InputSource in;
		try {
			in = new /* noIB */InputSource(new FileInputStream(filename));
		} catch (FileNotFoundException fnf) {
			System.err.println("FileInputStream of " + filename + " threw: " + fnf.toString());
			fnf.printStackTrace();
			return;
		}

		// Use a DOMParser from Xerces so we get a complete DOM from the
		// document
		DOMParser parser = new DOMParser();
		try {
			parser.parse(in);
		} catch (Exception e1) {
			System.err.println("Parsing " + filename + " threw: " + e1.toString());
			e1.printStackTrace();
			return;
		}

		doc = parser.getDocument();
	}

	public Document getDoc(String filename) {
		InputSource in;
		try {
			// File f = new File( filename );
			// if(Tracer.on && Tracer.on(_CLASSNAME,
			// "getDoc"))Tracer.internalOut( filename + " exists: " + f.exists()
			// );
			in = new /* noIB */InputSource(new FileInputStream(filename));

			// Use a DOMParser from Xerces so we get a complete DOM from the
			// document
			DOMParser parser = new DOMParser();
			try {

				// FIXME!! need to set ignorable whitespace
				// DocumentBuilderFactory dfactory =
				// DocumentBuilderFactory.newInstance();
				// dfactory.setValidating(true);
				// dfactory.setIgnoringElementContentWhitespace(true);
				// DocumentBuilder parser = dfactory.newDocumentBuilder();

				// if(Tracer.on && Tracer.on(_CLASSNAME,
				// "getDoc"))Tracer.internalOut( "entityResolver: " +
				// parser.getEntityResolver() );
				parser.parse(in);
			} catch (Exception e1) {
				System.err.println("Parsing " + filename + " threw: " + e1.toString());
				e1.printStackTrace();
			}

			return parser.getDocument();

		} catch (FileNotFoundException fnf) {
			// if (Tracer.on) {
			System.err.println("FileInputStream of " + filename + " threw: " + fnf.toString());
			fnf.printStackTrace();
			// }
		}
		return null;

	}

	public void setNode(Node n) {
		doc = n;
	}

	public Node getNode() {
		return doc;
	}

	public Document initDoc(String xmlStr) {
		return initDoc(new /* noIB */InputSource(new StringReader(xmlStr)));
	}

	public Document initDoc(File xmlFile) {
		try {
			return initDoc(new FileInputStream(xmlFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Document initDoc(InputStream is) {
		return initDoc(new /* noIB */InputSource(is));
	}

	public Document initDoc(InputSource in) {
		try {
			DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

			Document d = dfactory.newDocumentBuilder().parse(in);
			return d;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Document makeADoc(String fileName) {
		DOMParser parser = new DOMParser();

		try {
			parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
			parser.parse(fileName);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		Document document = parser.getDocument();
		return document;
	}

	public Document getNewDoc() {
		try {
			DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dfactory.newDocumentBuilder();
			return db.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Document getNewDoc(String rootTagName) {
		Document doc = getNewDoc();
		Element rootElement = doc.createElement(rootTagName);
		doc.appendChild(rootElement);
		return doc;

	}

	public Document getNewDoc(String namespaceURI, String qualifiedName, String publicId, String systemId) {
		try {
			DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dfactory.newDocumentBuilder();
			DOMImplementation implementation = db.getDOMImplementation();

			return implementation.createDocument(namespaceURI, qualifiedName, implementation.createDocumentType(
					qualifiedName, publicId, systemId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getXPathString(String xpath) {
		return getXPathString(xpath, doc);
	}

	public String getXPathString(String xpath, Node relativeTo) {
		Node n = getXPathNode(xpath, relativeTo);
		if (n != null)
			return n.getNodeValue();
		return null;
	}

	public String[] getXPathStrings(String xpath) {
		return getXPathStrings(xpath, doc);
	}

	public String[] getXPathStrings(String xpath, Node relativeTo) {
		Vector v = getXPathStringVector(xpath, relativeTo);

		if (v != null) {
			String vals[] = new String[v.size()];
			Enumeration e = v.elements();
			int i = 0;
			while (e.hasMoreElements())
				vals[i++] = (String) e.nextElement();
			return vals;
		}
		return null;
	}

	public Vector getXPathStringVector(String xpath) {
		return getXPathStringVector(xpath, doc);
	}

	public Vector getXPathStringVector(String xpath, Node relativeTo) {
		NodeList nl = getXPathNodeList(xpath, relativeTo);
		if (nl != null) {
			Node n;
			int i = 0;
			Vector v = new Vector();
			while ((n = nl.item(i++)) != null) {
				v.addElement(n.getNodeValue());
			}
			return v;
		}
		return null;
	}

	public Vector getXPathNodeVector(String xpath) {
		return getXPathNodeVector(xpath, doc);
	}

	public Vector getXPathNodeVector(String xpath, Node relativeTo) {
		NodeList nl = getXPathNodeList(xpath, relativeTo);
		if (nl != null) {
			Node n;
			int i = 0;
			Vector v = new Vector();
			while ((n = nl.item(i++)) != null) {
				v.addElement(n);
			}
			return v;
		}
		return null;
	}

	public Node getXPathNode(String xpath) {
		return getXPathNode(xpath, doc);
	}

	public Node getXPathNode(String xpath, Node relativeTo) {
		NodeList nl = getXPathNodeList(xpath, relativeTo);
		// if(Trace.enabled && Trace.on(this) ) Trace.out.println( "nl: " + nl
		// );

		if (nl != null) {
			return nl.item(0);
		}
		return null;
	}

	public NodeList getXPathNodeList(String xpath) {
		return getXPathNodeList(xpath, doc);
	}

	public NodeList getXPathNodeList(String xpath, Node relativeTo) {
		try {
			if (xpath == null || xpath.length() == 0) {
				return null;
			}
			return XPathAPI.selectNodeList(relativeTo, xpath);
		} catch (TransformerException se) {
			se.printStackTrace();
		}
		return null;
	}

	public String getElementChildContent(Element n, String childName) {

		NodeList nl = n.getChildNodes();

		for (int i = 0; i < nl.getLength(); ++i) {
			if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {

				Element e = (Element) nl.item(i);
				if (e.getTagName().equals(childName)) {
					return getElementContent(e);
				}
			}
		}
		return null;
	}

	public String getElementContent(Element e) {
		Node valTextNode = e.getFirstChild();
		if (valTextNode != null && valTextNode.getNodeType() == Node.TEXT_NODE) {
			return ((Text) valTextNode).getData();
		}
		return null;
	}

	public String getElementCDATAContent(Element e) {
		Node valTextNode = e.getFirstChild();
		if (valTextNode != null && valTextNode.getNodeType() == Node.CDATA_SECTION_NODE) {
			return ((CDATASection) valTextNode).getData();
		}
		return null;
	}

	public String getTextContent(Element e) {
		String content = getElementContent(e);
		if(content == null) {
			content = getElementCDATAContent(e);
		}
		return content;
	}
	
	public void removeAllElementChildrenWithName(Element parent, String gi) {

		Element e = getFirstElementChildWithName(parent, gi);
		while (e != null) {
			parent.removeChild(e);
			e = getFirstElementChildWithName(parent, gi);
		}

		// NodeList nl = parent.getChildNodes();

		// for( int i = 0; i < nl.getLength(); ++i ) {
		// Node n = nl.item(i);
		// if( n.getNodeType() == Node.ELEMENT_NODE &&
		// n.getNodeName().equals(gi) ) {
		// parent.removeChild( n );
		// }
		// }
	}

	public Element getFirstElementChild(Node n) {

		NodeList nl = n.getChildNodes();

		for (int i = 0; i < nl.getLength(); ++i) {
			if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
				return (Element) nl.item(i);
			}
		}
		return null;
	}

	public Element getFirstElementChildWithName(Element n, String childName) {

		NodeList nl = n.getChildNodes();

		for (int i = 0; i < nl.getLength(); ++i) {
			if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {

				Element e = (Element) nl.item(i);
				if (e.getTagName().equals(childName)) {
					return e;
				}
			}
		}
		return null;
	}

	public Node getFirstElementSibling(Node n) {

		Node temp = n;

		while ((temp = temp.getNextSibling()) != null) {
			if (temp.getNodeType() == Node.ELEMENT_NODE) {
				return temp;
			}
		}
		return null;
	}

	public Element getChildElementOrBuild(Document d, String childName) {
		Element elem = (Element) ApplyXPath.i().getNodeForXPath(d.getDocumentElement(), childName);

		if (elem == null) {
			elem = d.createElement(childName);
			d.getDocumentElement().appendChild(elem);
		}
		return elem;
	}

	public int getIntAttrValue(String paramName, Element node) {
		String val = node.getAttribute(paramName);
		if (val != null) {
			return Integer.parseInt(val);
		}
		return 0;
	}

	public float getFloatAttrValue(String paramName, Element node) {
		String val = node.getAttribute(paramName);
		if (val != null) {
			return Float.parseFloat(val);
		}
		return (float) 0.0;
	}

	public double getDoubleAttrValue(String paramName, Element node) {
		String val = node.getAttribute(paramName);
		if (val != null) {
			return Double.parseDouble(val);
		}
		return (double) 0.0;
	}

	public void setText(Element elem, String text) {
		Text textNode = elem.getOwnerDocument().createTextNode(text);
		elem.appendChild(textNode);
	}

	public int getIndexOfElement(Element element) {
		int index = 0;
		Node n = element;
		while ((n = n.getPreviousSibling()) != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE)
				++index;
		}
		return index;
	}

	public int getIndexOfElementMatchingTagName(Element element) {
		int index = 0;
		Node n = element;
		while ((n = n.getPreviousSibling()) != null) {
			if (n.getNodeType() == Node.ELEMENT_NODE && element.getNodeName().equals(n.getNodeName()))
				++index;
		}
		return index;
	}

	public int getDepthFromAncestor(Node thisNode, Node ancestor) {
		Node n = thisNode;
		int depth = 0;
		while (n != null && (n = n.getParentNode()) != ancestor) {
			depth++;
		}
		return depth;
	}

	public boolean hasElementChild(Element elem, String childGI) {
		return getFirstElementChildWithName(elem, childGI) != null;
	}

	public void newXPathThingy(Document d, String xpathExpr) throws XPathExpressionException {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(xpathExpr);

			Object result = expr.evaluate(d, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) {
				System.out.println(nodes.item(i).getNodeName());
			}
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public NodeList getMatches(Element element, String xpathExpr) throws XPathExpressionException {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(xpathExpr);

			Object result = expr.evaluate(element, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) {
				System.out.println(xpathExpr + "---------->>> " + nodes.item(i).getNodeName());
			}
			return nodes;
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void pnl(NodeList nodes) {
		System.out.println("----------XXX:-  " + nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			System.out.println("---------->>> " + nodes.item(i).getNodeName());
		}
	}

	// public void addXML( Document doc, Node contextNode, String
	// relativeXPath, Double nodeValue ){
	// public void addXML( Document doc, Node contextNode, String
	// relativeXPath, Integer nodeValue ){

	public String getPathStringFromThisToAncestor(Node thisNode, Node ancestorNode, Node startNode, String delim,
			boolean includeAncestor) {
		// System.out.println("te: " + thisNode.getTagName());
		// System.out.println("ae: " + ancestorNode.getTagName());

		if (thisNode == ancestorNode) {// || thisNode.getParentNode() == null)
			// {
			if (includeAncestor) {
				return delim + getXPathRep(thisNode, startNode);
			} else {
				return ".";
			}
		} else if (thisNode instanceof Attr) {
			Attr attr = (Attr) thisNode;
			return getPathStringFromThisToAncestor(attr.getOwnerElement(), ancestorNode, startNode, delim,
					includeAncestor)
					+ delim + getXPathRep(thisNode, startNode);

		} else if (thisNode.getParentNode().getNodeType() == Node.DOCUMENT_NODE) {
			System.err.println("parent Node is Document Node, ancesstor not found!!");
			return null;
		} else {
			String xpathRep = getXPathRep(thisNode, startNode);
			if (Util.i().hasContent(xpathRep)) {
				xpathRep = delim + xpathRep;
			}
			return getPathStringFromThisToAncestor((Node) thisNode.getParentNode(), ancestorNode, startNode, delim,
					includeAncestor)
					+ xpathRep;
		}
	}

	public String getXPathRep(Node node, Node startNode) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			return ((Element) node).getTagName();
		} else if (node.getNodeType() == Node.TEXT_NODE) {
			Text text = (Text) node;
			if (text.getParentNode().getNodeType() == Node.ATTRIBUTE_NODE) {
				return "";
			} else {
				return "text()";
			}
		} else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			Attr attr = (Attr) node;
			if (startNode.getNodeType() == Node.TEXT_NODE) {
				return "[@" + attr.getName() + "='" + attr.getValue() + "']";
			}
			return "@" + attr.getName();

		}
		return "";
	}

	public String getPathStringFromThisToAncestor(Node thisNode, Node ancestorNode) {
		return getPathStringFromThisToAncestor(thisNode, ancestorNode, thisNode, PATH_SEPARATOR, true);

	}

	public String getPathStringFromThisToAncestor(Node thisNode, Node ancestorNode, boolean includeAncestor) {
		return getPathStringFromThisToAncestor(thisNode, ancestorNode, thisNode, PATH_SEPARATOR, includeAncestor);

	}

	public String getPathToRoot(Node n, String delim) {
		String path = n.getNodeName();
		n = n.getParentNode();

		while (n != null) {
			path = n.getNodeName() + delim + path;
			n = n.getParentNode();
		}

		return path;
	}

	public String getPathToRoot(Node n) {
		return getPathToRoot(n, ".");
	}

	public String getPathStringFromAncestorToAncestor(Element thisElement, Element ancestorElement, int ancestorLevel) {
		String fullPath = getPathStringFromThisToAncestor(thisElement, ancestorElement);
		String returnPath = "";
		String[] pathElems = fullPath.split("\\" + PATH_SEPARATOR);
		for (int i = 0; i < pathElems.length - ancestorLevel; i++) {
			returnPath += PATH_SEPARATOR + pathElems[i];
		}
		return returnPath;
	}

	public void addXML(Document doc, Node contextNode, String relativeXPath, Object nodeValue) {
		if (nodeValue == null)
			return;
		addXML(doc, contextNode, relativeXPath, nodeValue.toString());
	}

	public void addXML(Document doc, Node contextNode, String relativeXPath, String nodeValue) {

		if (nodeValue == null)
			return;
		StringTokenizer elementTokens = new StringTokenizer(relativeXPath, "/");
		Node currContextNode = contextNode;
		boolean createNewXML = false;
		if (Tracer.on && Tracer.on("org.maxml.ApplyXPath", "addXML"))
			Tracer.out("currContextNode: " + currContextNode);

		while (elementTokens.hasMoreElements()) {
			String elementGI = (String) elementTokens.nextElement();

			if (Tracer.on && Tracer.on("org.maxml.ApplyXPath", "addXML"))
				Tracer.out("elementGI: " + elementGI);
			if (Tracer.on && Tracer.on("org.maxml.ApplyXPath", "addXML"))
				Tracer.out("currContextNode: " + currContextNode);

			if (elementGI.startsWith("@")) {

				if (nodeValue != null) {
					((Element) currContextNode).setAttribute(elementGI.substring(1), nodeValue);
				} else {
					((Element) currContextNode).setAttribute(elementGI.substring(1), "");
				}

			} else if (elementGI.equalsIgnoreCase("text()")) {

				if (nodeValue != null) {
					// if(Tracer.on && Tracer.on(_CLASSNAME,
					// "addXML"))Tracer.internalOut( "nodeValue: " + nodeValue
					// );
					currContextNode.appendChild(doc.createTextNode(nodeValue));
				} else {
					currContextNode.appendChild(doc.createTextNode(""));
				}

			} else if (createNewXML) {

				Node newNode = doc.createElement(elementGI);
				currContextNode.appendChild(newNode);
				currContextNode = newNode;

			} else {

				Node temp = getXPathNode(elementGI, currContextNode);
				if (temp == null) {

					createNewXML = true;
					Node newNode = doc.createElement(elementGI);
					currContextNode.appendChild(newNode);
					currContextNode = newNode;

				} else {
					currContextNode = temp;
				}
			}
		}
	}

	public void addXMLSubTree(Document doc, Node contextNode, String relativeXPath, Element subTree) {
		if (subTree == null)
			return;

		StringTokenizer elementTokens = new StringTokenizer(relativeXPath, "/");
		Node currContextNode = contextNode;
		boolean createNewXML = false;
		if (Tracer.on && Tracer.on("org.maxml.ApplyXPath", "addXML"))
			Tracer.out("currContextNode: " + currContextNode);

		while (elementTokens.hasMoreElements()) {
			String elementGI = (String) elementTokens.nextElement();

			if (Tracer.on && Tracer.on("org.maxml.ApplyXPath", "addXML"))
				Tracer.out("elementGI: " + elementGI);
			if (Tracer.on && Tracer.on("org.maxml.ApplyXPath", "addXML"))
				Tracer.out("currContextNode: " + currContextNode);

			if (createNewXML) {

				Node newNode = doc.createElement(elementGI);
				currContextNode.appendChild(newNode);
				currContextNode = newNode;

			} else {

				Node temp = getXPathNode(elementGI, currContextNode);
				if (temp == null) {

					createNewXML = true;
					Node newNode = doc.createElement(elementGI);
					currContextNode.appendChild(newNode);
					currContextNode = newNode;

				} else {
					currContextNode = temp;
				}
			}
		}
		currContextNode.appendChild(subTree);
	}

	public Document getDocFromStringXML(String stringXML) {
		InputSource in;

		in = new /* noIB */InputSource(new StringInputStream(stringXML));

		// Use a DOMParser from Xerces so we get a complete DOM from the
		// document
		DOMParser parser = new DOMParser();
		try {
			parser.parse(in);
		} catch (Exception e1) {
			System.err.println("Parsing " + stringXML + " threw: " + e1.toString());
			e1.printStackTrace();
			return null;
		}

		return parser.getDocument();

	}

	public Document getDocFromFile(String _filename) {
		InputSource in;
		try {
			in = new /* noIB */InputSource(new FileInputStream(_filename));
		} catch (FileNotFoundException fnf) {
			System.err.println("FileInputStream of " + _filename + " threw: " + fnf.toString());
			fnf.printStackTrace();
			return null;
		}

		// Use a DOMParser from Xerces so we get a complete DOM from the
		// document
		DOMParser parser = new DOMParser();
		try {
			parser.parse(in);
		} catch (Exception e1) {
			System.err.println("Parsing " + _filename + " threw: " + e1.toString());
			e1.printStackTrace();
			return null;
		}

		return parser.getDocument();

	}

	public Vector getStringVecForXPath(Node node, String _xpath) {

		Vector retStringVec = new Vector();
		try {
			// Use the simple _xpath API to select a node.
			NodeList nl = XPathAPI.selectNodeList(node, _xpath);
			int n = nl.getLength();
			for (int i = 0; i < n; i++) {
				retStringVec.addElement(nl.item(i).getNodeValue());
				// if(Trace.enabled && Trace.on(this) ) Trace.out.println(
				// nl.item(i) );
			}
		} catch (Exception e2) {
			System.err.println("selectNodeList threw: " + e2.toString()
					+ " perhaps your _xpath didn't select any nodes");
			e2.printStackTrace();
			return null;
		}
		return retStringVec;
	}

	public Vector getStringVecForXPath(Document doc, String _xpath) {

		Vector retStringVec = new Vector();
		Node root = doc.getDocumentElement();
		try {
			// Use the simple _xpath API to select a node.
			NodeList nl = XPathAPI.selectNodeList(root, _xpath);

			int n = nl.getLength();
			for (int i = 0; i < n; i++) {
				retStringVec.addElement(nl.item(i).getNodeValue());
				// if(Trace.enabled && Trace.on(this) ) Trace.out.println(
				// nl.item(i) );
			}
		} catch (Exception e2) {
			System.err.println("selectNodeList threw: " + e2.toString()
					+ " perhaps your _xpath didn't select any nodes");
			e2.printStackTrace();
			return null;
		}
		return retStringVec;
	}

	public Vector getNodeVecForXPath(Document doc, String _xpath) {

		Vector retStringVec = new Vector();
		Node root = doc.getDocumentElement();
		try {
			// Use the simple _xpath API to select a node.
			NodeList nl = XPathAPI.selectNodeList(root, _xpath);

			int n = nl.getLength();
			for (int i = 0; i < n; i++) {
				retStringVec.addElement(nl.item(i));
				// if(Trace.enabled && Trace.on(this) ) Trace.out.println(
				// nl.item(i) );
			}
		} catch (Exception e2) {
			System.err.println("selectNodeList threw: " + e2.toString()
					+ " perhaps your _xpath didn't select any nodes");
			e2.printStackTrace();
			return null;
		}
		return retStringVec;
	}

	public Collection getNodeCollectionForXPath(Document doc, String _xpath) {
		Node root = doc.getDocumentElement();
		return getNodeCollectionForXPath(root, _xpath);
	}

	public Collection getNodeCollectionForXPath(Node root, String _xpath) {

		ArrayList nodeCollection = new ArrayList();

		try {
			// Use the simple _xpath API to select a node.
			NodeList nl = XPathAPI.selectNodeList(root, _xpath);

			int n = nl.getLength();
			for (int i = 0; i < n; i++) {
				nodeCollection.add(nl.item(i));
			}
		} catch (Exception e2) {
			System.err.println("selectNodeList threw: " + e2.toString()
					+ " perhaps your _xpath didn't select any nodes");
			e2.printStackTrace();
			return null;
		}
		return nodeCollection;
	}

	public Collection getElementChildren(Element parent, String childTagName) {

		ArrayList nodeCollection = new ArrayList();

		try {
			// Use the simple _xpath API to select a node.
			NodeList nl = parent.getChildNodes();

			int n = nl.getLength();
			for (int i = 0; i < n; i++) {
				if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {

					Element e = (Element) nl.item(i);
					if (e.getTagName().equalsIgnoreCase(childTagName)) {
						nodeCollection.add(nl.item(i));
					}
				}
			}
		} catch (Exception e2) {
			System.err.println("selectNodeList threw: " + e2.toString()
					+ " perhaps your _xpath didn't select any nodes");
			e2.printStackTrace();
			return null;
		}
		return nodeCollection;
	}

	public String getAllText(Node root) {

		String text = "";

		try {
			// Use the simple _xpath API to select a node.
			NodeList nl = root.getChildNodes();

			int n = nl.getLength();
			for (int i = 0; i < n; i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					text += getAllText(node);
				} else if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
					text += node.getNodeValue();
				} else if (node.getNodeType() == Node.TEXT_NODE) {
					text += node.getNodeValue();
				}
			}
			
		} catch (Exception e2) {
			System.err.println("selectNodeList threw: " + e2.toString()
					+ " perhaps your _xpath didn't select any nodes");
			e2.printStackTrace();
			return null;
		}
		return text;
	}

	public Collection<String> getStringCollectionForXPath(Node root, String _xpath) {

		ArrayList<String> stringCollection = new ArrayList<String>();

		try {
			// Use the simple _xpath API to select a node.
			NodeList nl = XPathAPI.selectNodeList(root, _xpath);

			int n = nl.getLength();
			for (int i = 0; i < n; i++) {
				stringCollection.add(nl.item(i).getNodeValue());
			}
		} catch (Exception e2) {
			System.err.println("selectNodeList threw: " + e2.toString()
					+ " perhaps your _xpath didn't select any nodes");
			e2.printStackTrace();
			return null;
		}
		return stringCollection;
	}
	
	public String getAllTextForXPath(Node root, String _xpath) {
		Collection<String> stringCollection = getStringCollectionForXPath(root, _xpath);
		String content = "";
		for ( String s: stringCollection ) {
			content += s;
		}
		return content;
	}

	public Vector getNodeVecForXPath(Node node, String _xpath) {

		Vector retStringVec = new Vector();
		try {
			// Use the simple _xpath API to select a node.
			NodeList nl = XPathAPI.selectNodeList(node, _xpath);

			int n = nl.getLength();
			for (int i = 0; i < n; i++) {
				retStringVec.addElement(nl.item(i));
				// if(Trace.enabled && Trace.on(this) ) Trace.out.println(
				// nl.item(i) );
			}
		} catch (Exception e2) {
			System.err.println("selectNodeList threw: " + e2.toString()
					+ " perhaps your _xpath didn't select any nodes");
			e2.printStackTrace();
			return null;
		}
		return retStringVec;
	}

	public Node getNodeForXPath(Node node, String _xpath) {

		try {
			// Use the simple _xpath API to select a node.
			return XPathAPI.selectSingleNode(node, _xpath);

		} catch (Exception e2) {
			System.err.println("getNodeForXPath threw: " + e2.toString()
					+ " perhaps your _xpath didn't select any nodes");
			e2.printStackTrace();
			return null;
		}
	}

	public String getStringValFromXPath(Node node, String _xpath) {
		Node n = getNodeForXPath(node, _xpath);
		if (n != null)
			return n.getNodeValue();
		return null;
	}

	public float getFloatValFromXPath(Node node, String fromPath) {
		String sval = ApplyXPath.i().getStringValFromXPath(node, fromPath);
		if (sval == null)
			sval = "0";
		return new Float(sval).floatValue();
	}

	public double getDoubleValFromXPath(Node node, String fromPath) {
		String sval = ApplyXPath.i().getStringValFromXPath(node, fromPath);
		if (sval == null)
			sval = "0";
		return new Double(sval).doubleValue();
	}

	public int getIntValFromXPath(Node node, String fromPath) {
		String sval = ApplyXPath.i().getStringValFromXPath(node, fromPath);
		if (sval == null)
			sval = "0";
		return new Integer(sval).intValue();
	}

	/*
	 * public int getObjectValFromXPath( Node node, String fromPath ) { String
	 * sval = ApplyXPath.i().getStringValFromXPath( node, fromPath ); return
	 * new Integer( sval ).intValue(); }
	 */
	public void transform(StreamSource xmlSource, StreamSource xslSource, StreamResult streamResult)
			throws TransformerException, TransformerConfigurationException {

		TransformerFactory tfactory = TransformerFactory.newInstance();

		// The following line would be necessary if the stylesheet contained
		// an xsl:include or xsl:import with a relative URL
		// xslSource.setSystemId(xslID);

		// Create a transformer for the stylesheet.
		Transformer transformer = tfactory.newTransformer(xslSource);

		transformer.transform(xmlSource, streamResult);
	}

	public void removeAllText(Element element) {
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				element.removeChild(node);
			}
		}
	}

	/*
	 * public void printPath(Document doc, String _xpath) { if (doc == null)
	 * return; Node root = doc.getDocumentElement(); NodeList nl = null; try {
	 * nl = XPathAPI.selectNodeList(root, _xpath);
	 * 
	 * FormatterToXML fl =
	 * new FormatterToXML(System.out); TreeWalker tw =
	 * new TreeWalker(fl); int n = nl.getLength();
	 * for (int i = 0; i < n; i++) { tw.traverse(nl.item(i)); fl.flush();
	 * fl.flushWriter(); } } catch (Exception e2) {
	 * System.err.println("selectNodeList threw: " + e2.toString() + " perhaps
	 * your _xpath didn't select any nodes"); e2.printStackTrace(); return; } }
	 * 
	 * public void printNode(Node n) { printNode(n, System.out); }
	 * 
	 * public void printNode(Node n, File f) { try { FileOutputStream fos =
	 * new FileOutputStream(f); printNode(n, fos);
	 * fos.close(); } catch (IOException e) { e.printStackTrace(); } }
	 */
	public void printNode(Document d, File f) {
		try {
			FileOutputStream fos = new FileOutputStream(f);
			printNode(d, fos);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printToStdOut(Document d) {
		StringWriter sw = new StringWriter();
		XMLUtils.PrettyDocumentToWriter(d, sw);
		System.out.println(sw);
	}

	public String normalizeText(String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	public String docToString(Document d) {
		StringWriter sw = new StringWriter();
		XMLUtils.PrettyDocumentToWriter(d, sw);
		return sw.toString();
	}

	public int getAttributeValueAsNumber(Element element, String attribute, int defaultValue) {
		String attrValue = element.getAttribute(attribute);
		if (Util.i().hasContent(attrValue)) {
			return Integer.parseInt(attrValue);
		}
		return defaultValue;
	}

	/** Process input args and execute the XPath. */
	public void doMain2(String filename, String xpath) throws Exception {

		if ((filename != null) && (filename.length() > 0) && (xpath != null) && (xpath.length() > 0)) {
			// Tell that we're loading classes and parsing, so the time it
			// takes to do this doesn't get confused with the time to do
			// the actual query and serialization.
			System.out.println("Loading classes, parsing " + filename + ", and setting up serializer");

			// Set up a DOM tree to query.
			InputSource in = new InputSource(new FileInputStream(filename));
			DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
			dfactory.setNamespaceAware(true);
			Document doc = dfactory.newDocumentBuilder().parse(in);

			// Set up an identity transformer to use as serializer.
			Transformer serializer = TransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			// Use the simple XPath API to select a nodeIterator.
			System.out.println("Querying DOM using " + xpath);

			Element element = (Element) XPathAPI.selectSingleNode(doc, xpath);

			NodeIterator nl = XPathAPI.selectNodeIterator(element, "./tbody/tr");

			// Serialize the found nodes to System.out.
			System.out.println("<output>");

			Node n;
			while ((n = nl.nextNode()) != null) {
				if (isTextNode(n)) {
					// DOM may have more than one node corresponding to a
					// single XPath text node. Coalesce all contiguous text
					// nodes
					// at this level
					StringBuffer sb = new StringBuffer(n.getNodeValue());
					for (Node nn = n.getNextSibling(); isTextNode(nn); nn = nn.getNextSibling()) {
						sb.append(nn.getNodeValue());
					}
					System.out.print(sb);
				} else {
					serializer.transform(new DOMSource(n), new StreamResult(new OutputStreamWriter(System.out)));
				}
				System.out.println();
			}
			System.out.println("</output>");
		} else {
			System.out.println("Bad input args: " + filename + ", " + xpath);
		}
	}

	/** Decide if the node is text, and so must be handled specially */
	static boolean isTextNode(Node n) {
		if (n == null)
			return false;
		short nodeType = n.getNodeType();
		return nodeType == Node.CDATA_SECTION_NODE || nodeType == Node.TEXT_NODE;
	}

	public int[] getTableDimensions(Element tableElement) {

		int[] dimentions = new int[2];
		Collection<Element> trList = getElementChildren(tableElement, "tr");
		if (trList == null || trList.size() == 0) {
			Element tbodyElement = getFirstElementChildWithName(tableElement, "tbody");
			trList = getElementChildren(tbodyElement, "tr");
		}

		if (trList != null && trList.size() > 0) {

			int numRows = trList.size();
			int numCols = 0;

			for ( Element trElement: trList ) {

				Collection<Element> tdList = getElementChildren(trElement, "td");

				if (tdList != null && tdList.size() > 0) {
					System.out.println(tdList);

					int nCols = 0;

					for ( Element tdElement: tdList ) {
						int rs = getAttributeValueAsNumber(tdElement, "rowspan", 1);
						int cs = getAttributeValueAsNumber(tdElement, "colspan", 1);

						nCols += cs;
						// System.out.print("r: " + rs );
						// System.out.println(" c: " + cs);
					}
					// System.out.println(" ncols: " + nCols);
					numCols = Math.max(nCols, numCols);
				}
			}

			// System.out.println(" numRows: " + numRows);
			// System.out.println(" numCols: " + numCols);
			dimentions[0] = numRows;
			dimentions[1] = numCols;
		}

		return dimentions;
	}

	public int[] oldgetTableDimensions(Element tableElement) {

		int[] dimentions = new int[2];

		System.out.println("ttable: <>!@=:  " + tableElement.getTagName());
		Collection<Element> trList = getNodeCollectionForXPath(tableElement, "./TR");
		NodeList nodeList = null;

		try {
			nodeList = XPathAPI.selectNodeList(tableElement, "./TR");
			pnl(nodeList);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			nodeList = XPathAPI.selectNodeList(tableElement, "./TBODY/TR");
			pnl(nodeList);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (trList == null || trList.size() == 0) {
			Element tbodyElement = getFirstElementChildWithName(tableElement, "tbody");
			if (trList == null) {
				trList = getNodeCollectionForXPath(tbodyElement, "./tr");

				if (trList == null || trList.size() == 0) {
					trList = getElementChildren(tbodyElement, "tr");
				}
			}
		}

		if (trList != null && trList.size() > 0) {

			int numRows = trList.size();
			int numCols = 0;

			for ( Element trElement: trList ) {

				Collection<Element> tdList = getElementChildren(trElement, "td");

				if (tdList != null && tdList.size() > 0) {
					System.out.println(tdList);

					int nCols = 0;

					for ( Element tdElement: tdList ) {
						int rs = getAttributeValueAsNumber(tdElement, "rowspan", 1);
						int cs = getAttributeValueAsNumber(tdElement, "colspan", 1);

						nCols += cs;
						// System.out.print("r: " + rs );
						// System.out.println(" c: " + cs);
					}
					// System.out.println(" ncols: " + nCols);
					numCols = Math.max(nCols, numCols);
				}
			}

			// System.out.println(" numRows: " + numRows);
			// System.out.println(" numCols: " + numCols);
			dimentions[0] = numRows;
			dimentions[1] = numCols;
		}

		return dimentions;
	}

	public boolean hasSubChild(Element element, String descendantTagName) {
		try {
			return XPathAPI.selectSingleNode(element, ".//" + descendantTagName) != null;
		} catch (TransformerException te) {
			return false;
		}

	}

	public boolean ppxmlStdOut(Node node) throws IOException {
		return ppxml(node, System.out, -1);
	}
	
	public String getStartRep(Node node){
		return getElementStartRep(node,false);
	}
	public String getElementStartRep(Node node, boolean isSimple){

		if (node instanceof Element) {
			Element element = (Element) node;
			String nodeRep = "";

			nodeRep += "<" + element.getTagName();

			NamedNodeMap attrs = element.getAttributes();
			if (attrs != null) {
				for (int i = 0; i < attrs.getLength(); i++) {
					Attr attr = (Attr) attrs.item(i);
					nodeRep += " " + attr.getName() + "=\"" + attr.getValue() + "\"";
				}
			}
			return isSimple ? nodeRep + "/>" : nodeRep + ">";
		}

		return "";
	}
	
	public String getEndRep(Node node) {
		if (node instanceof Element) {

			Element element = (Element) node;
			return "</" + element.getTagName() + ">";
		}
		return "";
	}
	
	public String getStringRep(Node node, int depth) {
		String rep = "";
		
		if (node instanceof Document) {
			Document doc = (Document) node;
			getStringRep(doc.getDocumentElement(), depth);
		}
		
		if (node instanceof Element) {
			Element element = (Element) node;
			String nodeRep = "\n";

			for (int i = 0; i < depth; i++) {
				nodeRep += "  ";
			}
			nodeRep += "<" + element.getTagName();

			NamedNodeMap attrs = element.getAttributes();
			if (attrs != null) {
				for (int i = 0; i < attrs.getLength(); i++) {
					Attr attr = (Attr) attrs.item(i);
					nodeRep += " " + attr.getName() + "=\"" + attr.getValue() + "\"";
				}
			}

			NodeList children = element.getChildNodes();
			if (children != null) {
				boolean hadChildElement = false;
				nodeRep += ">";
				rep += nodeRep;
				for (int i = 0; i < children.getLength(); i++) {
					rep += getStringRep(children.item(i), depth + 1);
				}
				
				if (hadChildElement) {
					nodeRep = "\n";
					for (int i = 0; i < depth; i++) {
						nodeRep += "  ";
					}
				} else {
					nodeRep = "";
				}
				nodeRep += "</" + element.getTagName() + ">";
				rep += nodeRep;

			} else {
				nodeRep += "/>";
				rep += nodeRep;
			}

		}

		if (node instanceof Text) {
			Text text = (Text) node;

			rep += text.getData();
		}
		return rep;
	}

	public boolean ppxml(Node node, OutputStream os, int depth) throws IOException {
		boolean isElement = false;
		if (node instanceof Document) {
			Document doc = (Document) node;
			ppxml(doc.getDocumentElement(), os, depth);
		}
		if (node instanceof Element) {
			isElement = true;
			Element element = (Element) node;
			String nodeRep = "\n";

			for (int i = 0; i < depth; i++) {
				nodeRep += "  ";
			}
			nodeRep += "<" + element.getTagName();

			NamedNodeMap attrs = element.getAttributes();
			if (attrs != null) {
				for (int i = 0; i < attrs.getLength(); i++) {
					Attr attr = (Attr) attrs.item(i);
					nodeRep += " " + attr.getName() + "=\"" + attr.getValue() + "\"";
				}
			}

			NodeList children = element.getChildNodes();
			if (children != null) {
				boolean hadChildElement = false;
				nodeRep += ">";
				os.write(nodeRep.getBytes());
				for (int i = 0; i < children.getLength(); i++) {
					hadChildElement |= ppxml(children.item(i), os, depth + 1);
				}
				if (hadChildElement) {
					nodeRep = "\n";
					for (int i = 0; i < depth; i++) {
						nodeRep += "  ";
					}
				} else {
					nodeRep = "";
				}
				nodeRep += "</" + element.getTagName() + ">";
				os.write(nodeRep.getBytes());

			} else {
				nodeRep += "/>";
				os.write(nodeRep.getBytes());
			}

		}

		if (node instanceof Text) {
			Text text = (Text) node;

			os.write(text.getData().getBytes());
		}
		return isElement;
	}

	public boolean ppxml(Node node, Writer writer, int depth) throws IOException {
		boolean isElement = false;
		if (node instanceof Document) {
			Document doc = (Document) node;
			ppxml(doc.getDocumentElement(), writer, depth);
		}
		if (node instanceof Element) {
			isElement = true;

			Element element = (Element) node;
			String nodeRep = "\n";

			for (int i = 0; i < depth; i++) {
				nodeRep += "  ";
			}
			nodeRep += "<" + element.getTagName();

			NamedNodeMap attrs = element.getAttributes();
			if (attrs != null) {
				for (int i = 0; i < attrs.getLength(); i++) {
					Attr attr = (Attr) attrs.item(i);
					nodeRep += " " + attr.getName() + "=\"" + attr.getValue() + "\"";
				}
			}

			NodeList children = element.getChildNodes();
			if (children != null) {
				boolean hadChildElement = false;
				nodeRep += ">";
				writer.write(nodeRep);
				for (int i = 0; i < children.getLength(); i++) {
					hadChildElement |= ppxml(children.item(i), writer, depth + 1);
				}
				if (hadChildElement) {
					nodeRep = "\n";
					for (int i = 0; i < depth; i++) {
						nodeRep += "  ";
					}
				} else {
					nodeRep = "";
				}
				nodeRep += "</" + element.getTagName() + ">";
				writer.write(nodeRep);

			} else {
				nodeRep += "/>";
				writer.write(nodeRep);
			}

		}

		if (node instanceof Text) {
			Text text = (Text) node;

			writer.write(text.getData());
		}
		return isElement;
	}

	/*
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?> <!DOCTYPE hibernate-mapping PUBLIC
	 * "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
	 * "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
	 * 
	 * <hibernate-mapping>
	 */
	public void printNode(Document d, OutputStream outputStream) throws IOException {
		DocumentType documentType = d.getDoctype();
		if (documentType != null) {
			String docString = XMLUtils.DocumentToString(d);
			String rootElemTagName = d.getDocumentElement().getTagName();
			// TODO HACK: we parse out the prefix and stuff the doctype in
			// manually
			String[] hackElements = docString.split("<");

			String docTypeSystemId = documentType.getSystemId();
			String docTypePublicId = documentType.getPublicId();

			String startXML = hackElements[0] + "<" + hackElements[1] + "\n<!DOCTYPE " + rootElemTagName + " PUBLIC \""
					+ docTypePublicId + "\"\n     \"" + docTypeSystemId + "\">\n<" + hackElements[2];

			StringBuffer theRest = new StringBuffer();
			// some sort if indent at least
			for (int i = 3; i < hackElements.length; i++) {
				if (i < hackElements.length - 1) {
					theRest.append("\n    <" + hackElements[i]);
				} else {
					theRest.append("\n<" + hackElements[i] + "\n");
				}
			}
			System.out.println("totalX: " + startXML + theRest.toString());
			FileUtils.i().writeContents(outputStream, startXML + theRest.toString());

		} else {
			XMLUtils.PrettyDocumentToStream(d, outputStream);
		}
	}

	// public void printNode(Node n, OutputStream outputStream) {
	// try {
	// // Use the FormatterToXML class right not instead of
	// // the Xerces Serializer classes, because I'm not sure
	// // yet how to make them handle arbitrary nodes.
	// FormatterToXML fl =
	// new FormatterToXML(outputStream);
	// TreeWalker tw = new TreeWalker(fl);
	// tw.traverse(n);
	// // We have to do both a flush and a flushWriter here,
	// // because the FormatterToXML rightly does not flush
	// // until it get's an endDocument, which usually will
	// // not happen here.
	// fl.flush();
	// // fl.flus();
	// } catch (Exception e2) {
	// System.err.println("selectNodeList threw: " + e2.toString()
	// + " perhaps your _xpath didn't select any nodes");
	// e2.printStackTrace();
	// return;
	// }
	// }

	public String justGetIt() {
		// Vector v =
		// return getStringValFromXPath ( getDocFromStringXML(
		// TestXMLStrings.dataXML ), "/dataset/row/column/@name" );
		return null;
	}

	/** Process input args and execute the XPath. */
	public void doMain(String[] args) throws Exception {
		if (args.length == 0) {
			if (Tracer.on && Tracer.on("org.maxml.ApplyXPath", "justGetIt"))
				Tracer.out(justGetIt());
			// printPath( getDocFromStringXML( TestXMLStrings.dataXML ),
			// "/dataset/row/column/@name" );
		} else {
			String _filename = args[0];
			String _xpath = args[1];

			if ((_filename != null) && (_filename.length() > 0) && (_xpath != null) && (_xpath.length() > 0)) {
				if (Tracer.on && Tracer.on("org.maxml.ApplyXPath", "justGetIt"))
					Tracer.out(_filename + " 00 " + _xpath);
				Element elem = (Element) ApplyXPath.i().getNodeForXPath(getDocFromFile(_filename).getDocumentElement(),
						_xpath);

				// printPath( getDocFromFile( _filename ), _xpath );
			}
		}
	}

	/** Main method to run from the command line. */
	public static void main(String[] args) throws Exception {
		if (true) {
			String inputXhtml = FileUtils.i().getFileContents("c:\\ttt.xml").toString();
			Document document = ApplyXPath.i().getDocFromStringXML(inputXhtml);

		} else {
			i().doMain(args);
			// ApplyXPath app = new ApplyXPath(args[0]);
			// Node specNode = app.getNode();
			// Document doc = (Document)app.getNode();

			// if(Trace.enabled && Trace.on("ApplyXPath") ) Trace.out.println(
			// "---->" + doc );
			// if(Trace.enabled && Trace.on("ApplyXPath") ) Trace.out.println(
			// app.getXPathNode( "/mapping-spec/export-desc" ).getNodeName() );
			// if(Trace.enabled && Trace.on("ApplyXPath") ) Trace.out.println(
			// app.getXPathString(
			// "/mapping-spec/export-desc/query/where/exp/binary-operator/text()"
			// )
			// );

			// String s[] = app.getXPathStrings(
			// "/mapping-spec/import-desc/query/field/@name" );

			// if(Trace.enabled && Trace.on("ApplyXPath") ) Trace.out.println(
			// "\napp.getXPathStrings:" );
			// for( int i = 0; i < s.length; ++i ) if(Trace.enabled &&
			// Trace.on("ApplyXPath") ) Trace.out.println( s[i] );

			// if(Trace.enabled && Trace.on("ApplyXPath") ) Trace.out.println(
			// "\napp.getXPathStringVector:" );
			// Vector v = app.getXPathStringVector(
			// "/mapping-spec/export-desc/query/field/@export-id" );
			// Enumeration e = v.elements();
			// while( e.hasMoreElements() ) if(Trace.enabled &&
			// Trace.on("ApplyXPath") ) Trace.out.println(
			// (String)e.nextElement()
			// );

			// if(Trace.enabled && Trace.on("ApplyXPath") ) Trace.out.println(
			// "\nwith node:" );
			// if(Trace.enabled && Trace.on("ApplyXPath") ) Trace.out.println(
			// "\napp.getXPathStringVector:" );
			// NodeList ni = app.getXPathNodeList(
			// "/mapping-spec/export-desc/query"
			// );
			// Node n = app.getXPathNode( "query", app.getXPathNode(
			// "/mapping-spec/export-desc" ) );
			// if(Trace.enabled && Trace.on("ApplyXPath") ) Trace.out.println( n
			// );

			// addXML(doc, n, "bob/joe/jim/text()", "gor" );
			// addXML(doc, n, "bob/joe/jack", null );
			// addXML(doc, n, "bob/joe/jack/@rrrr", "muggy" );
			// addXML(doc, n, "bob/joe/jack/text()", "asdkasdkfasdfksadfk" );
			// if(Trace.enabled && Trace.on("ApplyXPath") ) Trace.out.println( n
			// );

			// Transformer serializer =
			// TransformerFactory.newInstance().newTransformer();
			// serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
			// "yes");
			// serializer.transform(new DOMSource(n),
			// new StreamResult(System.out));

			// app.doMain(args);
		}
	}

} // end of class ApplyXPath

