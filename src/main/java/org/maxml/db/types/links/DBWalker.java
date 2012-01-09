package org.maxml.db.types.links;

import java.util.Collection;
import java.util.Iterator;

import org.maxml.common.VisitException;
import org.maxml.db.DBException;

public class DBWalker extends OldWalker {
    protected LinkHandler linkHandler = LinkHandler.getInstance();


    public DBWalker(LinkGraph linkGraph) {
        super(linkGraph);
    }

    public void traverse() throws OldVisitException {
        traverseDepthFirst();
    }
    
    public void traverseBreadthFirst() throws OldVisitException {
        doVisit(linkGraph.getRootLink());
        traverseBreadthFirst(linkGraph.getRootLink());
    }
    
    public void traverseDepthFirst() throws OldVisitException {
        doVisit(linkGraph.getRootLink());
        traverseDepthFirst(linkGraph.getRootLink());
    }
    
    public void traverseDepthFirst(Link rootLink) throws OldVisitException {

        try {
            Collection<Link> subLinks = linkHandler.getLinks(rootLink.getId(), Link.getTypeId());

            if (subLinks != null && subLinks.size() > 0) {
                doVisitParent(rootLink);
                
                for ( Link link: subLinks ) {

                    doVisit(link);
   
                    traverseDepthFirst(link);
                }
            }
        } catch (DBException e) {
            throw new OldVisitException(e);
        }
    }

    public void traverseBreadthFirst(Link rootLink) throws OldVisitException{

        try {
            Collection<Link> subLinks = linkHandler.getLinks(rootLink.getId(), Link.getTypeId());

            if (subLinks != null && subLinks.size() > 0) {
                for ( Link link: subLinks ) {

                    doVisit(link);
                }
                for ( Link link: subLinks ) {

                    traverseBreadthFirst(link);
                }
            }
        } catch (DBException e) {
            throw new OldVisitException(e);
        } 
    }

    public static void walk(LinkGraph linkGraph, OldVisitor visitor) throws OldVisitException {
        OldWalker walker = new DBWalker(linkGraph);
        walker.addVisitor(visitor);
        walker.traverse();
    }
    
    public static void walk(LinkGraph linkGraph, Collection visitors) throws OldVisitException {
        OldWalker walker = new DBWalker(linkGraph);
        walker.addVisitors(visitors);
        walker.traverse();
    }

    public static void walkDepthFirst(LinkGraph linkGraph, OldVisitor visitor) throws OldVisitException {
        OldWalker walker = new DBWalker(linkGraph);
        walker.addVisitor(visitor);
        walker.traverseDepthFirst();
    }
    
    public static void walkDepthFirst(LinkGraph linkGraph, Collection visitors) throws OldVisitException {
        OldWalker walker = new DBWalker(linkGraph);
        walker.addVisitors(visitors);
        walker.traverseDepthFirst();
    }

    public static void walkBreadthFirst(LinkGraph linkGraph, OldVisitor visitor) throws OldVisitException {
        OldWalker walker = new DBWalker(linkGraph);
        walker.addVisitor(visitor);
        walker.traverseBreadthFirst();
    }
    
    public static void walkBreadthFirst(LinkGraph linkGraph, Collection visitors) throws OldVisitException {
        OldWalker walker = new DBWalker(linkGraph);
        walker.addVisitors(visitors);
        walker.traverseBreadthFirst();
    }
    
}
