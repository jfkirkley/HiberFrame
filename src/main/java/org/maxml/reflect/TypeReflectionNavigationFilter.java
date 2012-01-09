package org.maxml.reflect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.maxml.util.Util;

public class TypeReflectionNavigationFilter implements ReflectionNavigationFilter {

    private Set<Class> ignoreClasses;
    
    public TypeReflectionNavigationFilter() {
        ignoreClasses = new HashSet();
    }
    public TypeReflectionNavigationFilter(Class[] classes) {
        ignoreClasses = new HashSet(Util.i().makeListFromArray(classes));
    }
    public TypeReflectionNavigationFilter(Class clas) {
        ignoreClasses = new HashSet();
        ignoreClasses.add(clas);
    }
    
    public void init(Object info) {
        if (info instanceof Collection) {
            Collection classes = (Collection) info;
            ignoreClasses.addAll(classes);
        }
    }

    public boolean include(Class c, String method) {
        return !ignoreClasses.contains(c);
    }

    public boolean include(String path) {
        return false;
    }

    public boolean update(Object info) {
        return false;
    }
    public boolean include(Class c, Method m) {
        // TODO Auto-generated method stub
        return false;
    }
    public void setOwnerType(Class parentType) {
        // TODO Auto-generated method stub
        
    }
	public void setCurrentContextType(Class currentContextType) {
		// TODO Auto-generated method stub
		
	}

}
