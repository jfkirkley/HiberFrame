package org.maxml.propertymappers;

// import org.hibernate.*;
// import org.hibernate.criterion.Expression;
import java.util.*;
// import util.HibernateUtil;

import org.maxml.reflect.CachedClass;
import org.maxml.reflect.ReflectCache;
import org.maxml.util.ClassUtils;
import java.lang.reflect.Method;


public class PropertyMapper {

    protected ReflectCache reflectCache;
	protected HashMap nestedMappers;

	public static String MAPFUNCNAME="map";
	public static String MAPFUNCNAME_RETURNTYPE="Object";
	public static String SOURCEPARAMNAME="source";
	public static String TARGETPARAMNAME="target";

	public static String DEFAULT_NAME="default";

	private String source = DEFAULT_NAME;
	public String target = DEFAULT_NAME;
	public String sourceProperty = null;

	public String getSource(){ return source; }
	public void setSource(String source){ this.source = source; }
	public String getSourceProperty(){ return sourceProperty; }
	public void setSourceProperty(String sourceProperty){ this.sourceProperty = sourceProperty; }
	public String getTarget(){ return target; }
	public void setTarget(String target){ this.target = target; }

    public PropertyMapper() {
		reflectCache = ReflectCache.i();
    }

    public PropertyMapper( HashMap mappers ) {
		this();
		this.nestedMappers = new HashMap();

		Iterator iter = mappers.keySet().iterator();
		while(iter.hasNext() ) {
			String name = (String)iter.next();
			Object val   = mappers.get( name );

			if( val instanceof PropertyMapper ) {
				nestedMappers.put( name, val );
			} else if( val instanceof HashMap ) {
				nestedMappers.put( name, new PropertyMapper( (HashMap) val ) );
			}
		}
    }

	public Object getKeyObject() { return "default"; }

    public Object map( Object source ) {
        return map( source, source.getClass() );
    }

    public Object map( Object source, Class targetClass ) {
        Object target = ClassUtils.i().createNewObjectOfType( targetClass );
        return map( source, target );
    }

    public Object map( Object source, Object target ) {

		CachedClass sourceCachedClass = reflectCache.getClassCache( source.getClass() );
		CachedClass targetCachedClass = reflectCache.getClassCache( target.getClass() );

		TreeMap getMethods = targetCachedClass.getMethods();

		Iterator iter = getMethods.keySet().iterator();
		while(iter.hasNext() ) {
			String mName = (String)iter.next();

			Object sourceObj = sourceCachedClass.invokeGetMethod( source, mName );

			//if( sourceObj == null ) continue;

			Object newObject = null;
			PropertyMapper pm = 
				(nestedMappers!=null)?(PropertyMapper)nestedMappers.get( mName ):null;

			if( pm != null ) {
				newObject = pm.map( sourceObj );
			} else {
				newObject = sourceObj;
			}
			if( newObject != null ) {
				targetCachedClass.invokeSetMethod( target, mName, newObject );
			}

// 			if( sourceObj instanceof Collection ) {
// 				mapObject( target, (Collection)sourceObj, mName, targetCachedClass );
// 			} else {
// 				mapObject( target, sourceObj, mName, targetCachedClass );
// 			}
		}
		return target;
    }

    public Object map( HashMap sourceMap, HashMap targetMap ) {

		Object source = sourceMap.get( getSource() );
		Object target = targetMap.get( getTarget() );

		CachedClass sourceCachedClass = reflectCache.getClassCache( source.getClass() );
		CachedClass targetCachedClass = reflectCache.getClassCache( target.getClass() );
		
		String sourceProperty = getSourceProperty();
		if( sourceProperty != null ) {
			return sourceCachedClass.invokeGetMethod( source, sourceProperty );
		} 

		TreeMap  getMethods = targetCachedClass.getMethods();
		Iterator iter       = getMethods.keySet().iterator();

		while(iter.hasNext() ) {
			String mName = (String)iter.next();

			Object newObject = null;
			PropertyMapper pm = 
				(nestedMappers!=null)?(PropertyMapper)nestedMappers.get( mName ):null;

			if( pm != null ) {
				newObject = pm.map( sourceMap, targetMap );
			} else {
				newObject = sourceCachedClass.invokeGetMethod( source, mName );
			}

			if( newObject != null ) {
				targetCachedClass.invokeSetMethod( target, mName, newObject );
			}

		}
		return target;
    }

