package org.maxml.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CollectionMap <K,V> implements Map {
	
	private Map<K, Collection<?>> map;
	private Class<?> collectionType;
	
	public CollectionMap() {
		this(ArrayList.class);
		this.map = new HashMap();
	}

	public CollectionMap(Class<?> collectionType) {
		this.collectionType = collectionType;
	}

	public void clear() {
		this.map.clear();
	}

	public boolean containsKey(Object arg0) {
		return this.map.containsKey(arg0);

	}

	public boolean containsValue(Object arg0) {
		for ( Collection<?> c: map.values() ) {
			if( c.contains(arg0)) {
				return true;
			}
		}
		return map.containsValue(arg0);
	}

	public Set<Map.Entry<K,Collection<?>>> entrySet() {
		return map.entrySet();
	}

	public Collection<?> get(Object arg0) {
		return map.get(arg0);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Object putSubValue(K key, Object value) {
		Collection collection = map.get(key);
		if(collection == null) {
			collection = (Collection)ClassUtils.i().createNewObjectOfType(collectionType);
			map.put(key, collection);
		}
		collection.add(value);
		return collection;
	}
	
	public Object put(Object k, Object v) {
		return putSubValue((K)k,v);
	}
 	

    public void putAll(Map arg0) {
        for ( Object key: arg0.keySet() ) {
            putSubValue((K)key, map.get(key));
        }
    }
    
    public Object getContainingRootKey(Object subValue) {
        for ( Object rootKey: map.keySet() ) {
            if(map.get(rootKey).contains(subValue)) {
                return rootKey;
            }
        }
        return null;
    }
    
    public void putAllCollections(CollectionMap arg0) {
        for ( Object key: arg0.keySet() ) {
            putCollection(key, (Collection)arg0.get(key));
        }
    }

	public void putCollection(Object key, Collection collection) {
		if(containsKey(key)) {
			map.get(key).addAll(collection);
		} else {
			this.map.put((K)key, collection);
		}
	}

    public Collection<?> remove(Object arg0) {
		return this.map.remove(arg0);
	}

	public int size() {
		return map.size();
	}

	public Collection values() {
		Collection collection = (Collection)ClassUtils.i().createNewObjectOfType(collectionType);
		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
			Collection c = (Collection) map.get(iter.next());
			collection.addAll(c);
		}
		return collection; 
	}

}
