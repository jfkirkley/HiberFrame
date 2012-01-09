package org.maxml.db.types.links;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class OldWalker {
    protected LinkHandler linkHandler = LinkHandler.getInstance();

    protected LinkGraph     linkGraph;
    protected Collection<OldVisitor>    visitors;

    public OldWalker(LinkGraph linkGraph) {
        this.linkGraph = linkGraph;
        this.visitors = new ArrayList();
    }

    public void addVisitor(OldVisitor visitor) {
        visitors.add(visitor);
    }
    
    public void addVisitors(Collection visitors) {
        visitors.addAll(visitors);
    }

    public abstract void traverse() throws OldVisitException;
    public abstract void traverseBreadthFirst() throws OldVisitException;
    public abstract void traverseDepthFirst() throws OldVisitException;

    protected void doVisit(Link link) throws OldVisitException {
        for ( OldVisitor visitor: visitors ) {
            visitor.visit(link);
        }
    }

    protected void doVisitParent(Link link) throws OldVisitException {
        for ( OldVisitor visitor: visitors ) {
            visitor.visitParent(link);
        }
    }

    protected void doVisit(LinkGraph linkGraph) throws OldVisitException {
        for ( OldVisitor visitor: visitors ) {
            visitor.visit(linkGraph);
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
