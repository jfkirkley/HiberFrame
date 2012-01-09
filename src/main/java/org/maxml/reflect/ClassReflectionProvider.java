package org.maxml.reflect;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.maxml.util.ClassUtils;

public class ClassReflectionProvider implements ReflectionProvider {
    private Object target;
    private CachedClass cachedClass;
    private boolean doCreate;
    
    public ClassReflectionProvider(Object object, boolean doCreate ) {
        this.target = object;
        this.doCreate = doCreate;
        this.cachedClass = ReflectCache.i().getClassCache(object);
    }
    public Object get(String property) {
        Object object = this.cachedClass.invokeGetMethod(target, property);
        if( doCreate && object== null && cachedClass.hasProperty(property)) {
            Class type = cachedClass.getPropertyType(property);
            Object newObj = null;
            if( type == Collection.class || type == List.class) {
                newObj = new ArrayList();
            } else if( type == Map.class) {
                newObj = new HashMap();
            } else {
                newObj = ClassUtils.i().createNewObjectOfType(type);
            }
            cachedClass.invokeSetMethod(target, property, newObj);
            // incase there is a modification operation on this
            object = this.cachedClass.invokeGetMethod(target, property); 
        }
        return object;
    }
    public void set(String property, Object value) {
        this.cachedClass.invokeSetMethod(target, (String)property, value);
    }
    public void remove(String id) {
        
    }

    public void merge(Object thisObject, boolean overwrite) {
        Collection<String> properties = cachedClass.getProperties();
        for ( String property: properties ) {
            if( overwrite ) {
                Object value = cachedClass.invokeGetMethod(thisObject, property);
                cachedClass.invokeSetMethod(target, property, value);
            } else {
                Object value = cachedClass.invokeGetMethod(target, property);
                if( value == null ) {
                    value = cachedClass.invokeGetMethod(thisObject, property);
                    cachedClass.invokeSetMethod(target, property, value);
                } else {
                    Object otherValue = cachedClass.invokeGetMethod(thisObject, property);
                    ReflectionProviderFactory.i().merge(value, otherValue, overwrite);
                }
            }
        }
        
    }

//    public void merge()
}

