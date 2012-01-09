package org.maxml.db.types.links;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessor;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.DBUtils;
import org.maxml.db.types.TypeSet;
import org.maxml.db.types.profiles.dProfile;
import org.maxml.reflect.CachedClass;
import org.maxml.reflect.ReflectCache;

public class LinkGraph {

	private Integer								id;
	private Link								rootLink;
	private LinkGraph							parentLinkGraph;
	protected LinkHandler						linkHandler				= LinkHandler.getInstance();
	protected LinkGraph							rootLinkGraph			= null;

	private transient Integer					rootId					= null;

	protected static DBObjectAccessorFactory	objectAccessorFactory	= DBObjectAccessorFactory.i();

	private Collection							children				= null;									// child
	// links
	// and
	// linkGraphs

	private TypeSet								typeSet					= null;

	// referrent set is optimized to contain just a normalized Long where l =
	// (type<<32|id)
	private HashSet								referrentSet			= null;

	public LinkGraph() {
		this.children = new ArrayList();
	}

	public LinkGraph(Link link) {
		this.children = new ArrayList();
		this.rootLink = link;
	}

	public LinkGraph(LinkGraph parent, Link link) {
		this(link);
		this.parentLinkGraph = parent;
	}

	public LinkGraph(Link link, Collection elements) {
		this.rootLink = link;
		this.children = elements;
		this.typeSet = new TypeSet();
	}

	public LinkGraph(Collection entityList, Link link) {
		this(link, new ArrayList());

		for (Iterator iter = entityList.iterator(); iter.hasNext();) {
			Object entity = iter.next();
			Link ref = new Link(entity, false);
			children.add(ref);

			typeSet.add(ref.getReferentType());
		}
	}

	public LinkGraph(Collection linkGraphList, Link rootLink, boolean isForSets) {
		this.typeSet = new TypeSet();
		this.rootLink = rootLink;

		populateSets(linkGraphList, rootLink);
	}

	public void populateSets(Collection linkGraphList, Link link) {
		this.referrentSet = new HashSet();
		this.typeSet = new TypeSet();
		addReferentLink(link);
		populateSets(linkGraphList);
	}

