package org.maxml.db.types.links;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//import org.maxml.app.fei.obj.metadata.AttributeDef;
//import org.maxml.app.fei.obj.metadata.AttributeValue;
import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.types.Definition;
import org.maxml.util.Util;

public class lCollection implements Collection {
    protected DBObjectAccessorFactory objectAccessorFactory = DBObjectAccessorFactory.i();

    transient private HashMap<Link,Object>         link2ObjMap;
    private LinkGraph                 parent;
    private boolean                   lazy                  = false;
    private boolean                   initialized           = false;
    transient private ArrayList       orderList;

    public lCollection(Object linkedToMe) throws DBException {
        this(linkedToMe, true);
    }
    
    public lCollection(Object linkedToMe, boolean lazy) throws DBException {
        this(new LinkGraph(LinkHandler.getInstance().findLink(new Link(linkedToMe,false))), lazy);
    }
    
    
    public lCollection() {
        this(new LinkGraph(new Link()));
        try {
            objectAccessorFactory.getAccessor(Link.class).save(parent);
            objectAccessorFactory.flush();
        } catch (DBException e) {
            throw new RuntimeException(e);
        }
    }

    public lCollection(LinkGraph linkGraph) {
        this.link2ObjMap = new HashMap();
        this.parent = linkGraph;
        this.orderList = new ArrayList();
    }

