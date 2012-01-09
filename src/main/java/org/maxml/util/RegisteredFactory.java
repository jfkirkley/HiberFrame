package org.maxml.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class RegisteredFactory {

    protected HashMap class2ElementRegistry;
    private Class   handlerClass = null;

    public RegisteredFactory() {
        this.class2ElementRegistry = new /* definitely no IB !recursion! */HashMap();
    }

    public RegisteredFactory(String propertyFileName) {
        this();
        registerFromProperties(FileUtils.i().loadProperties(propertyFileName));
    }

    public RegisteredFactory(String propertyFileName, Class handlerClass) {
        this();
        this.handlerClass = handlerClass;
        registerFromProperties(FileUtils.i().loadProperties(propertyFileName));
    }

    protected void registerFromProperties(Properties properties) {
        if (properties != null) {
            class2ElementRegistry.clear();
//            for ( String keyOrClassName: properties.keySet() ) {
            for(Iterator iter = properties.keySet().iterator(); iter.hasNext();){
            	String keyOrClassName = (String)iter.next();
                String element = (String) properties.get(keyOrClassName);

                Object key = keyOrClassName;
                try {
                    key = Class.forName(keyOrClassName);
                } catch (ClassNotFoundException e) {
                }
                if (handlerClass == null) {
                    try {
                        register(key, Class.forName(element));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    register(key, element);
                }
            }
        }
    }

    public void register(Object key, Object element) {
        this.class2ElementRegistry.put(key, element);
    }

    public void unregister(Object key) {
        this.class2ElementRegistry.remove(key);
    }

    public void unregisterElement(Class elementClassForKey) {
        Util.i().removeEntriesWithValue(elementClassForKey, class2ElementRegistry);
    }

    public Object get(Object key) {
        if (handlerClass == null) {
            Class elementClass = (Class) this.class2ElementRegistry.get(key);
            if (elementClass != null) {
                return ClassUtils.i().createNewObjectOfType(elementClass);
            }
        } else {
            String param = (String) this.class2ElementRegistry.get(key);
            if (param != null) {
                return ClassUtils.i().createNewObjectOfType(handlerClass, param);
            }
        }
        return null;
    }

    public boolean has(Object key) {
        return this.class2ElementRegistry.containsKey(key);
    }

    public Object getRegisteredValue(Object key) {
        return this.class2ElementRegistry.get(key);        
    }
}
