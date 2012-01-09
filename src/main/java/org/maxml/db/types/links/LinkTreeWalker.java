package org.maxml.db.types.links;

import java.util.Collection;

import org.w3c.dom.Document;

import org.maxml.common.AbstractWalker;
import org.maxml.common.VisitException;
import org.maxml.common.Visitor;
import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.xpath.ApplyXPath;

public class LinkTreeWalker extends AbstractWalker {
	
	protected LinkHandler	linkHandler	= LinkHandler.getInstance();

	public LinkTreeWalker(Object root) {
		super(root);
	}


	public void traverse() throws VisitException {
	    super.traverseBreadthFirst();
	}
	

	public Collection getChildren(Object obj) throws VisitException {
	    try {
	        return linkHandler.getLinksByReferent(//(Link)obj);
	        		
	        		(Integer) DBObjectAccessorFactory.i().getObjectId(obj),
	                DBObjectAccessorFactory.i().getTypeIdForClass(obj.getClass()));
	    } catch (DBException e) {
	        throw new VisitException(e);
	    }
	}
	

	public static Document genXML(Object root) throws VisitException {
		Document doc = ApplyXPath.i().getNewDoc();
		doc.appendChild(doc.createElement("links"));
    	LinkTreeXMLBuilderVisitor linkTreeXMLBuilderVisitor = new LinkTreeXMLBuilderVisitor(doc.getDocumentElement());
    	walk(root, linkTreeXMLBuilderVisitor);
    	
    	return linkTreeXMLBuilderVisitor.getDoc();
    }
    
    public static void walk(Object root, Visitor visitor) throws VisitException {
        LinkTreeWalker linkTreeWalker = new LinkTreeNameMapBuilderWalker(root);
        linkTreeWalker.addVisitor(visitor);
        linkTreeWalker.traverse();
    }
}
