package org.maxml.reflect;

import java.util.Collection;
import java.util.Iterator;

import org.maxml.util.ClassUtils;
import org.maxml.util.Util;

public class CollectionReflectionProvider implements ReflectionProvider {

    private Collection collection;
    private boolean    doCreate;

    public CollectionReflectionProvider(Collection collection, boolean doCreate) {
        this.collection = collection;
        this.doCreate = doCreate;
    }

    public Object get(String property) {
        Integer index = 0;
        if (doCreate) {
            NameAndType nameAndType = new NameAndType((String) property);
            index = Integer.parseInt(nameAndType.getName());
            if (collection.size() <= index && nameAndType.getType() != null) {
                for (int i = collection.size(); i <= index; ++i) {
                    collection.add(ClassUtils.i().createNewObjectOfType(nameAndType.getType()));
                }
            }
//            else if( index == -1 ) {
//                collection.add(ClassUtils.i().createNewObjectOfType(nameAndType.getType()));
//            }
        } else {
            index = Integer.parseInt(property);
        }
        return Util.i().getNthElement(collection, index);
    }

    public void set(String property, Object value) {
        collection.add(value);
    }

    public void remove(String id) {
        Integer index = Integer.parseInt(id);
        Object element = Util.i().getNthElement(collection, index);
        if (element != null) {
            collection.remove(element);
        }
    }

    public void merge(Object thisObject, boolean overwrite) {
        Collection otherCollection = (Collection) thisObject;
        if (overwrite) {
            collection.clear();
            collection.addAll(otherCollection);
        } else {
            for (Iterator iter = otherCollection.iterator(); iter.hasNext();) {
                Object elem = iter.next();
                if (!collection.contains(elem)) {
                    collection.add(elem);
                } else {
                    Object thisElem = Util.i().getCollectionElement(collection, elem);
                    ReflectionProviderFactory.i().merge(elem, thisElem, overwrite);
                }
            }
        }
    }
}
