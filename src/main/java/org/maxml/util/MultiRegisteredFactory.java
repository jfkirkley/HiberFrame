package org.maxml.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class MultiRegisteredFactory {

	protected CollectionMap<Class<?>, Collection<?>> class2ElementsRegistry;
	
    public MultiRegisteredFactory() {
        this.class2ElementsRegistry = new CollectionMap();
    }

    public MultiRegisteredFactory(String propertyFileName) {
        this();
        registerFromProperties(FileUtils.i().loadProperties(propertyFileName));
    }
    
    protected void registerFromProperties(Properties properties) {
        if( properties != null ) {
            class2ElementsRegistry.clear();
            for ( Iterator iter = properties.keySet().iterator(); iter.hasNext(); ) {
                String keyClassName = (String) iter.next();
                String elementClassNames = (String) properties.get(keyClassName);
                String [] elementClassNameList = elementClassNames.split( "," );
                Class<?> keyClass = null;
                
                try {
                    keyClass = Class.forName(keyClassName);
                    for (int i = 0; i < elementClassNameList.length; i++) {
                        register(keyClass, Class.forName(elementClassNameList[i]));
                    }
                } catch (ClassNotFoundException e) {
                    if( keyClass == null ) {
                        for (int i = 0; i < elementClassNameList.length; i++) {
                            try {
                                register(Class.forName(keyClassName), Class.forName(elementClassNameList[i]));
                            } catch (ClassNotFoundException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
                
            }
        }
    }
	public void register(Class<?> key, Class elementClassForKey) {
		this.class2ElementsRegistry.putSubValue(key, elementClassForKey);
	}
	
	public Collection get(Object key) {
		ArrayList<Object> elements = new ArrayList<Object>();
		Collection<Class<?>> c = (Collection) this.class2ElementsRegistry.get(key);
		if( c == null ) return null;
		for ( Class<?> elementClass: c ) {
			elements.add(ClassUtils.i().createNewObjectOfType( elementClass ) );
		}
		return elements; 
	}

}
