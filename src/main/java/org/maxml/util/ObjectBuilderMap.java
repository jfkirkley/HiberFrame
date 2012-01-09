package org.maxml.util;

import java.util.Properties;


public class ObjectBuilderMap {
    
    Properties mappings;
    
    public ObjectBuilderMap(String filePath) {
        mappings = Util.i().loadProperties(filePath);
    }
    
    public Object getObject( String name ){
        String className = mappings.getProperty(name);
        if(className==null) return null;
        return ClassUtils.i().createNewObjectOfType(className);
    }

}
