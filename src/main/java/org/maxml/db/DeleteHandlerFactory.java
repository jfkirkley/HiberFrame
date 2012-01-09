package org.maxml.db;

import java.util.Collection;
import java.util.Iterator;

import org.maxml.util.FileUtils;
import org.maxml.util.Util;
import org.maxml.util.MultiRegisteredFactory;

public class DeleteHandlerFactory extends MultiRegisteredFactory {

    private static final String                  PROPERTY_FILE = Util.RD + "/DeleteHandlers.properties";

    private static DeleteHandlerFactory instance      = null;

    public static DeleteHandlerFactory getInstance() {
        if(instance==null){
            instance = new DeleteHandlerFactory();
        }
        if(instance!=null){
            // TODO remove this for production
            instance.registerFromProperties(FileUtils.i().loadProperties(PROPERTY_FILE));
        }
        return instance;
    }

    public DeleteHandlerFactory() {
        super(PROPERTY_FILE);
    }

    public Collection<DeleteHandler> getBuilders(Object key) {
        return (Collection<DeleteHandler>) super.get(key);
    }

    public static boolean fireDeleteHandlers(Object key, Class targetType, Object targetId, Object otherObj) {
        Collection<DeleteHandler> builders = getInstance().getBuilders(key);
        if (builders != null) {
            for ( DeleteHandler specialPropertiesBuilder: builders ) {
                specialPropertiesBuilder.delete(targetId, targetType, otherObj);
            }
            return true;
        }
        return false;
    }

}
