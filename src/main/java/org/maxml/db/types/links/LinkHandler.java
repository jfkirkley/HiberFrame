package org.maxml.db.types.links;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessor;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.DBSessionInterfaceFactory;
import org.maxml.db.query.Expression;
import org.maxml.db.query.GenericQuery;
import org.maxml.reflect.CachedClass;
import org.maxml.reflect.ReflectCache;
import org.maxml.util.ClassUtils;
import org.maxml.util.Util;
import org.maxml.xpath.ApplyXPath;

/*
 o1:

 l3r1
 l2r1
 l1r1
 l1r2
 l2r2
 l1r3
 l1r4
 
 o1, changes r3
 - l2r2, l3r1 are invalidated

 Objects:
 --------
 Registry
 - referent trees (RT) (top level is referent object)
 eg. RT|d,s,m->city,country,p
 TopLevel Objects (TLO)       
 - TLO tree (TLOT) (top level is referent object)
 eg. TLO->TLOT|d,s,m->city,country,p
 - RT list are Ref stored with the negative value of the TLO as the referrerId 
 
 Common
 - refobj
 - idreferent
 - referentType
 - idReferrer
 - referrerType

 Create Profile:
 --------------
 example scenario:
 - dialup
 - storetime
 - meta location
 - City
 - Country
 - postal code

 procedure to create RT (all referents must be defined) 
 1) get [property]id/type of each referent and use this to create RT
 2) query (FARQ) to find all TLOs with ref hierarchies that match
 3) add referrent tree to the TLOs RTL

 procedure when modding TLO
 eg. top level objec 1 (tlo1) changes City attribute
 1) invalidate(remove) each corresponding RT in RTL
 2) if new value for city object already exists 
 - query (FARQ) to find any RTs that match and add to RTL
 
 procedure when adding top level entity
 1) for all existing referrents 
 - query (FARQ) to find any RTs that match and add to RTL

 procedure when modding RT
 eg. RT changes City attribute
 1) query (FATLOQ) to find all TLOs that have this RT in their RTL
 2) foreach TLO with RT in its RTL invalidate(remove) each corresponding RT in the TLOs RTL
 if not tied( it is tied if the  RT defines the TLOs ref hierarchy)
 3) query (FARQ) to find all TLOs with ref hierarchies that match
 4) add referrent tree to the TLOs RTL


 referent Heirarchy
 - create refobj where referrer is a parent refobj (eg. attributes have metadata parent) 
 if sub ref changes propagate invalidations upwards
 
 propagate(RT) {
 rtl = FARQ_RT(RT)
 foreach( rt in rtl ) 
 if( rt in RTL ) propagate(rt)

 */

public class LinkHandler {

	private final static String			referentIdPropertyName		= "referentId";
	private final static String			referentTypePropertyName	= "referentType";
	private final static String			referrerIdPropertyName		= "referrerId";
	private final static String			referrerTypePropertyName	= "referrerType";
	protected DBObjectAccessorFactory	objectAccessorFactory		= DBObjectAccessorFactory.i();

	private static Object				instance					= null;

	// private Integer linkTypeId = Link.getTypeId();

	public static LinkHandler getInstance() {
		return (LinkHandler) (instance = ClassUtils.i().getSingletonInstance(instance, LinkHandler.class));
	}

	public LinkHandler() {
	}

	public LinkGraph addLinkGraph(HashMap<Integer,?> referrerId2TypeMap) throws DBException {
		return addLinkGraph(referrerId2TypeMap, -1, -1);
	}

