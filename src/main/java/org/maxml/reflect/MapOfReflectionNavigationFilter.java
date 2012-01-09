package org.maxml.reflect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.maxml.util.ClassUtils;
import org.maxml.util.FileUtils;
import org.maxml.util.MultiRegisteredFactory;
import org.maxml.util.Util;
import org.maxml.xpath.ApplyXPath;

public class MapOfReflectionNavigationFilter extends MultiRegisteredFactory implements ReflectionNavigationFilter {

    private Map<Class, ReflectionNavigationFilter> filterMap;
    private Class                                  currentContextType;
    private ReflectionNavigationFilter             currentReflectionNavigationFilter;

    public MapOfReflectionNavigationFilter() {
        filterMap = new HashMap<Class, ReflectionNavigationFilter>();
    }

    public void init(Object info) {
        String specFileName = (String) info;
        if (FileUtils.i().getExtension(specFileName).equals(".properties")) {
            Properties p = FileUtils.i().loadProperties(
                    ReflectionNavigationFilterFactory.FILTER_DEF_PATH + specFileName);

            registerFromProperties(p);

            for ( Class<?> type: class2ElementsRegistry.keySet() ) {
                Object initObj = class2ElementsRegistry.get(type);

                // TODO this should be type independent
                ReflectionNavigationFilter reflectionNavigationFilter = new TypeReflectionNavigationFilter();
                // ReflectionNavigationFilter reflectionNavigationFilter =
                // ReflectionNavigationFilterFactory.i().getReflectionFilter(type);

                reflectionNavigationFilter.init(initObj);

                filterMap.put(type, reflectionNavigationFilter);
            }
        } else if (FileUtils.i().getExtension(specFileName).equals(".xml")) {

            Document specDoc = ApplyXPath.i().getDoc(ReflectionNavigationFilterFactory.FILTER_DEF_PATH + specFileName);

            NodeList specNodes = ApplyXPath.i().getXPathNodeList("//filter", specDoc);
            for (int i = 0; i < specNodes.getLength(); i++) {
                Node n = specNodes.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element specElem = (Element) n;
                    String reflectNavType = specElem.getAttribute("filter-type");
                    String ownerType = specElem.getAttribute("context-type");

                    if (!Util.i().hasContent(reflectNavType)){
                        reflectNavType = "org.maxml.reflect.MethodReflectionNavigationFilter";
                    }

                    try {

                        ReflectionNavigationFilter reflectionNavigationFilter = (ReflectionNavigationFilter) ClassUtils.i().createNewObjectOfType(
                                reflectNavType);
                        reflectionNavigationFilter.init(specElem);
                        Class ownerTypeClass = Class.forName(ownerType);
                        filterMap.put(ownerTypeClass, reflectionNavigationFilter);

                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean include(Class c, String method) {
        if (currentReflectionNavigationFilter != null) {
            return currentReflectionNavigationFilter.include(c, method);
        }
        return true;
    }

    public boolean include(String path) {
        return false;
    }

    public boolean update(Object info) {
        return false;
    }

    public Class getCurrentContextType() {
        return currentContextType;
    }

    public void setCurrentContextType(Class currentContextType) {
        currentContextType = ReflectCache.i().normalizeClass(currentContextType);
        System.out.println("context " + currentContextType.getSimpleName());
        currentReflectionNavigationFilter = filterMap.get(currentContextType);
        this.currentContextType = currentContextType;

    }

    public boolean include(Class c, Method m) {
        // TODO Auto-generated method stub
        return true;
    }

    public void setOwnerType(Class ownerType) {
        ownerType = ReflectCache.i().normalizeClass(ownerType);
        System.out.println("owner " + ownerType.getSimpleName());
        if (currentReflectionNavigationFilter != null) {
            currentReflectionNavigationFilter.setOwnerType(ownerType);
        }
//        for (Iterator iter = filterMap.values().iterator(); iter.hasNext();) {
//            ReflectionNavigationFilter reflectionNavigationFilter = (ReflectionNavigationFilter) iter.next();
//            reflectionNavigationFilter.setOwnerType(parentType);
//        }
    }
}
