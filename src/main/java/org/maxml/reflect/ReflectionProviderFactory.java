package org.maxml.reflect;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.maxml.util.Regex;
import org.maxml.util.Util;

public class ReflectionProviderFactory {
    // x(n).y[3].z.p = a.b[2].c(m).d.e
    // x(n).y[3].z.p 
    // x.n.y.3.z.p 
    // x\n\y\3\z\p 
    // m\o\l\o\o
	
	public static final char escapeChar = '\\';
    
    private static ReflectionProviderFactory reflectionProviderFactory=null;
    public static ReflectionProviderFactory i(){
        if( reflectionProviderFactory == null) {
            reflectionProviderFactory = new ReflectionProviderFactory();
        }
        return reflectionProviderFactory;
    }
    public ReflectionProvider getReflectionProvider(Object provider) {
        return getReflectionProvider(provider, false);
    }
    public ReflectionProvider getReflectionProvider(Object provider, boolean doCreate) {
        ReflectionProvider reflectionProvider = null;
        if (provider instanceof List) {
            reflectionProvider = new ListReflectionProvider((List) provider, doCreate);
        }
        else if (provider instanceof Collection) {
            reflectionProvider = new CollectionReflectionProvider((Collection) provider, doCreate);
        }
        else if (provider instanceof Map) {
            reflectionProvider = new MapReflectionProvider((Map) provider, doCreate);
        }
        else if (provider.getClass().isArray()) {
            reflectionProvider = new ArrayReflectionProvider((Object[])provider, doCreate);
        } 
        else {
            reflectionProvider = new ClassReflectionProvider(provider,doCreate);
        }
        return reflectionProvider;
    }

    public Object getNestedProperty(String spec, Object rootObj) {
        return getNestedProperty(spec, rootObj, false, null);
    }

    public Object getNestedProperty(String spec, Object rootObj, AccessInfo accessInfo) {
        return getNestedProperty(spec, rootObj, false, accessInfo);
    }
    
    public Object getNestedProperty(String spec, Object rootObj, boolean doCreate) {
        return getNestedProperty(spec, rootObj, doCreate, null);
    }

    public Object getNestedProperty(String[] nestedProps, Object rootObj, boolean doCreate) {
        return getNestedProperty(nestedProps, rootObj, doCreate, null);
    }
    
    public Object getNestedProperty(String spec, Object rootObj, boolean doCreate, AccessInfo accessInfo) {
        
        String[] nestedProps = normalizeSpec(spec);
        return getNestedProperty(nestedProps, rootObj, doCreate, accessInfo);
    }
    

    public Object getNestedProperty(String[] nestedProps, Object rootObj, boolean doCreate, AccessInfo accessInfo) {
        Object currObject = rootObj;

        for (int i = 0; i < nestedProps.length; i++) {
            ReflectionProvider reflectionProvider = getReflectionProvider(currObject, doCreate);
            String next = nestedProps[i];
            String func = null;
            if( next.indexOf(':') != -1) {
                String [] parts = next.split(":");
                next = parts[0];
                func = parts[1];
            }
            currObject = reflectionProvider.get(next);
            
            if( currObject == null) return null;
            if( func != null) {
                currObject = CachedClass.callFunc(currObject, func);
            }
            if( accessInfo != null) {
                accessInfo.pushAccessObj(currObject, next);
            }
        }
        if( accessInfo != null) {
            accessInfo.setTarget(currObject);
            return accessInfo;
        }
        return currObject;
    }
    
    public void setNestedProperty(String spec, Object rootObj, boolean doCreate, Object value) {
        String[] nestedProps = normalizeSpec(spec);
        setNestedProperty(nestedProps, rootObj, doCreate, value);
    }
    
    public void setNestedProperty(String[] nestedProps, Object rootObj, boolean doCreate, Object value) {

        String finalProp = nestedProps[nestedProps.length-1];
        String [] nestedPropsForGet = new String[nestedProps.length-1];
        for (int i = 0; i < nestedProps.length-1; i++) { 
            nestedPropsForGet[i] = nestedProps[i];
        }
        Object target = getNestedProperty(nestedPropsForGet, rootObj, doCreate);
        if( target != null) {
            ReflectionProvider reflectionProvider = getReflectionProvider(target);
            reflectionProvider.set(finalProp, value);
        }
    }
    
    public void removeNestedElement(String spec, Object rootObj, boolean doCreate) {
        String[] nestedProps = normalizeSpec(spec);
        removeNestedElement(nestedProps, rootObj, doCreate);
    }
    
    public void removeNestedElement(String[] nestedProps, Object rootObj, boolean doCreate) {

        String finalProp = nestedProps[nestedProps.length-1];
        String [] nestedPropsForGet = new String[nestedProps.length-1];
        for (int i = 0; i < nestedProps.length-1; i++) { 
            nestedPropsForGet[i] = nestedProps[i];
        }
        Object target = getNestedProperty(nestedPropsForGet, rootObj, doCreate);
        ReflectionProvider reflectionProvider = getReflectionProvider(target);
        reflectionProvider.remove(finalProp); 
    }

    public void merge(Object thisObject, Object intoThis, boolean overwrite) {
        ReflectionProvider reflectionProvider = getReflectionProvider(intoThis);
        reflectionProvider.merge(thisObject, overwrite);
        
    }    
    
    protected String[] normalizeSpec(String spec) {
        String normalizedSpec = "";
        String dot = "";
        String[] parts = parseNestedPropSpec(spec);
    	ArrayList partsList = new ArrayList();
    	
        for (int i = 0; i < parts.length; i++) {
            PropertyName propertyName = new PropertyName(parts[i]);
            String name = propertyName.getPropertyName();
            if( propertyName.isTyped()) {
                name += NameAndType.TYPE_TOKEN + propertyName.getType().replace('.','_');
            }
            if( propertyName.isIndexed()) {
            	partsList.add(name);
            	partsList.add(propertyName.getIndexStr());
            	continue;
                //name += "." + propertyName.getIndexStr();
            } else if( propertyName.isParameterized()) {
            	partsList.add(name);
            	partsList.add(propertyName.getParameter());
            	continue;
                //name += "." + propertyName.getParameter();
            }
        	partsList.add(name);
//            normalizedSpec += dot + name;
//            dot = ".";
        }
    	return Util.i().toStringArray(partsList);
    }
    
    public String[] parseNestedPropSpec(String spec) {

    	ArrayList partsList = new ArrayList();
    	StringBuffer currTerm= new StringBuffer(256);
    	boolean inRegex = false;
		char nc = ' ';
    	for (int i = 0; i < spec.length()-1; i++) {

    		char c = spec.charAt(i);
    		nc = spec.charAt(i+1);

			if (c == escapeChar) {
				if(inRegex)
					currTerm.append(c);
				currTerm.append(nc);
				++i;
				continue;
			}
			
    		if( c == Regex.regexDelimChar2) {
				inRegex = !inRegex;
    		}
    		
    		if(inRegex) {
				currTerm.append(c);
    		} else if( c == '.') {
    			partsList.add(currTerm.toString());
    			currTerm.setLength(0);
    		} else {
				currTerm.append(c);
    		}
		}
    	currTerm.append(nc);
		partsList.add(currTerm.toString());
    	
    	return Util.i().toStringArray(partsList);
    }
    
    
}


