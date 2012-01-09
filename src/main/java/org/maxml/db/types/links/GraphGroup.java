package org.maxml.db.types.links;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.maxml.db.DBException;
import org.maxml.db.types.TypeSet;
import org.maxml.util.Util;

public class GraphGroup {

	protected LinkHandler	linkHandler;
	private Integer			keyReferrerType;
	protected HashMap<Link, LinkGraph>		key2LinkGraphMap;

	public GraphGroup(Integer keyReferrerType) throws DBException {
		this.keyReferrerType = keyReferrerType;
		linkHandler = LinkHandler.getInstance();
		key2LinkGraphMap = new HashMap();
		if (keyReferrerType != null) {
			readKey2LinkGraphMap();
		}
	}

	public void readKey2LinkGraphMap() throws DBException {
		try {
            Collection<Link> keys = linkHandler.getLinksByReferrerType(keyReferrerType);
            for ( Link refLink: keys ) {

            	Link linkHierachyRootLink = (Link) linkHandler.getReferredObject(refLink.getReferentId(), Link.class);

                LinkGraph linkGraph = GraphReader.read(linkHierachyRootLink);
            	
            	key2LinkGraphMap.put(refLink, linkGraph);

            	RegistryOfGraphs.getInstance().add(refLink, linkGraph);
            }
        } catch (OldVisitException e) {
            throw new DBException(e);
        }
	}

	public Collection getSuperSets(LinkGraph linkGraph, boolean checkTypesFirst) {
		return getSuperSets(linkGraph, checkTypesFirst, false);
	}

	public Collection getSuperSets(LinkGraph linkGraph, boolean checkTypesFirst, boolean getKeys) {
		List superSets = new ArrayList();
		Set targetSet = linkGraph.getReferrentSet();
		TypeSet targetTypeSet = linkGraph.getTypeSet();

		for ( Link refLink: key2LinkGraphMap.keySet() ) {
			LinkGraph nextLinkGraph = (LinkGraph) key2LinkGraphMap.get(refLink);

			if (checkTypesFirst && !nextLinkGraph.getTypeSet().containsAll(targetTypeSet)) {
				continue;
			}
//            linkHandler.printLinks(targetSet);
//            linkHandler.printLinks(nextLinkGraph.getReferrentSet());
            System.out.println("link: " + refLink.getId());
            Util.i().printList(targetSet);
            Util.i().printList(nextLinkGraph.getReferrentSet());
			if (nextLinkGraph.getReferrentSet().containsAll(targetSet)) {
				superSets.add(getKeys ? refLink : nextLinkGraph);
                System.out.println("\nmatches");
			}
            System.out.println();
            System.out.println();
		}
		return superSets;
	}

	public Collection getSubSets(LinkGraph linkGraph, boolean checkTypesFirst) {
		return getSubSets(linkGraph, checkTypesFirst, false);
	}

    public Collection getSubSets(LinkGraph linkGraph, boolean checkTypesFirst, boolean getKeys) {
        List subSets = new ArrayList();
        Set targetSet = linkGraph.getReferrentSet();
        TypeSet targetTypeSet = linkGraph.getTypeSet();

        for ( Link refLink: key2LinkGraphMap.keySet() ) {
            LinkGraph nextLinkGraph = (LinkGraph) key2LinkGraphMap.get(refLink);

			if (checkTypesFirst && !Util.i().hasOneOrMoreIncommon(nextLinkGraph.getTypeSet(), targetTypeSet)) {
				continue;
			}
			if (Util.i().hasOneOrMoreIncommon(nextLinkGraph.getReferrentSet(), targetSet)) {
				subSets.add(getKeys ? refLink : nextLinkGraph);
			}
		}
		return subSets;
	}
    
    public LinkGraph findGraphByReferent(Integer referentId, Integer referentType ) {
        Link findLink = new Link(referentId, referentType , null, null);
        for ( Link refLink: key2LinkGraphMap.keySet() ) {
            if( refLink.sameReferent(findLink) ) {
                return (LinkGraph) key2LinkGraphMap.get(refLink);
            }
        }
        return null;
    }
    public LinkGraph findGraphByRootId(Integer rootId ) {
    
        for ( Link refLink: key2LinkGraphMap.keySet() ) {
            if( refLink.getId().equals( rootId ) ) {
                return (LinkGraph) key2LinkGraphMap.get(refLink);
            }
        }
        return null;
    }

	public void add(Link refLink, LinkGraph linkGraph) {
		key2LinkGraphMap.put(refLink, linkGraph);
		RegistryOfGraphs.getInstance().add(refLink, linkGraph);
	}

	public void remove(Link refLink) {
		RegistryOfGraphs.getInstance().remove(refLink);
	}

	protected void removeHelper(Link refLink) {
		// TODO this is a work around since hashmap does not call equals and treemap (that should be used) gives a class cast
		// exception 

		Link keyLink = null;
		for ( Link kl: key2LinkGraphMap.keySet() ) {
			if (kl.equals(refLink)) {
				keyLink = kl;
				break;
			}
		}
		if (keyLink != null) {
			key2LinkGraphMap.remove(keyLink);
		}
	}
	

}
