package org.maxml.propertymappers;

import java.lang.Class;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.lang.reflect.Method;

import org.maxml.util.ClassUtils;
import org.maxml.reflect.CachedClass;




public class TypedCollectionPropertyMapper extends PropertyMapper {

    private String sourceClassName;
    private String targetClassName;

	public static String SOURCEPARAMNAME="sourceCollection";
	public static String TARGETPARAMNAME="targetCollection";

    public void setSource(String source) { sourceClassName = source; }
    public void setTarget(String target) { targetClassName = target; }

    public TypedCollectionPropertyMapper() {
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
			Object targetObj = ClassUtils.i().createNewObjectOfType( targetClassName );
			
			targetCollection.add( map( iter.next(), targetObj ));
		}
		return targetCollection;
    }


// 	public StringBuffer getCallCode( String propertyName, 
// 									 Method sourceMethod, 
// 									 Method targetMethod, 
// 									 String indent ) {

// 		StringBuffer codeBuf = org.maxml.java_langnew StringBuffer();
// 		String sourceType = sourceMethod.getReturnType().getName();
// 		String targetType = targetMethod.getParameterTypes()[0].getName();

// 		codeBuf.append( " map" + propertyName + "( source );" );
// 	}



	public Object writeSourceCode( String propertyName, Method sourceMethod, Method targetMethod, String indent, HashMap methodMap ) {

		StringBuffer codeBuf = new StringBuffer();
		String sourceType = sourceMethod.getReturnType().getName();
		String targetType = targetMethod.getParameterTypes()[0].getName();

		String methodSig = indent + 
			"public " + 
			targetType + 
			" map" + 
			propertyName +
			"(" + 
			sourceType + " " + SOURCEPARAMNAME + " ) {\n";

 		codeBuf.append( methodSig );
		codeBuf.append( indent + "    " + targetType + " " + 
						TARGETPARAMNAME + " = " + " new " + targetType + "();\n" );

		codeBuf.append( indent + "    Iterator iter = " + SOURCEPARAMNAME + ".iterator();\n" );
		codeBuf.append( indent + "    while( iter.hasNext() ) {\n" );
		codeBuf.append( indent + "        " + targetClassName + " target = new " + targetClassName + "();\n" );
		codeBuf.append( indent + "        " + sourceClassName + " source = (" + sourceClassName + ") iter.next();\n" );
		codeBuf.append( indent + "        " + TARGETPARAMNAME + ".add( map( source, target ) );\n" );
		codeBuf.append( indent + "    }\n" );
		codeBuf.append( indent + "    return " + TARGETPARAMNAME + ";\n");
		codeBuf.append( indent + "}\n" );

		methodMap.put( methodSig, codeBuf );

		try {
			writeSourceCode( Class.forName( sourceClassName ), 
							 Class.forName( targetClassName ), indent, methodMap );

		} catch( ClassNotFoundException cnfe ) {
			cnfe.printStackTrace();
		} 

		return methodMap;
	}
	
}
