package org.maxml.db.types.links;

public class ExtensionLinkGraph extends LinkGraph {

    public ExtensionLinkGraph(Link link) {
        super(link);
        link.setType(Link.EXT);
    }
    
}
