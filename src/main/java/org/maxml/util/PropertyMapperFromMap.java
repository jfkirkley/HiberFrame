package org.maxml.util;

// import org.hibernate.*;
// import org.hibernate.criterion.Expression;
import java.util.*;
// import util.HibernateUtil;

import org.maxml.propertymappers.PropertyMapper;
import org.maxml.reflect.CachedClass;





public class PropertyMapperFromMap extends PropertyMapper {

    public PropertyMapperFromMap() { super(); }

    private String makeGetMethodName( String propertyName ) {
		return "get" + propertyName.substring(0,1).toUpperCase() + 
			propertyName.substring(1);
    }

    private String makeSetMethodName( String propertyName ) {
    	return "set" + propertyName.substring(0,1).toUpperCase() + 
    	    propertyName.substring(1);
	}


    public Object createAndMap( String targetClassName, HashMap map ) {
		Object target = ClassUtils.i().createNewObjectOfType( targetClassName );
		map( map, target );
		return target;
    }

    public Object map( Object source, Object target   ) {
		
		HashMap map = (HashMap) source;
		CachedClass targetCachedClass = reflectCache.getClassCache( target.getClass() );

		Iterator iter = map.keySet().iterator();
		while(iter.hasNext() ) {
			String propertyName = (String)iter.next();
	    
			targetCachedClass.invokeSetMethod( target, propertyName, map.get(propertyName) );
		}
		return target;
    }
}
