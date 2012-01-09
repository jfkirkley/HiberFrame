package org.maxml.util;

import org.maxml.xpath.ApplyXPath;

import org.w3c.dom.*;
import java.util.*;




public class XMLConfigObjectBuilder {

    HashMap paramXpaths;
    PropertyMapperFromMap propertyMapperFromMap;

    public XMLConfigObjectBuilder( HashMap paramXpaths ) {
		this.paramXpaths = paramXpaths;
		propertyMapperFromMap = new PropertyMapperFromMap();
    }

    public Object createObject( Element contextElem, String typeName ) {
	
		HashMap paramMap = new HashMap ();

		if( paramXpaths != null && paramXpaths.size() > 0 ) {

			// get params
			Iterator iter = paramXpaths.keySet().iterator();
			while(iter.hasNext() ) {
				String propertyName = (String)iter.next();
				String xpath = (String) paramXpaths.get( propertyName );

				String value =  ApplyXPath.i().getStringValFromXPath( contextElem, xpath);
	    
				paramMap.put( propertyName, value );
			}
		}

		// create object of type
		// populate params
		return propertyMapperFromMap.createAndMap( typeName, paramMap );
    }
}
