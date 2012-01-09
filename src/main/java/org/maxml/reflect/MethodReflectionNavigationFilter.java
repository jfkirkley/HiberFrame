package org.maxml.reflect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.maxml.util.Util;
import org.maxml.xpath.ApplyXPath;

public class MethodReflectionNavigationFilter implements ReflectionNavigationFilter {

    private Set           blockedMethods;
    private Map ownerClass2MethodSetMap;

    public MethodReflectionNavigationFilter() {
        this.ownerClass2MethodSetMap = new HashMap();
    }

    public boolean include(Class c, Method method) {
        return (blockedMethods != null) ? !blockedMethods.contains(method) : true;
    }

    public void setOwnerType(Class ownerType) {
        ownerType = ReflectCache.i().normalizeClass(ownerType);
        
        blockedMethods = (Set) this.ownerClass2MethodSetMap.get(ownerType);
    }

    public void init(Object info) {

        Element specElem = (Element) info;

        NodeList classNodes = ApplyXPath.i().getXPathNodeList("./class", specElem);
        
        for (int i = 0; i < classNodes.getLength(); i++) {
            Element classElem = (Element)classNodes.item(i);

            String type = classElem.getAttribute("type");
            NodeList methodNodes = ApplyXPath.i().getXPathNodeList("./method", classElem);
            
            HashSet methodNames = new HashSet();            

            for (int j = 0; j < methodNodes.getLength(); j++) {
                Node mn = methodNodes.item(j);

                if (mn.getNodeType() == Node.ELEMENT_NODE) {
                    Element methodSpecElem = (Element) mn;
                    String methodName = ApplyXPath.i().getElementContent(methodSpecElem);
                    methodNames.add(methodName);
                }
            }
            
            try {
                Class typeClass = Class.forName(type);
                ownerClass2MethodSetMap.put(typeClass, methodNames);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }

    public boolean include(Class c, String method) {
        return (blockedMethods!=null)?!blockedMethods.contains(method):true;
    }

    public boolean include(String path) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean update(Object info) {
        // TODO Auto-generated method stub
        return false;
    }

	public void setCurrentContextType(Class currentContextType) {
		// TODO Auto-generated method stub
		
	}

}
