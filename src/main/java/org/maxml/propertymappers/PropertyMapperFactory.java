package org.maxml.propertymappers;

import java.util.*;
import java.lang.Class;
import java.util.HashMap;
import java.util.Properties;


import org.maxml.config.RootConfig;
import org.maxml.reflect.ReflectCache;
import org.maxml.util.XMLConfig;

public class PropertyMapperFactory {

    private Properties   mapperClassProperties;
    private HashMap      propertyMapperCache;

    private XMLConfig    propertyMapperConfig;

    public static PropertyMapperFactory instance = null;

    public static String propertyMapperConfigXPathFileName = "xpath.propertymapperconfig.filename";
    public static String propertyMapperConfigXMLFileName = "xml.propertymapperconfig.filename";

    public PropertyMapperFactory(ReflectCache reflectCache) {

		this.propertyMapperCache = new HashMap();

		propertyMapperConfig = new XMLConfig
			(RootConfig.i().getPath(propertyMapperConfigXPathFileName),
			 RootConfig.i().getPath(propertyMapperConfigXMLFileName));

		HashMap hm = propertyMapperConfig.buildNestedObjects("mapper[attribute::type=\"nested\"]", "@property" );

		Iterator iter = hm.keySet().iterator();
		while(iter.hasNext()) {
			String name = (String)iter.next();
			Object val  = hm.get( name );

			if( val instanceof PropertyMapper ) {
				propertyMapperCache.put( name, val );
			} else if( val instanceof HashMap ) {
				propertyMapperCache.put( name, new PropertyMapper( (HashMap) val ) );
			}
		}
    }


    public static PropertyMapperFactory getInstance(ReflectCache reflectCache) {
		if( instance == null ) {
			instance = new PropertyMapperFactory(reflectCache);
		}
		return instance;
    }

    public static PropertyMapperFactory getInstance() {
		return instance;
    }

    public PropertyMapper getPropertyMapper( Object key ) { //, Class contextClass ) {
		PropertyMapper propertyMapper = (PropertyMapper)propertyMapperCache.get(key);

// 		if( propertyMapper == null ) {
// 			propertyMapper = createPropertyMapper( propertyMapperToken );
// 			propertyMapperCache.put( propertyMapperToken, propertyMapper );
// 		}
		return propertyMapper;
    }


    public PropertyMapper createPropertyMapper( String propertyMapperToken ) {
		try {
			String className = (String)mapperClassProperties.get(propertyMapperToken);

			if( className == null ) 
				className = (String)mapperClassProperties.get("default");

			// 		throw new 
			// 		    ClassNotFoundException
			// 		    ("property mapper for token: " + propertyMapperToken + " not found" );

			Class claz = Class.forName(className);
			PropertyMapper propertyMapper = (PropertyMapper) claz.newInstance();
			//propertyMapper.init( reflectCache, type2typeMap );
			return propertyMapper;

		} catch( ClassNotFoundException cnfe ) {
			cnfe.printStackTrace();
		} catch( IllegalAccessException iae ) {
			iae.printStackTrace();
		} catch( InstantiationException ie ) {
			ie.printStackTrace();
		}
		return null;
    }


}