	public void populateSets(Collection linkGraphList) {
		for (Iterator iter = linkGraphList.iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;
				addReferentLink(link);
			}

			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;

				populateSets(sublinkGraph.getChildren());
				addReferentLink(sublinkGraph.getRootLink());
			}
		}

	}

	private void addReferentLink(Link link) {
		if (link != null && link.getReferentType() != null) {
			typeSet.add(link.getReferentType());
			// long a = ((long)link.getReferentType()) << 32;
			// long b = link.getReferentId();
			// Long val = new Long( a | b );
			// System.out.println( val );
			referrentSet.add(DBUtils.encodeTypeAndId(link.getReferentType(), link.getReferentId()));
		}
	}

	public Link getRootLink() {
		return rootLink;
	}

	public void setRootLink(Link link) {
		this.rootLink = link;
	}

	public Collection getChildren() {
		return children;
	}

	public void addChildLink(Link link) {
		children.add(link);
	}

	public void removeChildLink(Link link) {
		children.remove(link);
	}

	public LinkGraph addSubLinkGraph(LinkGraph linkGraph) {
		// linkGraph.getRootLink().setObjectAsReferrer(getRootLink());
		children.add(linkGraph);
		linkGraph.setParentLinkGraph(this);

		// in case this was just a child link before, we need to delete the that
		// child link
		removeChildLink(linkGraph.getRootLink());

		return linkGraph;
	}

	public void removeSubLinkGraph(LinkGraph linkGraph) {
		children.remove(linkGraph);
	}

	public LinkGraph getFirstSubLinkGraph() {

		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				return sublinkGraph;
			}
		}
		return null;
	}

	public void setChildren(Collection elements) {
		this.children = elements;
	}

	public TypeSet getTypeSet() {
		if (this.typeSet == null) {
			populateSets(children, rootLink);
		}
		return typeSet;
	}

	public void setTypeSet(TypeSet typeSet) {
		this.typeSet = typeSet;
	}

	public Collection flatten() {
		return flatten(this, new ArrayList());
	}

	public Collection flatten(LinkGraph linkGraph, Collection flatList) {

		for (Iterator iter = linkGraph.getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;
				flatList.add(link);
			}

			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				flatten(sublinkGraph, flatList);
			}
		}

		return flatList;
	}

	public Collection getSubLinks() {
		ArrayList subLinks = new ArrayList();

		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;
				subLinks.add(link);
			}
		}

		return subLinks;
	}

	public Collection getSubLinkGraphs() {
		ArrayList subLinkGraphs = new ArrayList();

		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				subLinkGraphs.add(sublinkGraph);
			}
		}

		return subLinkGraphs;
	}

	public Collection getAllSubLinks() {
		ArrayList subLinks = new ArrayList();

		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				subLinks.add(sublinkGraph.getRootLink());
			} else {
				subLinks.add(obj);
			}
		}

		return subLinks;
	}

	public HashSet getReferrentSet() {
		if (this.referrentSet == null) {
			populateSets(children, rootLink);
		}
		return referrentSet;
	}

	public void setReferrentSet(HashSet referrentSet) {
		this.referrentSet = referrentSet;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Link createLinkToMe(Object fromThis) throws DBException {
		Link refLink = new Link(fromThis, getRootLink());
		refLink.setReferentId(getRootId());
		linkHandler.addLink(refLink);
		return refLink;
	}

	public boolean hasTypeFrom(Collection<Link> links) {
		for ( Link link: links ) {
			if (typeSet.contains(link.getReferentType())) {
				return true;
			}
		}
		return false;
	}

	public LinkGraph clone() {
		return clone(this);
	}

	private LinkGraph clone(LinkGraph linkGraph) {
		LinkGraph newLinkGraph = new LinkGraph(linkGraph.getRootLink().clone());

		for (Iterator iter = linkGraph.getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;
				newLinkGraph.addChildLink(new Link(link));
			}

			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				newLinkGraph.addSubLinkGraph(clone(sublinkGraph));
			}
		}
		return newLinkGraph;
	}

	public Link findSubLink(Link likeThis) {
		return findSubLink(likeThis, Link.MATCH_ID);
	}

	public Link findSubLink(Link likeThis, char searchType) {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;

				switch (searchType) {
				case Link.MATCH_ID:
					if (likeThis.equals(link)) {
						return link;
					}
					break;
				case Link.MATCH_REFERENT:
					if (likeThis.sameReferent(link)) {
						return link;
					}
					break;
				case Link.MATCH_REFERRER:
					if (likeThis.sameReferrer(link)) {
						return link;
					}
					break;
				}
			}

			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				return sublinkGraph.findSubLink(likeThis, searchType);
			}
		}
		return null;
	}

	public LinkGraph findSubLinkGraph(Link likeThis) {
		return findSubLinkGraph(likeThis, Link.MATCH_ID);
	}

	public LinkGraph findSubLinkGraph(Link likeThis, char searchType) {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			LinkGraph sublinkGraph = null;
			Link link = null;
			if (obj instanceof LinkGraph) {
				sublinkGraph = (LinkGraph) obj;
				link = sublinkGraph.getRootLink();
			}
			if (obj instanceof Link) {
				link = (Link) obj;
			}

			switch (searchType) {
			case Link.MATCH_ID:
				if (likeThis.equals(link)) {
					if (sublinkGraph != null) {
						return sublinkGraph;
					} else {
						return addSubLinkGraph(new LinkGraph(this, link));
					}
				}
				break;
			case Link.MATCH_REFERENT:
				if (likeThis.sameReferent(link)) {
					if (sublinkGraph != null) {
						return sublinkGraph;
					} else {
						return addSubLinkGraph(new LinkGraph(this, link));
					}
				}
				break;
			case Link.MATCH_REFERRER:
				if (likeThis.sameReferrer(link)) {
					if (sublinkGraph != null) {
						return sublinkGraph;
					} else {
						return addSubLinkGraph(new LinkGraph(this, link));
					}
				}
				break;
			}

			if (sublinkGraph != null) {
				return sublinkGraph.findSubLinkGraph(likeThis, searchType);
			}

		}
		return null;
	}

	public LinkGraph getRootLinkGraph() {
		if (parentLinkGraph != null) {
			if (rootLinkGraph == null) {
				rootLinkGraph = parentLinkGraph.getRootLinkGraph();
			}
			return rootLinkGraph;
		}
		return this;
	}

	public void setChildrenReferrer(Integer toThisReferrerId, Integer toThisReferrerType) {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;
				link.setReferrerId(toThisReferrerId);
				link.setReferrerType(toThisReferrerType);
			}
		}
	}

	public void expandExtLink(LinkGraph extLinkGraph) {
		LinkGraph parentLinkGraph = extLinkGraph.getParentLinkGraph();
		if (parentLinkGraph != null) {
			parentLinkGraph.removeSubLinkGraph(extLinkGraph);
		}

		// do P2P links first, as P2ST needs them all in place
		for (Iterator iter = extLinkGraph.getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;

				if (link.isP2PLink() && parentLinkGraph != null) {
					LinkGraph profileLinkGraph = dProfile.getInstance().getProfileGraph(link);
					parentLinkGraph.addSubLinkGraph(profileLinkGraph);
				}

			}
		}

		// now do P2ST links
		for (Iterator iter = extLinkGraph.getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				Link link = sublinkGraph.getRootLink();
				if (link.isP2STLink() && parentLinkGraph != null) {
					Link searchLink = new Link();
					searchLink.setId(link.getReferentId());
					LinkGraph targetLinkGraph = getRootLinkGraph().findSubLinkGraph(searchLink, Link.MATCH_ID);
					targetLinkGraph.addAllChildren(sublinkGraph);
					// the referentid of this P2ST link points to the link that
					// is the childrens parent
					// sublinkGraph.setChildrenReferrer(

					// TODO what happens if you move the link in the profile
				}
			}
		}

	}

	public void addAllChildren(LinkGraph fromThisGraph) {
		children.addAll(fromThisGraph.getChildren());
	}

	public void printTree(String indent) {

		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;
				System.out.println(indent + link);
			}
			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				System.out.println(indent + sublinkGraph);
				sublinkGraph.printTree(indent + ".");
			}
		}
	}

	public LinkGraph getParentLinkGraph() {
		return parentLinkGraph;
	}

	public void setParentLinkGraph(LinkGraph parentLinkGraph) {
		this.parentLinkGraph = parentLinkGraph;
	}

	public Link addP2PLink(Link profileRootLink) {
		Link l = new Link(profileRootLink.getId(), Link.getTypeId(), null, null, Link.P2P);
		addChildLink(l);
		return l;
	}

	public LinkGraph addP2STLink(Link targetProfileLink) {
		Link l = new Link(targetProfileLink.getId(), Link.getTypeId(), null, null, Link.P2ST);
		LinkGraph linkGraph = new LinkGraph(l);
		addSubLinkGraph(linkGraph);
		return linkGraph;
	}

	public LinkGraph addSubExtLinkGraph() {
		LinkGraph linkGraph = new LinkGraph(new Link(null, null, null, null, Link.EXT));
		addSubLinkGraph(linkGraph);
		return linkGraph;
	}

	public String toString() {
		return "LG " + id + " r> " + rootLink;
	}

	public Object createMappedObj(Object mappedObject, CachedClass mappedObjectCachedClass) throws DBException {

		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;
				mapObject(link, mappedObject, mappedObjectCachedClass);
			}

			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				Link rootLink = sublinkGraph.getRootLink();
				Object childObject = mapObject(rootLink, mappedObject, mappedObjectCachedClass);
				if (childObject != null) {
					sublinkGraph.createMappedObj(childObject, ReflectCache.i().getClassCache(
							childObject.getClass()));
				}
			}
		}
		return mappedObject;
	}

	private Object mapObject(Link link, Object mappedObject, CachedClass mappedObjectCachedClass) throws DBException {
		String propertyName = link.getName();
		try {
			Object object = linkHandler.getReferentObject(link);
			mappedObjectCachedClass.invokeSetMethod(mappedObject, propertyName, object);
			return object;
		} catch (ClassNotFoundException e) {
			throw new DBException(e);
		}
	}

	public void populateReferents() throws DBException {

		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;
				createReferent(link);
			}

			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				Link rootLink = sublinkGraph.getRootLink();
				createReferent(rootLink);
				sublinkGraph.populateReferents();
			}
		}

	}

	private void createReferent(Link link) throws DBException {
		Class clazz = objectAccessorFactory.getClassForTypeId(link.getReferentId());
		if (clazz != null) {
			try {

				Object newObj = clazz.newInstance();
				DBObjectAccessor objectAccessor = objectAccessorFactory.getAccessor(clazz);
				Object id = objectAccessor.save(newObj);
				// objectAccessorFactory.flush();
				link.setReferentId((Integer) id);
				objectAccessorFactory.getAccessor(Link.class).save(link);

			} catch (InstantiationException e) {
				throw new DBException(e);
			} catch (IllegalAccessException e) {
				throw new DBException(e);
			}
		}
	}

	public static LinkGraph makeNewLinkGraph() {
		try {
			Link rl = new Link();
			Integer id = (Integer) objectAccessorFactory.getAccessor(Link.class).save(rl);
			// objectAccessorFactory.flush();
			LinkGraph linkGraph = new LinkGraph(rl);
			linkGraph.setRootId(id);
			objectAccessorFactory.getAccessor(LinkGraph.class).save(linkGraph);
			return linkGraph;
		} catch (DBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static LinkGraph makeNewLinkGraph(Object rootObj) {
		try {
			Link rl = new Link(rootObj, false);
			Integer id = (Integer) objectAccessorFactory.getAccessor(Link.class).save(rl);
			// objectAccessorFactory.flush();
			LinkGraph linkGraph = new LinkGraph(rl);
			linkGraph.setRootId(id);
			objectAccessorFactory.getAccessor(LinkGraph.class).save(linkGraph);
			return linkGraph;
		} catch (DBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Integer getRootId() {
		return rootId;
	}

	public void setRootId(Integer rootId) {
		this.rootId = rootId;
	}
}