	public LinkGraph addLinkGraph(HashMap<Integer,?> referrerId2TypeMap, Integer referrerId, Integer referrerType)
			throws DBException {

		Link rootLink = new Link(-1, -1, referrerId, referrerType);

		getAccessor(Link.class).save(rootLink);
		flush();
		Integer referrerTypeId = Link.getTypeId();
		ArrayList<Link> links = new ArrayList<Link>();
		LinkGraph linkGraph = new LinkGraph(rootLink, links);

		for ( Integer id: referrerId2TypeMap.keySet() ) {
			Object obj = referrerId2TypeMap.get(id);
			if (obj instanceof Integer) {
				Integer type = (Integer) obj;

				linkGraph.getTypeSet().add(type);
				Link referrer = new Link(id, type, rootLink.getId(), referrerTypeId);
				getAccessor(Link.class).save(referrer);

				links.add(referrer);

			}
			if (obj instanceof HashMap) {
				HashMap referrerId2TypeSubMap = (HashMap) obj;
				LinkGraph sublinkGraph = addLinkGraph(referrerId2TypeSubMap, rootLink.getId(), referrerTypeId);
				linkGraph.getTypeSet().addAll(sublinkGraph.getTypeSet());
			}
		}

		flush();
		return linkGraph;
	}

	public LinkGraph addLinkGraph(LinkGraph linkGraph) throws DBException {

		Link rootLink = linkGraph.getRootLink();

		getAccessor(Link.class).save(rootLink);
		flush();

		for (Iterator iter = linkGraph.getChildren().iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (obj instanceof Link) {
				Link link = (Link) obj;
				link.setReferrerId(rootLink.getId());
				link.setReferrerType(Link.getTypeId());
				getAccessor(Link.class).save(link);
				System.out.println(link);
			}
			if (obj instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) obj;
				sublinkGraph.getRootLink().setObjectAsReferrer(rootLink);
				addLinkGraph((LinkGraph) obj);
			}
		}

