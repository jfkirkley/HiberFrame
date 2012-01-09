package org.maxml.db;

public interface DeleteHandler {
    
    public void delete(Object targetId, Class targetType, Object infoObj);

    public void delete(Object target) throws DBException;
}
