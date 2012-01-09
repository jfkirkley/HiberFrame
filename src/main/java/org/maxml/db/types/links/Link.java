package org.maxml.db.types.links;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.reflect.CachedClass;
import org.maxml.reflect.ReflectCache;

public class Link {
    public final static char                 MATCH_REFERENT        = 't';                                  // extension
    public final static char                 MATCH_REFERRER        = 'r';                                  // extension
    public final static char                 MATCH_ID              = 'a';                                  // extension

    private static Integer                   linkTypeId            = null;
    public static char                       NORMAL                = 'n';
    public static char                       EXT                   = 'x';                                  // extension
    public static char                       P2P                   = 'p';                                  // pointer
                                                                                                            // 2
                                                                                                            // profile
    public static char                       P2ST                  = 's';                                  // pointer
                                                                                                            // 2
                                                                                                            // subtree

    protected static DBObjectAccessorFactory objectAccesskorFactory = DBObjectAccessorFactory.i();
    LinkHandler                              linkHandler           = LinkHandler.getInstance();
    private Integer                          id;

    private char                             type                  = NORMAL;
    private Integer                          referentId;
    private Integer                          referentType;
    private Integer                          referrerId;
    private Integer                          referrerType;

    private String                           name;

    private static DBObjectAccessorFactory getDBObjectAccessorFactory() {
    	if(objectAccesskorFactory==null){
    		objectAccesskorFactory=DBObjectAccessorFactory.i();
    	}
    	return objectAccesskorFactory;
    }
    
    public Link() {
    }

    public Link(char type) {
        this.type = type;
    }

    public Link(Object target, boolean isReferrer) {
        if (isReferrer) {
            setObjectAsReferrer(target);
        } else {
            setObjectAsReferent(target);
        }
    }

    public Link(Object referrer, Object referent) {
        setObjectAsReferrer(referrer);
        setObjectAsReferent(referent);
    }

    public Link(Object referrer, Integer referrerId, Object referent, Integer referentId) {
        setObjectAsReferent(referent, referentId);
        setObjectAsReferrer(referrer, referrerId);
    }

    public Link(Object referrer) {
        setObjectAsReferrer(referrer);
    }

    public void setObjectAsReferent(Object referent, Integer id) {
        this.referentId=id;
        if(referentId==null) System.err.println("Link Error: Referent: '" + referent + "' has null id!");
        this.referentType=getDBObjectAccessorFactory().getTypeIdForClass(referent.getClass().getName());
    }
    
    public void setObjectAsReferrer(Object referrer, Integer id) {
        this.referrerId=id;
        if(referrerId==null) System.err.println("Link Error: Referrer: '" + referrer + "' has null id!");
        this.referrerType=getDBObjectAccessorFactory().getTypeIdForClass(referrer.getClass().getName());
    }
    
    public void setObjectAsReferrer(Object referrer) {
        Class referrerClass = referrer.getClass();
        String idPropertyName = getDBObjectAccessorFactory().getTypeIdPropertyName(referrerClass);
        CachedClass cachedClass = ReflectCache.i().getClassCache(referrerClass);
        referrerId = (Integer) cachedClass.invokeGetMethod(referrer, idPropertyName);
        if(referrerId==null) System.err.println("Link Error: Referrer: '" + referrer + "' has null id!");
        referrerType = getDBObjectAccessorFactory().getTypeIdForClass(referrerClass.getName());
    }

    public void setObjectAsReferent(Object referent) {
        Class referentClass = referent.getClass();
        String idPropertyName = getDBObjectAccessorFactory().getTypeIdPropertyName(referentClass);
        CachedClass cachedClass = ReflectCache.i().getClassCache(referentClass);
        referentId = (Integer) cachedClass.invokeGetMethod(referent, idPropertyName);
        if(referentId==null) System.err.println("Link Error: Referent: '" + referent + "' has null id!");
        referentType = getDBObjectAccessorFactory().getTypeIdForClass(referentClass.getName());
    }

    public Link(Integer referentId, Integer referentType, Integer referrerId, Integer referrerType) {
        this(referentId, referentType, referrerId, referrerType, NORMAL);
    }

    public Link(Integer referentId, Integer referentType, Integer referrerId, Integer referrerType, char type) {
        this.referentId = referentId;
        this.referentType = referentType;
        this.referrerId = referrerId;
        this.referrerType = referrerType;
        this.type = type;
    }