		flush();
		return linkGraph;
	}

	public void addLink(Link link) throws DBException {
		getAccessor(Link.class).save(link);
	}

	public void addLinksToObject(Object target, Collection<Link> links) throws DBException {
		for ( Link link: links ) {
			Link linkToTarget = new Link(link, target);
			getAccessor(Link.class).save(linkToTarget);
		}
	}

	public void addLinksToObjectFromReferrers(Object target, Collection<Link> links) throws DBException {
		for ( Link link: links ) {
			Link linkToTarget = new Link(target, false);
			linkToTarget.setReferrerId(link.getReferrerId());
			linkToTarget.setReferrerType(link.getReferrerType());
			getAccessor(Link.class).save(linkToTarget);
		}
	}

	public void addLinksFromObject(Object target, Collection<Link> links) throws DBException {
		for ( Link link: links ) {
			Link linkFromTarget = new Link(target, link);
			getAccessor(Link.class).save(linkFromTarget);
		}
	}

	public void createLinksToObject(Object object, Collection otherObjects) throws DBException {
		for (Iterator iter = otherObjects.iterator(); iter.hasNext();) {
			Object o = iter.next();
			Link link = new Link(object, o);
			getAccessor(Link.class).save(link);
		}
	}

	public HashMap makeId2TypeMap(Collection entityList) {
		HashMap id2TypeMap = new HashMap();
		for (Iterator iter = entityList.iterator(); iter.hasNext();) {
			Object entity = iter.next();
			Class entityClass = entity.getClass();
			String idPropertyName = objectAccessorFactory.getTypeIdPropertyName(entityClass);
			CachedClass cachedClass = ReflectCache.i().getClassCache(entityClass);
			Integer id = (Integer) cachedClass.invokeGetMethod(entity, idPropertyName);
			Integer typeId = objectAccessorFactory.getTypeIdForClass(entityClass.getName());

			id2TypeMap.put(id, typeId);
		}
		return id2TypeMap;
	}

	public void deleteAllMatches(Collection<Link> linkList) throws DBException {
		deleteLinkCollection(getLinksMatchingAll(linkList));
	}

	public void deleteLinkCollection(Collection<Link> links) throws DBException {
		if (links != null) {
			for (Iterator iter = links.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof Link) {
					Link link = (Link) o;
					getAccessor(Link.class).delete(link.getId());
				} else {
					LinkGraph linkGraph = (LinkGraph) o;
					deleteLinkCollection(linkGraph.getChildren());
					getAccessor(LinkGraph.class).delete(linkGraph.getId());
				}
			}
		}
	}

	public void deleteSubMatches(Link referrer) throws DBException {
		deleteLinkCollection(getSubLinks(referrer));
	}

	public void deleteGraph(Link referrer) throws DBException {
		try {

			LinkGraph linkGraph = GraphReader.read(referrer);
			deleteLinkCollection(linkGraph.getChildren());
			getAccessor(LinkGraph.class).delete(linkGraph.getId());

		} catch (OldVisitException e) {
			throw new DBException(e);
		}
	}

	public void deleteLinksToObject(Object target, Collection<Link> links) throws DBException {

		Link linkToTarget = new Link(target, false);
		for ( Link referrer: links ) {
			delete(referrer, linkToTarget);
		}
	}

	public void deleteAllLinksToObject(Object target) throws DBException {
		Link linkToTarget = new Link(target, false);
		deleteAll(linkToTarget, true);
	}

	public void deleteLinksFromObject(Object target, Collection<Link> links) throws DBException {

		Link linkFromTarget = new Link(target, true);
		for ( Link referent: links ) {
			delete(linkFromTarget, referent);
		}
	}

	public void delete(Link linkForReferrerSide, Link linkForReferentSide) throws DBException {
		delete(linkForReferrerSide.getReferrerId(), linkForReferrerSide.getReferrerType(), linkForReferentSide
				.getReferentId(), linkForReferentSide.getReferentType());
	}

	public void delete(Link link) throws DBException {
		getAccessor(Link.class).delete(link.getId().intValue());
	}

	public void delete(Integer referrerId, Integer referrerType, Integer referentId, Integer referentType)
			throws DBException {
		getAccessor(Link.class).delete(
				new Object[] { "referrerId", referrerId, "referrerType", referrerType, "referentId", referentId,
						"referentType", referentType });
	}

	public void deleteReferentObject(Link link) throws DBException {
		Integer referentType = link.getReferentType();
		Integer referentId = link.getReferentId();
		if (referentId != null && referentType != null) {
			DBObjectAccessor objectAccessor = objectAccessorFactory.getAccessor(referentType);
			objectAccessor.delete(referentId.intValue());
		}
	}

	public void deleteAll(Link link, boolean referrers) throws DBException {
		if (referrers) {
			getAccessor(Link.class).delete(
					new Object[] { "referrerId", link.getReferrerId(), "referrerType", link.getReferrerType() });
		} else {
			getAccessor(Link.class).delete(
					new Object[] { "referentId", link.getReferentId(), "referentType", link.getReferentType() });
		}
	}

	public Collection<Link> getSubLinks(Link link) throws DBException {
		return getLinks(link.getId(), link.getTypeId());
	}

	public Collection<Link> getLinks(Link link) throws DBException {
		return getLinks(link.getReferrerId(), link.getReferrerType());
	}

	public Collection<Link> getLinks(Integer referrerId, Integer referrerType) throws DBException {
		return getAccessor(Link.class).findAll(
				new Object[] { referrerIdPropertyName, referrerId, referrerTypePropertyName, referrerType });
	}

	public Collection<Link> getLinksByReferent(Integer referentId, Integer referentType) throws DBException {
		return getAccessor(Link.class).findAll(
				new Object[] { referentIdPropertyName, referentId, referentTypePropertyName, referentType });
	}

	public Collection<Link> getLinksByReferrerType(Integer referrerType) throws DBException {
		return getAccessor(Link.class).findAll(new Object[] { referrerTypePropertyName, referrerType });
	}

	public Collection<Link> getLinksByReferentType(Integer referentType) throws DBException {
		return getAccessor(Link.class).findAll(new Object[] { referentTypePropertyName, referentType });
	}

	public List<Link> getLinksMatchingAll(Collection<Link> links) throws DBException {

		List aliasValuePairList = new ArrayList();
		GenericQuery genericQuery = objectAccessorFactory.getGenericQuery();
		Expression expression = null;
		int cnt = 0;

		for ( Link link: links ) {

			if (expression == null) {
				expression =
				// Expression.getAndExpression(
				// Expression.getGTEExpression("r" + cnt,
				// referentIdPropertyName, 0),
				Expression.getAndExpression(Expression
						.getEqualsExpression("r" + cnt, referentIdPropertyName, "i" + cnt), Expression
						.getEqualsExpression("r" + cnt, referentTypePropertyName, "t" + cnt));
			} else {
				expression = Expression.getAndExpression(expression, Expression.getAndExpression(Expression
						.getAndExpression(Expression.getEqualsExpression("r" + cnt, referentIdPropertyName, "i" + cnt),
								Expression.getEqualsExpression("r" + cnt, referentTypePropertyName, "t" + cnt)),

				Expression.getAndExpression(Expression.getEqualsExpression("r" + (cnt - 1), referrerIdPropertyName, "r"
						+ cnt, referrerIdPropertyName), Expression.getEqualsExpression("r" + (cnt - 1),
						referrerTypePropertyName, "r" + cnt, referrerTypePropertyName))));
			}

			aliasValuePairList.add("i" + cnt);
			aliasValuePairList.add(link.getReferentId());
			aliasValuePairList.add("t" + cnt);
			aliasValuePairList.add(link.getReferentType());

			genericQuery.addEntityClass(Link.class, "r" + cnt);

			++cnt;
		}

		genericQuery.setSelectParameters(new String[] { "r0", referrerIdPropertyName, "r0", referrerTypePropertyName });
		genericQuery.setParameterExpression(expression);
		genericQuery.setParameterValues(aliasValuePairList);

		System.out.println(genericQuery.getQueryAsString());
		Util.i().printPairList(aliasValuePairList);

		return genericQuery.list();
	}

	public Collection<LinkGraph> getMathingGraphs(LinkGraph linkGraph) throws DBException {
		return getMatchingGraphs(linkGraph, new ArrayList<Link>());
	}

	public Collection<LinkGraph> getMatchingGraphs(LinkGraph linkGraph, Collection matchingGraphs) throws DBException {

		Collection<Link> subLinks = linkGraph.getAllSubLinks();
		Collection<Link> matches = getLinksMatchingAll(subLinks);

		if (matchingGraphs.size() > 0) {
			Collection temp = Util.i().getIntersection(matches, matchingGraphs);
			matchingGraphs.clear();
			matchingGraphs.addAll(temp);
		} else {
			matchingGraphs.addAll(matches);
		}

		LinkGraph tempLinkGraph = new LinkGraph();
		Collection<LinkGraph> subLinkGraphs = linkGraph.getSubLinkGraphs();
		if (subLinkGraphs.size() > 0) {
			for ( LinkGraph subLinkGraph: subLinkGraphs ) {
				tempLinkGraph.getChildren().addAll(subLinkGraph.getAllSubLinks());
			}
		} else {
			return matchingGraphs;
		}

		return getMatchingGraphs(tempLinkGraph, matchingGraphs);
	}

	public Collection<Link> filterOutItemsReferringToType(Collection<Link> links, Integer type) {
		List<Link> removeItems = new ArrayList<Link>();
		for ( Link link: links ) {
			if (link.getReferentType().equals(type)) {
				removeItems.add(link);
			}
		}
		for (Iterator iter = removeItems.iterator(); iter.hasNext();) {
			links.remove(iter.next());
		}
		return links;
	}

	public Collection<Link> filterOutItemsReferredToByType(Collection<Link> links, Integer type) {
		List<Link> removeItems = new ArrayList<Link>();
		for ( Link link: links ) {
			if (link.getReferrerType().equals(type)) {
				removeItems.add(link);
			}
		}
		for (Iterator iter = removeItems.iterator(); iter.hasNext();) {
			links.remove(iter.next());
		}
		return links;
	}

	public LinkGraph addGroup(HashMap referrerId2TypeMap) throws DBException {
		return addGroup(referrerId2TypeMap, -1, -1);
	}

	public LinkGraph addGroup(HashMap referrerId2TypeMap, Integer groupReferrerId, Integer groupReferrerType)
			throws DBException {

		LinkGraph linkGraph = addLinkGraph(referrerId2TypeMap, groupReferrerId, groupReferrerType);

		List<Link> matchingReferrerList = getLinksMatchingAll(linkGraph.getChildren());
		for (Iterator iter = matchingReferrerList.iterator(); iter.hasNext();) {
			Object[] params = (Object[]) iter.next();

			Integer referrerId = (Integer) params[0];
			Integer referrerType = (Integer) params[1];

			if (!linkGraph.getRootLink().matches(referrerId, referrerType)) {
				Link referrerRef = new Link(referrerId, referrerType, groupReferrerId, groupReferrerType);
				getAccessor(Link.class).save(referrerRef);
			}
		}
		return linkGraph;
	}

	public LinkGraph addGroup(LinkGraph linkGraph) throws DBException {
		addLinkGraph(linkGraph);
		Integer groupReferrerId = linkGraph.getRootLink().getReferrerId();
		Integer groupReferrerType = linkGraph.getRootLink().getReferrerType();

		List<Link> matchingReferrerList = getLinksMatchingAll(linkGraph.flatten());

		for (Iterator iter = matchingReferrerList.iterator(); iter.hasNext();) {
			Object[] params = (Object[]) iter.next();

			Integer referrerId = (Integer) params[0];
			Integer referrerType = (Integer) params[1];

			if (!linkGraph.getRootLink().matches(referrerId, referrerType)) {
				Link referrerRef = new Link(referrerId, referrerType, groupReferrerId, groupReferrerType);
				getAccessor(Link.class).save(referrerRef);
			}
		}
		return linkGraph;
	}

	public Collection<Link> getLinksWithSameReferrer(Link link) throws DBException {
		return getLinks(link.getReferrerId(), link.getReferrerType());
	}

	public List<Link> getMatchingLinks(Integer referentId, Integer referentType) throws DBException {
		// return getAccessor(Referrer.class).getPropertyFromAll("referrerId",
		// new Object[]{"referentId", referentId, "referentType", referentType,
		// "referrerType", Referrer.getTypeId() } );
		List aliasValuePairList = new ArrayList();

		GenericQuery genericQuery = objectAccessorFactory.getGenericQuery();
		Expression expression = Expression.getAndExpression(Expression.getAndExpression(Expression.getGTEExpression(
				"r", referentIdPropertyName, 0), Expression.getEqualsExpression("r", referrerTypePropertyName, "rt")),
				Expression.getAndExpression(Expression.getEqualsExpression("r", referentIdPropertyName, "i"),
						Expression.getEqualsExpression("r", referentTypePropertyName, "t")));

		aliasValuePairList.add("i");
		aliasValuePairList.add(referentId);
		aliasValuePairList.add("t");
		aliasValuePairList.add(referentType);
		aliasValuePairList.add("rt");
		aliasValuePairList.add(Link.getTypeId());

		genericQuery.setSelectParameters(new String[] { "r", referrerIdPropertyName });
		genericQuery.setParameterExpression(expression);
		genericQuery.setParameterValues(aliasValuePairList);
		genericQuery.addEntityClass(Link.class, "r");

		System.out.println("------------   " + genericQuery.getQueryAsString());

		return genericQuery.list();
	}

	public List<Link> getMatchingLinks(Link link) throws DBException {
		return getMatchingLinks(link.getReferentId(), link.getReferentType());
	}

	public Link findOrCreateLink(Object referrer, Object referent) throws DBException {
	    Link link = new Link(referrer, referent);
        Link realLink = findLink(link);
        if( realLink == null) {
            DBObjectAccessorFactory.i().getAccessor(Link.class).save(link);
            return link;
        }
        return realLink;
    }
    
    public Link findLink(Link likeThis) throws DBException {
		return findLink(likeThis.getReferrerId(), likeThis.getReferrerType(), likeThis.getReferentId(), likeThis
				.getReferentType());
	}

	public Link findLink(Integer referrerId, Integer referrerType, Integer referentId, Integer referentType)
			throws DBException {
		return (Link) getAccessor(Link.class).find(
				new Object[] { "referrerId", referrerId, "referrerType", referrerType, "referentId", referentId,
						"referentType", referentType });
	}

	public Link findLinkByReferent(Integer referentId, Integer referentType) throws DBException {
		return (Link) getAccessor(Link.class).find(
				new Object[] { "referentId", referentId, "referentType", referentType });
	}

	public Link findLinkByReferrer(Integer referrerId, Integer referrerType) throws DBException {
		return (Link) getAccessor(Link.class).find(
				new Object[] { "referrerId", referrerId, "referrerType", referrerType });
	}

	public Link findLinkByReferentAndReferrerType(Object referent, Class referrerClass) throws DBException {
		return findLinkByReferentAndReferrerType((Integer) objectAccessorFactory.getObjectId(referent),
				objectAccessorFactory.getTypeIdForClass(referent.getClass()), objectAccessorFactory
						.getTypeIdForClass(referrerClass));
	}

	public Link findLinkByReferentAndReferrerType(Integer referentId, Class referentClass, Class referrerClass)
			throws DBException {
		return findLinkByReferentAndReferrerType(referentId, objectAccessorFactory.getTypeIdForClass(referentClass),
				objectAccessorFactory.getTypeIdForClass(referrerClass));
	}

	public Link findLinkByReferentAndReferrerType(Integer referentId, Integer referentType, Integer referrerType)
			throws DBException {
		return (Link) getAccessor(Link.class).find(
				new Object[] { "referentId", referentId, "referentType", referentType, "referrerType", referrerType });
	}

	public Object getReferredObject(Integer referentId, Class referentClass, Class referrerClass) throws DBException,
			ClassNotFoundException {
		return getReferredObject(findLinkByReferentAndReferrerType(referentId, referentClass, referrerClass));
	}

	public Object getReferredObject(Object referent, Class referrerClass) throws DBException, ClassNotFoundException {
		return getReferredObject(findLinkByReferentAndReferrerType(referent, referrerClass));
	}

	// Referrer Accessor methods for 'referred object'

	public Object getReferredObject(Link link) throws DBException, ClassNotFoundException {
		if (link == null)
			return null;
		return getReferredObject(link.getReferrerId(), link.getReferrerType());
	}

	public Object getReferredObject(Integer referrerId, Integer referrerType) throws DBException,
			ClassNotFoundException {
		return getReferredObject(referrerId, DBObjectAccessorFactory.i().getTypeIdClassName(referrerType));
	}

	public Object getReferredObject(Integer referrerId, String referrerTypeClassName) throws DBException,
			ClassNotFoundException {
		if(referrerTypeClassName==null) return null;
		return getReferredObject(referrerId, Class.forName(referrerTypeClassName));
	}

	public Object getReferredObject(Integer referrerId, Class referrerClass) throws DBException {
		return getAccessor(referrerClass).find(referrerId);
	}

	// Referrer Accessor methods for 'referent object'

	public Object getReferentObject(Link referrer) throws DBException, ClassNotFoundException {
		return getReferentObject(referrer.getReferentId(), referrer.getReferentType());
	}

	public Object getReferentObject(Integer referentId, Integer referentType) throws DBException,
			ClassNotFoundException {
		return getReferentObject(referentId, DBObjectAccessorFactory.i().getTypeIdClassName(referentType));
	}

	public Object getReferentObject(Integer referentId, String referentTypeClassName) throws DBException,
			ClassNotFoundException {
		// System.out.println( referentTypeClassName);
		return getReferentObject(referentId, Class.forName(referentTypeClassName));
	}

	public Object getReferentObject(Integer referentId, Class referentClass) throws DBException {
		return getAccessor(referentClass).find(referentId);
	}

	public boolean referentIsInLinks(Collection<Link> links, Integer referentId, Integer referentTypeId) {
		Link checkLink = new Link(referentId, referentTypeId, null, null);
		for ( Link link: links ) {
			if (checkLink.sameReferent(link)) {
				return true;
			}
		}
		return false;
	}

	public boolean referrerIsInLinks(Collection<Link> links, Integer referrerId, Integer referrerTypeId) {
		Link checkLink = new Link(null, null, referrerId, referrerTypeId);
		for ( Link link: links ) {
			if (checkLink.sameReferrer(link)) {
				return true;
			}
		}
		return false;
	}

	public Map deepReadLinkStructure(Integer rootId, int[] types) throws DBException {
		HashMap map = new HashMap();

		Collection<Link> rootLinks = LinkHandler.getInstance().getLinksByReferent(rootId, types[0]);
		try {

			for ( Link rootLink: rootLinks ) {

				Collection<Link> subLinks = LinkHandler.getInstance().getSubLinks(rootLink);

				Object refObj = null;
				refObj = LinkHandler.getInstance().getReferredObject(rootLink);

				CachedClass.deepRead(refObj);

				for ( Link subLink: subLinks ) {
					Object subRefObj = LinkHandler.getInstance().getReferentObject(subLink);

					CachedClass.deepRead(subRefObj);
				}
			}
		} catch (ClassNotFoundException e) {
			throw new DBException(e);
		}
		return map;
	}

	/*
	 * propagate(RT) { rtl = FARQ_RT(RT) foreach( rt in rtl ) if( rt in RTL )
	 * propagate(rt) }
	 */

	// public LinkGraph readLinkGraph(LinkGraph linkGraph) throws DBException {
	// try {
	// return GraphReader.read(linkGraph);
	// } catch (VisitException e) {
	// throw new DBException(e);
	// }
	// }
	//
	// public Collection<Link> getGraph(Link link) throws DBException {
	// List<Link> links = new ArrayList<Link>();
	// links.add(link);
	// return getGraph(links);
	// }
	//
	// public Collection<Link> getGraph(Collection<Link> links) throws DBException {
	//
	// List<Link> matchList = new ArrayList<Link>();
	//
	// for (Iterator iter = links.iterator(); iter.hasNext();) {
	// Link referrer = (Link) iter.next();
	//
	// Collection<Link> result = getLinks(referrer.getId(), Link.getTypeId());
	// if (result.size() > 0) {
	// Collection<Link> subMatchList = getGraph(result);
	// if (subMatchList.size() > 0) {
	// matchList.add(new LinkGraph(referrer, subMatchList));
	// }
	// } else {
	// matchList.add(referrer);
	// }
	// }
	//
	// return matchList;
	// }
	public Document getDOMLinkGraph(Link referrer) throws DBException {
		try {
			Document linkGraphDoc = ApplyXPath.i().getNewDoc();

			LinkGraph linkGraph = GraphReader.read(referrer);

			linkGraphDoc.appendChild(getDOMLinkGraph(linkGraph, linkGraphDoc.createElement("refDoc")));

			return linkGraphDoc;
		} catch (OldVisitException e) {
			throw new DBException(e);
		}
	}

	public Element getDOMLinkGraph(LinkGraph linkGraph, Element linkGraphElement) throws DBException {

		Collection linkGraphList = linkGraph.getChildren();
		for (Iterator iter = linkGraphList.iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o instanceof LinkGraph) {
				LinkGraph sublinkGraph = (LinkGraph) o;
				Element listElement = createLinkElement(sublinkGraph.getRootLink(), linkGraphElement);
				linkGraphElement.appendChild(listElement);
				getDOMLinkGraph(sublinkGraph, listElement);
			}
			if (o instanceof Link) {
				Link link = (Link) o;
				Element refElement = createLinkElement(link, linkGraphElement);
				linkGraphElement.appendChild(refElement);
			}
		}

		return linkGraphElement;
	}

	private Element createLinkElement(Link link, Element parentElement) throws DBException {
		Integer typeId = link.getReferentType();

		if (typeId >= 0) {
			String className = DBObjectAccessorFactory.i().getTypeIdClassName(typeId);
			if (className != null) {
				try {
					CachedClass cachedClass = new CachedClass(className);
					Object referentObject = getReferentObject(link.getReferentId(), cachedClass.getThisClass());
					Element classElement = parentElement.getOwnerDocument().createElement(cachedClass.getShortName());
					parentElement.appendChild(classElement);
					Iterator iter = cachedClass.getMethods().keySet().iterator();
					while (iter.hasNext()) {
						String mName = (String) iter.next();

						if (cachedClass.propertyIsBasic(mName)) {

							Element propertyElement = parentElement.getOwnerDocument().createElement(mName);
							classElement.appendChild(propertyElement);

							Object value = cachedClass.invokeGetMethod(referentObject, mName);
							if (value != null) {
								propertyElement.appendChild(parentElement.getOwnerDocument().createTextNode(
										value.toString()));
							}
						}
					}
					return classElement;

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return parentElement.getOwnerDocument()
				.createElement("r" + link.getReferentId() + "t" + link.getReferentType());
	}

	public List OLDgetReferrerGraph(List<Link> links) throws DBException {
		List matchList = new ArrayList();

		for ( Link link: links ) {
			List listOfMatchingGroups = getMatchingLinks(link);

			Expression expression = Expression.getAndExpression(Expression.getAndExpression(Expression
					.getEqualsExpression("r", "referrerId", link.getReferrerId()), Expression.getEqualsExpression("r",
					"referrerType", link.getReferrerType())), Expression.getAndExpression(Expression
					.getEqualsExpression("r", "referentType", Link.getTypeId()), Expression.getINExpression("r",
					"referentId", "rl")));

			GenericQuery genericQuery = objectAccessorFactory.getGenericQuery();

			genericQuery.setParameterExpression(expression);
			genericQuery.addEntityClass(Link.class, "r");

			List aliasValuePairList = new ArrayList();
			aliasValuePairList.add("rl");
			aliasValuePairList.add(listOfMatchingGroups);
			genericQuery.setParameterValues(aliasValuePairList);

			List result = genericQuery.list();
			matchList.add(result);

			matchList.add(OLDgetReferrerGraph(result));
		}

		return matchList;
		// genericQuery.setSelectParameters(new String[] { "r0",
		// referrerIdPropertyName, "r0", referrerTypePropertyName });

		// for (Iterator iter = links.iterator(); iter.hasNext();) {
		// Integer ReferrerId = (Integer) iter.next();
		// Referrer groupReferrer = findReferrerObject(
		// -1*referrer.getReferrerId(), referrer.getReferrerType(), ReferrerId,
		// Referrer.getTypeId() );
		// }
	}

	public static void printLinks(Collection<Link> collection) {
		for ( Link link: collection ) {
			System.out.print(link.getId() + "|");
		}
		System.out.println();
	}

	public DBObjectAccessor getAccessor(Class objectClass) {
		return objectAccessorFactory.getAccessor(objectClass);
	}

	protected void flush(boolean flush) throws DBException {
		if (flush) {
			flush();
		}
	}

	protected void flush() throws DBException {
		DBSessionInterfaceFactory.getInstance().endSession(false);
	}

}
