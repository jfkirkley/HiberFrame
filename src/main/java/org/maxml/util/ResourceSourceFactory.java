package org.maxml.util;

import java.util.HashMap;

//import org.maxml.gui.ComponentProvider;


public class ResourceSourceFactory extends RegisteredFactory {
    
    
    
    protected static HashMap cache = new HashMap();

    public ResourceSourceFactory(String propertyFile) {
        super(propertyFile);
    }
    
    public ResourceSource getResourceSource(String spec) {
        return (ResourceSource)super.get(spec);
    }

    public ResourceSource getResourceSource(String spec, String initParams) {
        ResourceSource resourceSource = (ResourceSource) get(spec);
        
        resourceSource.init(initParams);
        
        return resourceSource;

    }
    
    public ResourceSource getAndCache(String spec, String path) {
        path = FileUtils.i().normalize(path);
        ResourceSource resourceSource = (ResourceSource)cache.get(spec+path);
        if( resourceSource==null) {
            resourceSource = getResourceSource(spec, path);
            cache.put(spec+path, resourceSource);
        }

        return resourceSource;
    }


//    public ComponentProvider getRSComponentProvider(String spec) {
//        ComponentProvider componentProvider = (ComponentProvider)  get(spec + ".gui");
//        
//        if(has(spec + ".gui.init")) {
//            Object initObj = get(spec + ".gui.init");
//            componentProvider.init(initObj);
//        }
//        
//        return componentProvider;
//    }
}