    public Object writeSourceCode( Class source, Class target, String indent, HashMap methodMap ) {
	
		StringBuffer codeBuf = new StringBuffer();

		CachedClass sourceCachedClass = reflectCache.getClassCache( source );
		CachedClass targetCachedClass = reflectCache.getClassCache( target );

		TreeMap getMethods = targetCachedClass.getMethods();

		String methodSig = ( indent + "public " + 
							MAPFUNCNAME_RETURNTYPE + " " + 
							MAPFUNCNAME + 
							"( " + 
							sourceCachedClass.getShortName() + " " + SOURCEPARAMNAME + ", " + 
							targetCachedClass.getShortName() + " " + TARGETPARAMNAME +
							") {\n" );

		codeBuf.append( methodSig );

		Iterator iter = getMethods.keySet().iterator();
		while(iter.hasNext() ) {
			String mName = (String)iter.next();
	    
			Method sourceMethod = sourceCachedClass.getGetMethod( mName );
			Method targetMethod = targetCachedClass.getSetMethod( mName );

			PropertyMapper pm = 
				(nestedMappers!=null)?(PropertyMapper)nestedMappers.get( mName ):null;

 			if( pm != null && !pm.writeInLine() ) {

				String sourceMethodName = sourceMethod.getName();
				String targetMethodName = targetMethod.getName();
				codeBuf.append( indent + "    " + TARGETPARAMNAME + "." + 
								targetMethodName + "( map" + mName + "( source ) );\n" );
				
				Object result = pm.writeSourceCode
					( mName, sourceMethod, 
					  targetMethod,
					  indent, methodMap );


 			} else if( pm != null && pm.writeInLine() ) {
				
				Object result = pm.writeSourceCode
					( mName, sourceMethod, 
					  targetMethod,
					  indent, methodMap );

				codeBuf.append( result );
				

 			} else {

				String sourceMethodName = sourceMethod.getName();
				String targetMethodName = targetMethod.getName();
				codeBuf.append( indent + "    " + 
								TARGETPARAMNAME + "." + targetMethodName + "( " + 
								SOURCEPARAMNAME + "." + sourceMethodName + "() );\n" );
 			}

		}
		codeBuf.append( indent + "}\n" );

		methodMap.put( methodSig, codeBuf );

		return methodMap;
    }


	public Object writeSourceCode( String propertyName, 
								   Method sourceMethod, 
								   Method targetMethod, 
								   String indent, 
								   HashMap methodMap ) {
		return null;
	}

	public boolean writeInLine() { return false; }

    protected void mapObject( Object target, Object sourceObj, String mName, CachedClass targetCachedClass ) {
		targetCachedClass.invokeSetMethod( target, mName, createMapObject(sourceObj) );
    }

    protected Object createMapObject(Object sourceObj) {

		Object targetObject = null;

		PropertyMapper propertyMapper = 
			PropertyMapperFactory.getInstance().getPropertyMapper( sourceObj.getClass() );

		if( propertyMapper != null && propertyMapper != this) {
			targetObject = propertyMapper.mapObject( sourceObj );
		} else {
			targetObject = sourceObj;
		}
		return targetObject;
    }

    protected Object mapObject( Object sourceObj) {
		Object targetObj = ClassUtils.i().createNewObjectOfType( sourceObj.getClass() );
		map( sourceObj, targetObj);
		return targetObj;
    }

    protected void mapObject( Object target, Collection sourceCollection, String mName, CachedClass targetCachedClass ) {

		Collection targetCollection = (Collection)ClassUtils.i().createNewObjectOfType(sourceCollection);
		Iterator iter = sourceCollection.iterator();
		while(iter.hasNext()) {
			targetCollection.add( createMapObject( iter.next() ));
		}
		targetCachedClass.invokeSetMethod( target, mName, targetCollection );
    }
}
