package org.maxml.reflect;

import java.lang.reflect.Method;

public interface ReflectionNavigationFilter {
	public void init(Object info);	
    public boolean include(Class c, String method);
    public boolean include(Class c, Method m);
    public void setOwnerType(Class parentType);
	public boolean include(String path);
	public boolean update(Object info);
    public void setCurrentContextType(Class currentContextType);

}
