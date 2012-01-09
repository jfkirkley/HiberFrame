package org.maxml.reflect;

import java.util.Collection;

import org.maxml.common.CollectionSource;

public class PropertySource implements CollectionSource {

    public Collection getCollection(Object descriptor) {
        if (descriptor instanceof Class) {
            Class clazz = (Class) descriptor;
            CachedClass classCache = ReflectCache.i().getClassCache(clazz);
            return classCache.getProperties();
        }
        return null;
    }

    public Collection getCollection() {
        return null;
    }

    public Collection getCollection(String descriptor) {
        try {
            return getCollection(Class.forName(descriptor));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
