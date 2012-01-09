package org.maxml.db.types.links;

import java.util.Map;
import java.util.Iterator;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;

public class lIterator implements Iterator {
    protected DBObjectAccessorFactory objectAccessorFactory = DBObjectAccessorFactory.i();
    
    private Map link2ObjMap;
    private Iterator keyIterator;
    public lIterator( Map link2ObjMap ) {
        this.link2ObjMap = link2ObjMap;
        this.keyIterator = link2ObjMap.keySet().iterator();
    }
    
    public boolean hasNext() {
        return keyIterator.hasNext();
    }

    public Object next() {
        Link nextLink = (Link)keyIterator.next();
        Object val = link2ObjMap.get( nextLink );
        if( val == null) {
            if (nextLink.getReferentType() != null && nextLink.getReferentId() != null) {
                try {
                    val = objectAccessorFactory.getAccessor(nextLink.getReferentType()).find(nextLink.getReferentId());
                    link2ObjMap.put(nextLink, val);
                } catch (DBException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return val;
    }

    public void remove() {

    }

}
