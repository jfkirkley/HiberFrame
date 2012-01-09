package org.maxml.reflect;

public interface ReflectionProvider {
    public Object get(String property);
    public void set(String property, Object value);
    public void remove(String id);
    public void merge(Object thisObject, boolean overwrite);
}


