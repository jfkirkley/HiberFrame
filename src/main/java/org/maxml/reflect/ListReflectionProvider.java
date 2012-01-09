package org.maxml.reflect;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.maxml.util.ClassUtils;
import org.maxml.util.Util;

public class ListReflectionProvider implements ReflectionProvider {
    
    private List list;
    private boolean doCreate;
    public ListReflectionProvider(List List, boolean doCreate) {
        this.list = List;
        this.doCreate = doCreate;
    }
    public Object get(String property) {
        Integer index = 0;
        if( doCreate ) {
            NameAndType nameAndType = new NameAndType((String)property);
            index = Integer.parseInt(nameAndType.getName());
            if( list.size() <= index && nameAndType.getType()!=null) {
                for(int i = list.size(); i <= index; ++i) {
                    list.add(ClassUtils.i().createNewObjectOfType(nameAndType.getType()));
                }
            }
        } else {
            index = Integer.parseInt(property);
        }
        if( index >= 0 && index < list.size() ) {
            return list.get(index); 
        }
        return null;
    }
    public void set(String property, Object value) {
        Integer index = Integer.parseInt(property);
        list.add(index, value);
    }
    public void remove(String id) {
        Integer index = Integer.parseInt(id);
        if( index >= 0 && index < list.size()) {
            list.remove(index);
        }
    }

    public void merge(Object thisObject, boolean overwrite) {
        Collection otherCollection = (Collection) thisObject;
        if (overwrite) {
            list.clear();
            list.addAll(otherCollection);
        } else {
            for (Iterator iter = otherCollection.iterator(); iter.hasNext();) {
                Object elem = iter.next();
                if (!list.contains(elem)) {
                    list.add(elem);
                } else {
                    Object thisElem = Util.i().getCollectionElement(list,elem);
                    ReflectionProviderFactory.i().merge(elem,thisElem,overwrite);
                }
            }
        }
    }

}


