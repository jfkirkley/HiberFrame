package org.maxml.db.types;

import java.util.HashSet;
import java.util.Set;

import org.maxml.db.DBObjectAccessor;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.db.DBSessionInterfaceFactory;
import org.maxml.db.DBUtils;
import org.maxml.dispatch.Caller;
import org.maxml.dispatch.Criteria;
import org.maxml.dispatch.CriteriaException;
import org.maxml.dispatch.Dispatcher;

import org.maxml.reflect.CachedClass;
import org.maxml.reflect.ReflectCache;
import org.maxml.util.Util;


public class dCRUD {
    
    protected Dispatcher dispatcher;
    protected Set dirtyObjs;
    protected Set dirtyClasses;
    protected DBObjectAccessorFactory objectAccessorFactory = DBObjectAccessorFactory.i();
    
    public dCRUD() {
        this.dispatcher = new Dispatcher();
        this.dirtyObjs = new HashSet();
        this.dirtyClasses = new HashSet();
    }
    
    public void addCaller(Criteria criteria, Caller caller) {
        dispatcher.addCaller(criteria, caller);
    }
    
    protected void inform(String methodCalled) {
        Object [] info = new Object[3];
        
        dirtyObjs.addAll(DBSessionInterfaceFactory.getInstance().getDirtyObs());
        dirtyClasses.addAll(DBSessionInterfaceFactory.getInstance().getDirtyClasses());
        info[0] = methodCalled;
        info[1] = dirtyClasses;
        info[2] = dirtyObjs;
        
        try {
            dispatcher.dispatch(info);
        } catch (CriteriaException e) {
            e.printStackTrace();
        }
    }
    
    protected void start() {
        DBSessionInterfaceFactory.getInstance().startRecording();
    }
    
    protected void stop(String methodCalled) {
        inform(methodCalled);
        DBSessionInterfaceFactory.getInstance().stopRecording();
    }

    protected void addDirtyObj( Object obj) {
        Class objClass = obj.getClass();
        String idPropertyName = objectAccessorFactory.getTypeIdPropertyName(objClass);
        CachedClass cachedClass = ReflectCache.i().getClassCache(objClass);
        Integer objId = (Integer) cachedClass.invokeGetMethod(obj, idPropertyName);
        Integer objType = objectAccessorFactory.getTypeIdForClass(objClass.getName());
        
        dirtyClasses.add(objType);
        dirtyObjs.add(DBUtils.encodeTypeAndId(objType, objId) );
    }
    
    
    public DBObjectAccessor getAccessor(Class objectClass) {
        return objectAccessorFactory.getAccessor(objectClass);
    }
}
