package org.maxml.xpath;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

//import com.sun.org.apache.xpath.internal.XPathAPI;

public class XPathQuery {

	public final static int	SINGLE		= 0;
	public final static int	MULTIPLE	= 1;
	public final static int	ITERATOR	= 2;

	Element					contextNode;
	String					xpath;
	int						queryType;

	public XPathQuery(String xpath) {
		this(xpath, MULTIPLE);
	}

	public XPathQuery(String xpath, int queryType) {
		this.queryType = queryType;
		this.xpath = xpath;
	}

	public XPathQuery(Element contextNode, String xpath, int queryType) {
		this(xpath, queryType);
		this.contextNode = contextNode;
	}

//	public Node single() throws TransformerException {
//		return XPathAPI.selectSingleNode(contextNode, xpath);
//	}
//
//	public NodeList multiple() throws TransformerException {
//		return XPathAPI.selectNodeList(contextNode, xpath);
//	}
//
//	public NodeIterator iterator() throws TransformerException {
//		return XPathAPI.selectNodeIterator(contextNode, xpath);
//		
//	}

	public int getQueryType() {
		return queryType;
	}

	public Element getContextNode() {
		return contextNode;
	}

	public void setContextNode(Element contextNode) {
		this.contextNode = contextNode;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	
	public boolean isSingle() {
		return this.queryType == SINGLE;
	}

	public boolean isMultiple() {
		return this.queryType == MULTIPLE;
	}

}
