package org.maxml.util;

import java.util.*;


public class NestedHashMap{

    HashMap rootMap;

    public NestedHashMap() {
		rootMap = new HashMap();
    }

    public HashMap getRootMap() { return rootMap; }
    public HashMap getNestedMap( Object key ) { return (HashMap) rootMap.get(key); }
    public HashMap removeNestedMap( Object key ) { return (HashMap) rootMap.remove(key); }

    public Object set( Object mapkey, Object valuekey, Object value ) { 
		HashMap nestedMap = getNestedMap( mapkey );
		if( nestedMap == null ) {
			nestedMap = new HashMap();
			rootMap.put( mapkey, nestedMap );
		}
		return nestedMap.put( valuekey, value );
    }

    public Object get( Object mapkey, Object valuekey ) { 
        HashMap nestedMap = getNestedMap( mapkey );
        if( nestedMap == null ) {
            return null;
        }
        return nestedMap.get( valuekey );
    }
    public Object remove( Object mapkey, Object valuekey ) { 
        HashMap nestedMap = getNestedMap( mapkey );
        if( nestedMap == null ) {
            return null;
        }
        return nestedMap.remove( valuekey );
    }
}
