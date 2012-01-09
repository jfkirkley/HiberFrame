package org.maxml.db.types.links;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.maxml.common.VisitException;
import org.maxml.common.Visitor;
import org.maxml.common.Walker;


public class LinkTreeXMLBuilderVisitor implements Visitor {

    protected Document                        doc;
    protected Element                         currentElement;
    protected Element                         newElement = null;
    
    protected Stack<Element>                  elementStack;
    protected boolean newParent = true;
    
    protected Walker walker; 
    

    public LinkTreeXMLBuilderVisitor() {
        elementStack = new Stack<Element>();
    }

    public LinkTreeXMLBuilderVisitor(Element rootElement) {
    	this();
        this.currentElement = rootElement;
        this.doc = rootElement.getOwnerDocument();
    }


    public void visit(Object target) throws VisitException {
        try {
        	Link link = (Link)target;
        	newElement = link.getXMLRep(currentElement.getOwnerDocument());
            currentElement.appendChild(newElement);
        } catch (Throwable e) {
            throw new VisitException(e);
        }
    }

    public void preVisitParent(Object parent) throws VisitException {

        elementStack.push(currentElement);
        currentElement = newElement;
    }

    public void postVisitParent(Object parent) throws VisitException {
        if (!elementStack.empty()) {
            currentElement = elementStack.pop();
        }
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

	public Walker getWalker() {
		return walker;
	}

	public void setWalker(Walker walker) {
		this.walker = walker;
	}




}
