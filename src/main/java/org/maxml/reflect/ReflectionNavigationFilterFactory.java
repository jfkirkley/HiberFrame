package org.maxml.reflect;


import org.maxml.util.RegisteredFactory;
import org.maxml.util.Util;


public class ReflectionNavigationFilterFactory extends RegisteredFactory {

    private static final String                      PROPS_FILE      = Util.RD + "/XMLReflectionFilters.properties";
    public static final String                       FILTER_DEF_PATH = Util.RD + "/../ReflectionFilters/";

    private static ReflectionNavigationFilterFactory instance        = null;

    public static ReflectionNavigationFilterFactory i() {
        if (instance == null) {
            instance = new ReflectionNavigationFilterFactory();
        }
        return instance;
    }

    public ReflectionNavigationFilterFactory() {
        super(PROPS_FILE);
    }

    // a string key or path is considered to be the init object as well -> this can
    // be a path to a resource for initialization
    public ReflectionNavigationFilter getReflectionFilter(String path) {
        return getReflectionFilter(path, true);
    }

    public ReflectionNavigationFilter getReflectionFilter(Object key) {
        return getReflectionFilter(key, false);
    }

    public ReflectionNavigationFilter getReflectionFilter(Object key, boolean initWithKey) {
        ReflectionNavigationFilter reflectionFilter = (ReflectionNavigationFilter) super.get(key);

        if (initWithKey && reflectionFilter != null) {
            reflectionFilter.init(key);
        }
        return reflectionFilter;
    }

}
