package org.maxml.util;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


public class PropertyFileResourceSource implements ResourceSource {

    private Properties properties=null;
    private String fileName;
    
    public PropertyFileResourceSource() {
        
    }
    
    public PropertyFileResourceSource(String propertyFileName) {
        this.properties = FileUtils.i().loadProperties(propertyFileName);
    }
    
    public Object get(String id) {
        return properties.get(id);
    }

    public String getString(String id) {
        return (String)properties.get(id);
    }

    public void init(String spec) {
        this.fileName = spec + ".properties";
        if( FileUtils.i().exists(this.fileName)) {
            this.properties = FileUtils.i().loadProperties(this.fileName);
        } else {
            this.properties = new Properties();
        }
    }
    
    public boolean isEmpty() {
        return (this.properties == null) || this.properties.size() == 0;
    }

    public Collection getAllKeys() {
        return properties.keySet();
    }

    public void save(String spec) {
        this.fileName = spec + ".properties";
        FileUtils.i().saveProperties(this.properties, this.fileName);
    }
    
    public void save(){
        FileUtils.i().saveProperties(this.properties, this.fileName);
    }

    public Object put(Object key, Object value) {
        if( properties != null ) {
            return properties.put(key, value);
        }
        return null;
    }

    public Map getMap() {
        return properties;
    }

    public ResourceSource merge(ResourceSource resourceSource) {
        Map map = resourceSource.getMap();
        putMap(map);
        return this;
    }

    public void clear() {
        properties.clear();
    }
    
    public String getSpec() { return fileName; }

    public void putMap(Map map) {
 
        for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            properties.put(key, map.get(key) );
        }
        
    }

    public void delete() {
        FileUtils.i().delete(this.fileName);
    }

    public void rename(String newName) {
        delete();
        save(newName);
    }

    public String getSubMapValue(String mapkey, String submapkey) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        
    }
}
