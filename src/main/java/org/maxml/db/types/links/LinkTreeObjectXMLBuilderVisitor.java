package org.maxml.db.types.links;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.maxml.common.VisitException;
import org.maxml.common.Visitor;
import org.maxml.common.Walker;

import org.maxml.reflect.CachedClass;
import org.maxml.reflect.MapOfReflectionNavigationFilter;
import org.maxml.util.Util;


public class LinkTreeObjectXMLBuilderVisitor implements Visitor {

    protected Document                        doc;
    protected Element                         currentElement;
    protected Element                         newElement = null;
    
    protected Stack<Element>                  elementStack;
    protected MapOfReflectionNavigationFilter mapOfReflectionNavigationFilter;
    protected boolean newParent = true;
    
    protected Walker walker; 
    

    public LinkTreeObjectXMLBuilderVisitor() {
        elementStack = new Stack<Element>();
    }

    public LinkTreeObjectXMLBuilderVisitor(Element rootElement,
            MapOfReflectionNavigationFilter mapOfReflectionNavigationFilter) {
        this();
        this.currentElement = rootElement;
        this.elementStack.push(rootElement);
        this.doc = currentElement.getOwnerDocument();
        this.mapOfReflectionNavigationFilter = mapOfReflectionNavigationFilter;
    }

    public void visit(Object target) throws VisitException {
        try {
            this.mapOfReflectionNavigationFilter.setCurrentContextType(target.getClass());
            newElement = (Element)CachedClass.getXMLRepresentation(currentElement.getOwnerDocument(), target,
                    mapOfReflectionNavigationFilter);

            newElement.setAttribute("linked", "true");            
            
            currentElement.appendChild(newElement);

        } catch (Throwable e) {
            throw new VisitException(e);
        }
    }

    public void preVisitParent(Object parent) throws VisitException {

    	//System.out.println("p" + Util.i().buildIndent(this.walker.getDepth()) + currentElement.getTagName());
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
