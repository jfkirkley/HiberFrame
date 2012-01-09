package org.maxml.reflect;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.maxml.util.ClassUtils;
import org.maxml.util.Regex;
import org.maxml.util.Util;

public class MapReflectionProvider implements ReflectionProvider {
    
    private Map map;
    private boolean doCreate;
    
    public static final String WILD_CARD="*";
    public static final String SORT_ASC="^";
    public static final String SORT_DESC="v";
    
    public MapReflectionProvider(Map Map, boolean doCreate) {
        this.map = Map;
        this.doCreate = doCreate;
    }
    
    public Object get(String property) {
    	
    	// TODO this is not the right place for *^, need filter class or some such
    	
    	if(Regex.isRegexDelimitted2(property)) {
    		Regex regex = new Regex(Regex.getRegexp2(property));
            Collection collection = Util.i().getValuesMatchingRegex(map, regex);
            return collection;
    	}
        if( property.indexOf(WILD_CARD) != -1 ) {
        	String sorttype=null;

            if(property.endsWith(SORT_ASC)) {
                property = property.substring(0,property.length()-1);
                sorttype = SORT_ASC;
            } else if(property.endsWith(SORT_DESC)) {
                property = property.substring(0,property.length()-1);
                sorttype = SORT_DESC;
            }
            
            Collection collection = null;            
            if(property.startsWith(WILD_CARD)) {
                property = property.substring(1);
                if(property.endsWith(WILD_CARD)) {
                    property = property.substring(0,property.length()-1);
                    collection = Util.i().getMatchingKeys(map, property);
                } else {
                    collection = Util.i().getKeysMatchingSuffix(map, property);
                }
            }

            if(property.endsWith(WILD_CARD)) {
                property = property.substring(0,property.length()-1);
                collection = Util.i().getKeysMatchingPrefix(map, property);
            }
            
            if( sorttype != null ) {
                if(sorttype.equals(SORT_ASC)) {
                	collection = Util.i().sortCollection(collection);
                } else {
                	collection = Util.i().sortCollectionDescending(collection);
                }
            }
            
            return Util.i().getItemsForKeys(map,collection);
            
        } 
        else if( doCreate ) {
            NameAndType nameAndType = new NameAndType((String)property);
            if( !map.containsKey(nameAndType.getName()) && nameAndType.getType() != null) {
                map.put(nameAndType.getName(), ClassUtils.i().createNewObjectOfType(nameAndType.getType()));
                property = nameAndType.getName();
            }
        }

        return map.get(property); 
    }
    public void set(String property, Object value) {
        this.map.put(property, value);
    }
    public void remove(String id) {
        this.map.remove(id);
    }

    public void merge(Object thisObject, boolean overwrite) {
        Map otherMap = (Map) thisObject;
        if (overwrite) {
            map.clear();
            map.putAll(otherMap);
        } else {
            for (Iterator iter = otherMap.keySet().iterator(); iter.hasNext();) {
                Object key = iter.next();
                if (!map.containsKey(key)) {
                    Object val = otherMap.get(key);
                    map.put(key,val);
                } else {
                    Object val = map.get(key);
                    Object otherVal = otherMap.get(key);
                    ReflectionProviderFactory.i().merge(otherVal,val,overwrite);
                }
            }
        }
    }

}

