package org.maxml.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.maxml.db.types.DefaultUpdateHandler;

public class DBUpdateInfo {

    private Collection updateObjs;
    private Collection removeObjs;
    
    private Map<Object,DeleteHandler> deleteHandlerMap=new HashMap<Object,DeleteHandler>();
    private Map<Object,UpdateHandler> updateHandlerMap=new HashMap<Object,UpdateHandler>();
    
    private DefaultUpdateHandler defaultUpdateHandler = new DefaultUpdateHandler();
    private DefaultDeleteHandler defaultDeleteHandler = new DefaultDeleteHandler();

    public DBUpdateInfo() {
        this(new HashSet(), new HashSet());
    }

    public DBUpdateInfo(Collection updateObjs, Collection removeObjs) {
        this.updateObjs = updateObjs;
        this.removeObjs = removeObjs;
    }

    public Object addDeleteHandler(Class type, DeleteHandler deleteHandler) {
        this.deleteHandlerMap.put(type, deleteHandler);
        return deleteHandler;
    }
    
    public Object addUpdateHandler(Class type, UpdateHandler updateHandler) {
        this.updateHandlerMap.put(type, updateHandler);
        return updateHandler;
    }
    
    public Object addDeleteHandler(Object target, DeleteHandler deleteHandler) {
        this.deleteHandlerMap.put(target, deleteHandler);
        return deleteHandler;
    }
    
    public Object addUpdateHandler(Object target, UpdateHandler updateHandler) {
        this.updateHandlerMap.put(target, updateHandler);
        return updateHandler;
    }
        
    public Collection getUpdateObjs() {
        return updateObjs;
    }

    public void setUpdateObjs(Collection updateObjs) {
        this.updateObjs = updateObjs;
    }

    public Collection getRemoveObjs() {
        return removeObjs;
    }

    public void setRemoveObjs(Collection removeObjs) {
        this.removeObjs = removeObjs;
    }

    public Object addUpdateObj(Object obj) {
        this.updateObjs.add(obj);
        return obj;
    }

    public Object addRemoveObj(Object obj) {
        this.removeObjs.add(obj);
        return obj;
    }

    public void doUpdate() throws DBException {

        if (updateObjs != null && updateObjs.size() > 0) {
            for (Iterator iter = updateObjs.iterator(); iter.hasNext();) {
                Object object = iter.next();

                if(updateHandlerMap.containsKey(object)) {
                    updateHandlerMap.get(object).update(object);
                } else if(updateHandlerMap.containsKey(object.getClass())) {
                    updateHandlerMap.get(object.getClass()).update(object);
                } else {
                    defaultUpdateHandler.update(object);
                }
            }
        }
        if (removeObjs != null && removeObjs.size() > 0) {
            for (Iterator iter = removeObjs.iterator(); iter.hasNext();) {
                Object object = iter.next();

                if(deleteHandlerMap.containsKey(object)) {
                    deleteHandlerMap.get(object).delete(object);
                } else if(deleteHandlerMap.containsKey(object.getClass())) {
                    deleteHandlerMap.get(object.getClass()).delete(object);
                } else {
                    defaultDeleteHandler.delete(object);
                }
            }
        }
    }
}
