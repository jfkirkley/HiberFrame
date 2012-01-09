package org.maxml.db.types.links;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

public class GraphReader implements OldVisitor {

    private LinkGraph rootLinkGraph    = null;
    private LinkGraph currentLinkGraph = null;
    private Link      lastLink         = null;
    private Stack<LinkGraph>     graphStack;
    private ArrayList<LinkGraph> extLinkGraphs;
    
    public GraphReader() {
        this.graphStack = new Stack<LinkGraph>();
        this.extLinkGraphs = new ArrayList();
    }

    public void visit(Link link) throws OldVisitException {
        LinkGraph linkGraph = getCurrentLinkGraph(link);
        lastLink = link;
        linkGraph.addChildLink(link);
    }

    public void visit(LinkGraph linkGraph) throws OldVisitException {
    }

    public void visitParent(Link parentLink) throws OldVisitException {

        if (currentLinkGraph != null) {
            currentLinkGraph.removeChildLink(parentLink);
            LinkGraph linkGraph = new LinkGraph(currentLinkGraph, parentLink);
            if( ExtLink.isExtLink(parentLink) ) {
                extLinkGraphs.add(linkGraph);
            }
            graphStack.push(linkGraph);
            currentLinkGraph.addSubLinkGraph(linkGraph);
            currentLinkGraph = linkGraph;
            lastLink = null;
        } else {
            rootLinkGraph.removeChildLink(parentLink);
        }
    }

    public void expandExtGraphs() {
        for ( LinkGraph extLinkGraph: extLinkGraphs ) {
            rootLinkGraph.expandExtLink(extLinkGraph);
        }
    }
    
    private LinkGraph getCurrentLinkGraph(Link link) {
        if (rootLinkGraph == null) {
            rootLinkGraph = new LinkGraph(link);
            graphStack.push(rootLinkGraph);
            return rootLinkGraph;
        } 
        if( lastLink != null && !lastLink.sameReferrer(link)) {
            currentLinkGraph = graphStack.pop();
        }
        return currentLinkGraph;
    }

    public LinkGraph getRootLinkGraph() {
        return rootLinkGraph;
    }

    public static LinkGraph read(LinkGraph linkGraph) throws OldVisitException {
        GraphReader graphReader = new GraphReader();
        DBWalker.walk(linkGraph, graphReader);
        graphReader.expandExtGraphs();
        return graphReader.getRootLinkGraph();
    }
    
    public static LinkGraph read(Link link) throws OldVisitException {
        return read( new LinkGraph(link));
    }

    public static void read(LinkGraph linkGraph, OldVisitor visitor) throws OldVisitException {
        GraphReader graphReader = new GraphReader();
        ArrayList visitors = new ArrayList();
        visitors.add(graphReader);
        visitors.add(visitor);
        DBWalker.walk(linkGraph, visitors);
    }

    public static void read(LinkGraph linkGraph, Collection visitorList) throws OldVisitException {
        GraphReader graphReader = new GraphReader();
        ArrayList visitors = new ArrayList();
        visitors.add(graphReader);
        visitors.addAll(visitorList);
        DBWalker.walk(linkGraph, visitors);
    }

}
