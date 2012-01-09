package org.maxml.reflect;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.maxml.util.FileUtils;

public class XMLReflectionNavigationFilter implements ReflectionNavigationFilter {
	private HashSet filterClasses = new HashSet();

	public void init(Object info) {
		String resourcePath = ReflectionNavigationFilterFactory.FILTER_DEF_PATH + info;
		Properties classNameProperies = FileUtils.i().loadProperties(resourcePath);
		
		for (Iterator iter = classNameProperies.values().iterator(); iter.hasNext();) {
			String className = iter.next().toString();

			try {
				filterClasses.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	
	public boolean include(Class c, String method) {
		return !filterClasses.contains(c);
	}

	public boolean include(String path) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(Object info) {
		// TODO Auto-generated method stub
		return false;
	}


    public boolean include(Class c, Method m) {
        // TODO Auto-generated method stub
        return false;
    }


    public void setOwnerType(Class parentType) {
        // TODO Auto-generated method stub
        
    }


	public void setCurrentContextType(Class currentContextType) {
		// TODO Auto-generated method stub
		
	}

}
