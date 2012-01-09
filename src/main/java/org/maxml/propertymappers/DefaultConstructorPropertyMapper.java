package org.maxml.propertymappers;

import java.util.HashMap;
import java.lang.reflect.Method;

import org.maxml.util.ClassUtils;




public class DefaultConstructorPropertyMapper extends PropertyMapper {

    private String objectType;


    public String getObjectType() { return objectType; }
    public void   setObjectType( String v ) {  objectType = v; }

    public DefaultConstructorPropertyMapper() {
		super();
    }

	public Object map(Object source) {
		return ClassUtils.i().createNewObjectOfType( objectType );
    }

	public boolean writeInLine() { return true; }

    public Object map( HashMap sourceMap, HashMap targetMap ) {

		return ClassUtils.i().createNewObjectOfType( objectType );

// 		Object source = sourceMap.get( getSource() );
// 		Object target = targetMap.get( getTarget() );
// 		String sourceProperty = getSourceProperty();

// 		CachedClass sourceCachedClass = reflectCache.getClassCache( source.getClass().getName() );
// 		CachedClass targetCachedClass = reflectCache.getClassCache( target.getClass().getName() );
	}

 	public Object writeSourceCode( String propertyName, Method sourceMethod, Method targetMethod, String indent, HashMap methodMap ) {

 		String targetMethodName = targetMethod.getName();

 		StringBuffer codeBuf = new StringBuffer();

 		codeBuf.append( indent + "    " + 
 						TARGETPARAMNAME + "." + targetMethodName + "( new " + objectType + "() );\n" );


 		return codeBuf;
 	}
	
}