    public Link(Link link) {
        this(link.getReferentId(), link.getReferentType(), link.getReferrerId(), link.getReferrerType(), link.getType());
        setName(link.getName());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReferentId() {
        return referentId;
    }

    public void setReferentId(Integer referentId) {
        this.referentId = referentId;
    }

    public Integer getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(Integer referrerId) {
        this.referrerId = referrerId;
    }

    public Integer getReferentType() {
        return referentType;
    }

    public void setReferentType(Integer referentType) {
        this.referentType = referentType;
    }

    public Integer getReferrerType() {
        return referrerType;
    }

    public void setReferrerType(Integer referrerType) {
        this.referrerType = referrerType;
    }

    public boolean nullReferent() {
        return referentId == null && referentType == null;
    }

    public boolean nullReferrer() {
        return referrerId == null && referrerType == null;
    }

    public boolean sameReferent(Link otherLink) {
        if (nullReferent()) {
            return otherLink.nullReferent();
        }
        return otherLink.getReferentId().equals(referentId) && otherLink.getReferentType().equals(referentType);
    }

    public boolean sameReferrer(Link otherLink) {
        if (nullReferrer()) {
            return otherLink.nullReferrer();
        }
        return otherLink.getReferrerId().equals(referrerId) && otherLink.getReferrerType().equals(referrerType);
    }

    public String toString() {
        return id + "(" + type + "): " + referentId + " " + referentType + " " + referrerId + " " + referrerType;
    }

    public boolean matches(Integer id, Integer type) {

        if (id.equals(this.id) && type.equals(getTypeId())) {
            return true;
        }
        return false;
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (o instanceof Link) {
            Link otherLink = (Link) o;

            if (otherLink.getId() != null && otherLink.getId().equals(id)) {
                return true;
            }
            return sameReferrer(otherLink) && sameReferent(otherLink);
        }
        return false;
    }

    public static Integer getTypeId() {
        if (linkTypeId == null) {
            linkTypeId = DBObjectAccessorFactory.i().getTypeIdForClass(Link.class.getName());
        }
        return linkTypeId;
    }

    public Link clone() {
        return new Link(this);
    }

    public Element getXMLRep(Document doc) {
    	Element rep = doc.createElement("link");
    	if(id!=null) {
    		rep.setAttribute("id", id.toString());
    	}
    	if(!nullReferent()) {
        	Element outElem = doc.createElement("out");
    		outElem.setAttribute("id", referentId.toString());
    		outElem.setAttribute("type", referentType.toString());
    		rep.appendChild(outElem);
    	}
    	if(!nullReferrer()) {
        	Element inElem = doc.createElement("in");
    		inElem.setAttribute("id", referrerId.toString());
    		inElem.setAttribute("type", referrerType.toString());
    		rep.appendChild(inElem);
    	}
    	
    	return rep;
    }
    
    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public static boolean isExtLink(Link link) {
        return link.getType() == EXT;
    }

    public static boolean isP2PLink(Link link) {
        return link.getType() == P2P;
    }

    public static boolean isP2STLink(Link link) {
        return link.getType() == P2ST;
    }

    public static boolean isNormalLink(Link link) {
        return link.getType() == NORMAL;
    }

    public boolean isExtLink() {
        return type == EXT;
    }

    public boolean isP2PLink() {
        return type == P2P;
    }

    public boolean isP2STLink() {
        return type == P2ST;
    }

    public boolean isNormalLink() {
        return type == NORMAL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getReferentObj() {
        try {
            return linkHandler.getReferentObject(this);
        } catch (DBException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getReferredObj() {
        try {
            return linkHandler.getReferredObject(this);
        } catch (DBException e) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveNewReferrer(Object o) {

        try {
            Integer id = (Integer)getDBObjectAccessorFactory().getAccessor(o.getClass()).save(o);
            //objectAccessorFactory.flush();
            setObjectAsReferrer(o, id);
            getDBObjectAccessorFactory().getAccessor(Link.class).save(this);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public void saveNewReferent(Object o) {

        try {
            Integer id = (Integer)getDBObjectAccessorFactory().getAccessor(o.getClass()).save(o);
            //objectAccessorFactory.flush();
            setObjectAsReferent(o, id);
            getDBObjectAccessorFactory().getAccessor(Link.class).save(this);
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    public static Link makeNewLink() {
        try {
            Link link = new Link();
            getDBObjectAccessorFactory().getAccessor(Link.class).save(link);
            //objectAccessorFactory.flush();
            return link;
        } catch (DBException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static Link makeNewLink(Object referrerObj) {
        try {
            Link link = new Link(referrerObj,true);
            getDBObjectAccessorFactory().getAccessor(Link.class).save(link);
            //objectAccessorFactory.flush();
            return link;
        } catch (DBException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    
}