    public static lCollection build(LinkGraph parent, boolean lazy) {
        try {
            return new lCollection(parent, lazy);
        } catch (DBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public lCollection(LinkGraph parent, boolean lazy) throws DBException {
        this.link2ObjMap = new HashMap();
        this.parent = parent;
        this.orderList = new ArrayList();

        this.lazy = lazy;
        if (parent.getRootLink() != null) {
            Collection<Link> childlinks = LinkHandler.getInstance().getSubLinks(parent.getRootLink());
            for ( Link link: childlinks ) {
                Object val = null;
                if (!lazy) {
                    if (link.getReferentType() != null && link.getReferentId() != null) {
                        val = objectAccessorFactory.getAccessor(link.getReferentType()).find(link.getReferentId());
                        if (val != null) {
                            this.orderList.add(val);
                        }
                    }
                }
                this.link2ObjMap.put(link, val);
            }
        }
    }

    public void readAll() throws DBException {
        if (!initialized) {
            HashMap<Link,Object> tmpMap = new HashMap<Link,Object>();
            for ( Link link: link2ObjMap.keySet() ) {
                Object val = null;
                if (link.getReferentType() != null && link.getReferentId() != null) {
                    val = objectAccessorFactory.getAccessor(link.getReferentType()).find(link.getReferentId());
                    tmpMap.put(link, val);
                }
            }
            orderList.clear();
            for ( Link link: tmpMap.keySet() ) {
                Object val = tmpMap.get(link);
                this.link2ObjMap.put(link, val);
                this.orderList.add(val);
            }
        }
        initialized = true;
    }

    public int size() {
        return link2ObjMap.size();
    }

    public boolean isEmpty() {
        return link2ObjMap.isEmpty();
    }

    public boolean contains(Object o) {
        return link2ObjMap.containsValue(o);
    }

    public Iterator iterator() {
        // return lazy ? new lIterator(link2ObjMap) :
        // orderList.iterator();//link2ObjMap.values().iterator();
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
            Object id = objectAccessorFactory.getAccessor(o.getClass()).save(o);
            // objectAccessorFactory.flush();
            Link l = new Link(parent.getRootLink(), parent.getRootLink().getId(), o, (Integer) id);
            parent.addChildLink(l);
            objectAccessorFactory.getAccessor(Link.class).save(l);
            link2ObjMap.put(l, o);
            orderList.add(o);
            return true;
        } catch (DBException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean remove(Object o) {
        Link link = (Link) Util.i().findKeyForValue(link2ObjMap, o);
        if (link != null) {
            try {
                objectAccessorFactory.getAccessor(Link.class).delete(link);
                objectAccessorFactory.getAccessor(o.getClass()).delete(o);
                link2ObjMap.remove(link);
                orderList.remove(o);
                return true;
            } catch (DBException e) {
                throw new RuntimeException(e);
            }
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
        removeAll(link2ObjMap.values());
    }
    
    public Collection getReadObjects() {
        List readObjs = new ArrayList();
        for ( Link link: link2ObjMap.keySet() ) {
            Object object = link2ObjMap.get(link);
            if( object != null ) {
                readObjs.add(object);
            }
        }
        return readObjs;
    }

    public static void main(String[] a) {
        try {

//            ArrayList al = new ArrayList();
//
//            al.add(new AttributeDef("attr.type.01"));
//            al.add(new AttributeDef("attr.type.02"));
//            al.add(new AttributeDef("attr.type.03"));
//
//            for (Iterator iter = al.iterator(); iter.hasNext();) {
//                AttributeDef attributeDef = (AttributeDef) iter.next();
//                DBObjectAccessorFactory.i().getAccessor(AttributeDef.class).save(attributeDef);
//            }

//            ArrayList al2 = new ArrayList();
//            al2.add(new AttributeValue((Definition) al.get(0), "attr.value.01"));
//            al2.add(new AttributeValue((Definition) al.get(1), "attr.value.02"));
//            al2.add(new AttributeValue((Definition) al.get(2), "attr.value.03"));
//            al2.add(new AttributeValue((Definition) al.get(0), "attr.value.04"));
//            al2.add(new AttributeValue((Definition) al.get(1), "attr.value.05"));
//            al2.add(new AttributeValue((Definition) al.get(2), "attr.value.06"));
//            al2.add(new AttributeValue((Definition) al.get(0), "attr.value.07"));
//            al2.add(new AttributeValue((Definition) al.get(1), "attr.value.08"));
//            al2.add(new AttributeValue((Definition) al.get(2), "attr.value.09"));
//
//            for (Iterator iter = al2.iterator(); iter.hasNext();) {
//                AttributeValue attributeValue = (AttributeValue) iter.next();
//                DBObjectAccessorFactory.getInstance().getAccessor(AttributeDef.class).save(attributeValue);
//            }

//            if (false) {
//                lCollection lc = new lCollection();
//                for (Iterator iter = al2.iterator(); iter.hasNext();) {
//                    AttributeValue attributeValue = (AttributeValue) iter.next();
//                    lc.add(attributeValue);
//                }
//
//            } else {
//
//                lCollection collection = new lCollection(new LinkGraph(
//                        (Link) DBObjectAccessorFactory.getInstance().getAccessor(Link.class).find(1)), true);
//                for (Iterator iter = collection.iterator(); iter.hasNext();) {
//                    AttributeValue element = (AttributeValue) iter.next();
//                    System.out.println(element.getValue());
//                }
//
//                if (false) {
//                    al2.clear();
//                    int i = 0;
//                    for (Iterator iter = collection.iterator(); iter.hasNext();) {
//                        AttributeValue element = (AttributeValue) iter.next();
//                        System.out.println(element.getValue());
//                        if (++i % 2 == 0)
//                            al2.add(element);
//                    }
//
//                    for (Iterator iter = al2.iterator(); iter.hasNext();) {
//                        AttributeValue element = (AttributeValue) iter.next();
//                        collection.remove(element);
//                        System.out.println("removing: " + element.getValue());
//                    }
//                    // collection.remove()
//                    for (Iterator iter = al2.iterator(); iter.hasNext();) {
//                        AttributeValue attributeValue = (AttributeValue) iter.next();
//                        collection.add(attributeValue);
//                    }
//                }
//            }
            DBObjectAccessorFactory.i().endSession();
        } catch (DBException e) {
            e.printStackTrace();
        }
    }
}
