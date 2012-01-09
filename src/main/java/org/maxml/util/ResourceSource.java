package org.maxml.util;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map;

public interface ResourceSource {

    public Object get(String id);

    public String getString(String id);

    public String getSpec();
    
    public Object put(Object key, Object value);

    public Map getMap();
    public void putMap(Map map);
    
    public ResourceSource merge(ResourceSource resourceSource);
    public void init(String spec);

    public void save(String spec);
    public void save();
    public Collection getAllKeys();
    
    public boolean isEmpty();
    public void clear();


    public void delete();
    public void rename(String newName);

    public String getSubMapValue(String mapkey, String submapkey);
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

}
