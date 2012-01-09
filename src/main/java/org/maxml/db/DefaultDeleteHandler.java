package org.maxml.db;


public class DefaultDeleteHandler implements DeleteHandler {

    public void delete(Object targetId, Class targetType, Object infoObj) {

    }

    public void delete(Object target) throws DBException {

        if(!DBObjectAccessorFactory.i().isAttached(target)) {
            DBObjectAccessorFactory.i().refresh(target);
        }
        DBObjectAccessorFactory.i().delete(target);
    }

}
