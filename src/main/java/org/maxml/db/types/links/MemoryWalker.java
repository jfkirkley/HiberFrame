package org.maxml.db.types.links;

import java.util.Collection;
import java.util.Iterator;

public class MemoryWalker extends OldWalker {

    public MemoryWalker(LinkGraph linkGraph) {
        super(linkGraph);
    }

    public void traverse() throws OldVisitException {
        traverseDepthFirst(this.linkGraph);
    }
    
    public void traverseBreadthFirst() throws OldVisitException {
        traverseBreadthFirst(this.linkGraph);
    }
    
    public void traverseDepthFirst() throws OldVisitException {
        traverseDepthFirst(this.linkGraph);
    }

    public void traverseDepthFirst(LinkGraph linkGraph) throws OldVisitException {
        doVisit(linkGraph);

        for (Iterator iter = linkGraph.getChildren().iterator(); iter.hasNext();) {
            Object obj = iter.next();

            if (obj instanceof Link) {
                Link link = (Link) obj;
                doVisit(link);
            } else if (obj instanceof LinkGraph) {
                LinkGraph sublinkGraph = (LinkGraph) obj;
                traverseDepthFirst(sublinkGraph);
            }
        }
    }

    public void traverseBreadthFirst(LinkGraph linkGraph) throws OldVisitException {
        doVisit(linkGraph);
        boolean hasSubGraphs = false;
        for (Iterator iter = linkGraph.getChildren().iterator(); iter.hasNext();) {
            Object obj = iter.next();

            if (obj instanceof Link) {
                Link link = (Link) obj;
                doVisit(link);
            } else {
                hasSubGraphs = true;
            }
        }

        if (hasSubGraphs) {
            for (Iterator iter = linkGraph.getChildren().iterator(); iter.hasNext();) {
                Object obj = iter.next();
                if (obj instanceof LinkGraph) {
                    LinkGraph sublinkGraph = (LinkGraph) obj;
                    traverseBreadthFirst(sublinkGraph);
                }
            }
        }
    }

    public static void walk(LinkGraph linkGraph, OldVisitor visitor) throws OldVisitException {
        OldWalker walker = new MemoryWalker(linkGraph);
        walker.addVisitor(visitor);
        walker.traverse();
    }
    
    public static void walk(LinkGraph linkGraph, Collection visitors) throws OldVisitException {
        OldWalker walker = new MemoryWalker(linkGraph);
        walker.addVisitors(visitors);
        walker.traverse();
    }

    public static void walkDepthFirst(LinkGraph linkGraph, OldVisitor visitor) throws OldVisitException {
        OldWalker walker = new MemoryWalker(linkGraph);
        walker.addVisitor(visitor);
        walker.traverseDepthFirst();
    }
    
    public static void walkDepthFirst(LinkGraph linkGraph, Collection visitors) throws OldVisitException {
        OldWalker walker = new MemoryWalker(linkGraph);
        walker.addVisitors(visitors);
        walker.traverseDepthFirst();
    }

    public static void walkBreadthFirst(LinkGraph linkGraph, OldVisitor visitor) throws OldVisitException {
        OldWalker walker = new MemoryWalker(linkGraph);
        walker.addVisitor(visitor);
        walker.traverseBreadthFirst();
    }
    
    public static void walkBreadthFirst(LinkGraph linkGraph, Collection visitors) throws OldVisitException {
        OldWalker walker = new MemoryWalker(linkGraph);
        walker.addVisitors(visitors);
        walker.traverseBreadthFirst();
    }
    
}
