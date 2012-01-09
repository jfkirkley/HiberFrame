package org.maxml.propertymappers;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

import org.maxml.util.ClassUtils;
import org.maxml.reflect.CachedClass;



public class CollectionPropertyMapper extends PropertyMapper {

    public CollectionPropertyMapper() {
		super();
    }


    public Object map( HashMap sourceMap, HashMap targetMap ) {

 		Object source = sourceMap.get( getSource() );
 		String sourceProperty = getSourceProperty();

 		CachedClass sourceCachedClass = reflectCache.getClassCache( source.getClass() );

		return map( sourceCachedClass.invokeGetMethod( source, sourceProperty ) );
		
	}

	public Object map(Object sourceCollection) {

		Collection targetCollection = 
			(Collection)ClassUtils.i().createNewObjectOfType(sourceCollection);

		Iterator iter = ((Collection)sourceCollection).iterator();
		while(iter.hasNext()) {
			targetCollection.add( iter.next() );
		}
		return targetCollection;
    }
}
