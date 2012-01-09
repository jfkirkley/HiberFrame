package org.maxml.db.types.links;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.DefaultDeleteHandler;

public class LinkedObjectDeleteHandler extends DefaultDeleteHandler {

    private static DBObjectAccessorFactory objectAccessorFactory=DBObjectAccessorFactory.i();

    private int referrerType; 
    private int referentType;
    
    private Integer referentId = null;
    private Integer referrerId = null;
    
    public LinkedObjectDeleteHandler(int referrerType, int referentType) {
        this.referrerType = referrerType;
        this.referentType = referentType; 
    }
    
    public LinkedObjectDeleteHandler(Class referrerTypeClass, Class referentTypeClass) {
        this((int)objectAccessorFactory.getTypeIdForClass(referrerTypeClass),
                (int)objectAccessorFactory.getTypeIdForClass(referentTypeClass));
    }
    
    public LinkedObjectDeleteHandler(Object referrer, Object referent) {
        this(referrer.getClass(),referent.getClass());
    }
    
    public LinkedObjectDeleteHandler(Object referrer, Class referentClass) {
        this(referrer.getClass(),referentClass);
        this.referrerId = (Integer)objectAccessorFactory.getObjectId(referrer);
    }
        
    public LinkedObjectDeleteHandler(Class referrerClass, Object referent) {
        this(referrerClass,referent.getClass());
        this.referentId = (Integer)objectAccessorFactory.getObjectId(referent);
    }
            
    public LinkedObjectDeleteHandler(Integer referrerId, Class referrerClass, Class referentClass) {
        this(referrerClass,referentClass);
        this.referrerId = referrerId;
    }
        
    public LinkedObjectDeleteHandler(Class referrerClass, Class referentClass, Integer referentId) {
        this(referrerClass,referentClass);
        this.referentId = referentId;
    }
            
    public void delete(Object targetId, Class targetType, Object infoObj) {

    }

    public void delete(Object target) throws DBException {
        if(referentId != null){
            Integer referrerId = (Integer)objectAccessorFactory.getObjectId(target);
            LinkHandler.getInstance().delete(referrerId,referrerType, referentId, referentType);
        } else if(referrerId != null) {
            Integer referentId = (Integer)objectAccessorFactory.getObjectId(target);
            LinkHandler.getInstance().delete(referrerId,referrerType, referentId, referentType);
        }

        super.delete(target);
    }
}
