package org.maxml.db.types.links;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.util.Util;

public class pCollection implements Collection {
	transient static protected DBObjectAccessorFactory	objectAccessorFactory	= DBObjectAccessorFactory.i();

	private LinkGraph									linkGraph;
	private Integer										id;

	transient private HashMap<Link,Object>							link2ObjMap;
	transient private boolean							lazy					= false;
	transient private boolean							initialized				= false;
	transient private ArrayList							orderList;

	public pCollection(Object linkedToMe) throws DBException {
		this(linkedToMe, true);
	}

	public pCollection(Object linkedToMe, boolean lazy) throws DBException {
		this(new LinkGraph(LinkHandler.getInstance().findLink(new Link(linkedToMe, false))), lazy);
	}

	public pCollection() {
		lazy = true;
		initialized = false;
		link2ObjMap = new HashMap();
		orderList = new ArrayList();
	}

	public pCollection(LinkGraph linkGraph) {
		this();
		this.linkGraph = linkGraph;
	}

	public static pCollection build() {
		try {
			Link link = new Link();
			LinkGraph linkGraph = new LinkGraph(link);
			objectAccessorFactory.getAccessor(Link.class).save(link);
			objectAccessorFactory.getAccessor(LinkGraph.class).save(linkGraph);
			return build(linkGraph, false);
		} catch (DBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static pCollection build(LinkGraph parent, boolean lazy) {
		try {
			return new pCollection(parent, lazy);
		} catch (DBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public pCollection(LinkGraph parent, boolean lazy) throws DBException {
		this(parent);
		init(lazy);
	}

	private void init(boolean lazy) throws DBException {
		this.lazy = lazy;
		initMap();
	}

	private void initMap() throws DBException {
		if (linkGraph.getRootLink() != null) {
			Collection<Link> childlinks = LinkHandler.getInstance().getSubLinks(linkGraph.getRootLink());
			for ( Link link: childlinks ) {
				Object val = null;
				if (!lazy) {
					if (link.getReferentType() != null && link.getReferentId() != null) {
						val = objectAccessorFactory.getAccessor(link.getReferentType()).find(link.getReferentId());
						if (val != null) {
							this.orderList.add(val);
						} else {
							System.err.println("WARNING: pCollection has link to null obj! link: " + link);
						}
					}
				}
				this.link2ObjMap.put(link, val);
			}
		}
	}

	public void readAll() throws DBException {
		if (!initialized) {
			init(false);
			// HashMap tmpMap = new HashMap();
			// for (Iterator iter = link2ObjMap.keySet().iterator();
			// iter.hasNext();) {
			// Link link = (Link) iter.next();
			// Object val = null;
			// if (link.getReferentType() != null && link.getReferentId() !=
			// null) {
			// val =
			// objectAccessorFactory.getAccessor(link.getReferentType()).find(link.getReferentId());
			// tmpMap.put(link, val);
			// }
			// }
			// orderList.clear();
			// for (Iterator iter = tmpMap.keySet().iterator(); iter.hasNext();)
			// {
			// Link link = (Link) iter.next();
			// Object val = tmpMap.get(link);
			// this.link2ObjMap.put(link, val);
			// this.orderList.add(val);
			// }
		}
		initialized = true;
	}

	public int size() {
		try {
			readAll();
		} catch (DBException e) {
			e.printStackTrace();
		}
		return (link2ObjMap == null) ? 0 : link2ObjMap.size();
	}

	public boolean isEmpty() {
		try {
			readAll();
		} catch (DBException e) {
			e.printStackTrace();
		}
		return (link2ObjMap == null) ? true : link2ObjMap.isEmpty();
	}

	public boolean contains(Object o) {
        if(o==null)return false;
		try {
			readAll();
		} catch (DBException e) {
			e.printStackTrace();
		}
		for (Iterator iter = orderList.iterator(); iter.hasNext();) {
            if(o.equals(iter.next()) ){
                return true;
            }
        }
		return false;
	}

	public Iterator iterator() {
		if (lazy && !initialized) {
			try {
				readAll();
			} catch (DBException e) {
				// TODO BURIED EXCEPTION
			}
		}
		return orderList.iterator();
	}

	public Object[] toArray() {
		try {
			readAll();
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
		return orderList.toArray();// link2ObjMap.values().toArray();
	}

	public Object[] toArray(Object[] a) {
		try {
			readAll();
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
		return orderList.toArray();// link2ObjMap.values().toArray();
	}

	public boolean add(Object o) {
		try {
            Object id = objectAccessorFactory.getAccessor(o.getClass()).getObjectId(o);
            if( id == null) {
                id = objectAccessorFactory.getAccessor(o.getClass()).save(o);
            }
			// objectAccessorFactory.flush();
			Link l = new Link(linkGraph.getRootLink(), linkGraph.getRootLink().getId(), o, (Integer) id);
			linkGraph.addChildLink(l);
			objectAccessorFactory.getAccessor(Link.class).save(l);
			link2ObjMap.put(l, o);
			orderList.add(o);
			return true;
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean remove(Object o) {
		try {
			readAll();
			Link link = (Link) Util.i().findKeyForValue(link2ObjMap, o);
			if (link != null) {
				objectAccessorFactory.getAccessor(Link.class).delete(link);
				//objectAccessorFactory.getAccessor(o.getClass()).delete(o);
				link2ObjMap.remove(link);
				orderList.remove(o);
				return true;
			}
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public boolean containsAll(Collection c) {
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			if (!link2ObjMap.containsValue(iter.next())) {
				return false;
			}
		}
		return true;
	}

	public boolean addAll(Collection c) {
		boolean retval = false;
		if (c == null)
			return retval;
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			retval = add(iter.next()) || retval;
		}
		return retval;
	}

	public boolean removeAll(Collection c) {
		boolean retval = false;
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			retval = remove(iter.next()) || retval;
		}
		return retval;
	}

	public boolean retainAll(Collection c) {
		boolean retval = false;
		ArrayList itemsToRemove = new ArrayList();
		for (Iterator iter = link2ObjMap.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			Object val = link2ObjMap.get(key);
			if (!c.contains(val)) {
				itemsToRemove.add(key);
				orderList.remove(val);
			}
		}
		for (Iterator iter = itemsToRemove.iterator(); iter.hasNext();) {
			link2ObjMap.remove(iter.next());
		}
		return retval;
	}

	public void clear() {
		try {
			readAll();
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
		removeAll(new ArrayList(link2ObjMap.values()));
	}

	public Collection getReadObjects() {
		List readObjs = new ArrayList();
		for ( Link link: link2ObjMap.keySet() ) {
			Object object = link2ObjMap.get(link);
			if (object != null) {
				readObjs.add(object);
			}
		}
		return readObjs;
	}

	public static void main(String[] a) {
//		try {

//			ArrayList al = new ArrayList();
//
//			al.add(new AttributeDef("attr.type.01"));
//			al.add(new AttributeDef("attr.type.02"));
//			al.add(new AttributeDef("attr.type.03"));
//
//			for (Iterator iter = al.iterator(); iter.hasNext();) {
//				AttributeDef attributeDef = (AttributeDef) iter.next();
//				DBObjectAccessorFactory.getInstance().getAccessor(AttributeDef.class).save(attributeDef);
//			}
//
//			ArrayList al2 = new ArrayList();
//			al2.add(new AttributeValue((Definition) al.get(0), "attr.value.01"));
//			al2.add(new AttributeValue((Definition) al.get(1), "attr.value.02"));
//			al2.add(new AttributeValue((Definition) al.get(2), "attr.value.03"));
//			al2.add(new AttributeValue((Definition) al.get(0), "attr.value.04"));
//			al2.add(new AttributeValue((Definition) al.get(1), "attr.value.05"));
//			al2.add(new AttributeValue((Definition) al.get(2), "attr.value.06"));
//			al2.add(new AttributeValue((Definition) al.get(0), "attr.value.07"));
//			al2.add(new AttributeValue((Definition) al.get(1), "attr.value.08"));
//			al2.add(new AttributeValue((Definition) al.get(2), "attr.value.09"));
//
//			for (Iterator iter = al2.iterator(); iter.hasNext();) {
//				AttributeValue attributeValue = (AttributeValue) iter.next();
//				DBObjectAccessorFactory.getInstance().getAccessor(AttributeDef.class).save(attributeValue);
//			}
//			if (false) {
//				lCollection lc = new lCollection();
//				for (Iterator iter = al2.iterator(); iter.hasNext();) {
//					AttributeValue attributeValue = (AttributeValue) iter.next();
//					lc.add(attributeValue);
//				}
//
//			} else {
//
//				lCollection collection = new lCollection(new LinkGraph((Link) DBObjectAccessorFactory.getInstance()
//						.getAccessor(Link.class).find(1)), true);
//				for (Iterator iter = collection.iterator(); iter.hasNext();) {
//					AttributeValue element = (AttributeValue) iter.next();
//					System.out.println(element.getValue());
//				}
//
//				if (false) {
//					al2.clear();
//					int i = 0;
//					for (Iterator iter = collection.iterator(); iter.hasNext();) {
//						AttributeValue element = (AttributeValue) iter.next();
//						System.out.println(element.getValue());
//						if (++i % 2 == 0)
//							al2.add(element);
//					}
//
//					for (Iterator iter = al2.iterator(); iter.hasNext();) {
//						AttributeValue element = (AttributeValue) iter.next();
//						collection.remove(element);
//						System.out.println("removing: " + element.getValue());
//					}
//					// collection.remove()
//					for (Iterator iter = al2.iterator(); iter.hasNext();) {
//						AttributeValue attributeValue = (AttributeValue) iter.next();
//						collection.add(attributeValue);
//					}
//				}
//			}
//			DBObjectAccessorFactory.getInstance().endSession();
//		} catch (DBException e) {
//			e.printStackTrace();
//		}
	}

	public LinkGraph getLinkGraph() {
		return linkGraph;
	}

	public void setLinkGraph(LinkGraph linkGraph) {
		this.linkGraph = linkGraph;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public boolean equals(Object otherObj) {
        if (otherObj == null)
            return false;
        if (otherObj == this)
            return true;

        if (otherObj instanceof pCollection) {
            pCollection otherpCollection = (pCollection) otherObj;
            if (this.id != null && this.id.equals(otherpCollection.getId())) {
                return true;
            }
        }
        return false;
    }
}
