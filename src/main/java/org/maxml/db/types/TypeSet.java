package org.maxml.db.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.maxml.db.DBObjectAccessorFactory;

public class TypeSet extends TreeSet {
    private ArrayOf2ByteInts arrayOf2ByteInts;

    public TypeSet() {
        this(new ArrayOf2ByteInts());
    }

    public TypeSet(byte[] rep) {
        this(new ArrayOf2ByteInts(rep));
    }

    public TypeSet(ArrayOf2ByteInts arrayOf2ByteInts) {
        super();
        this.arrayOf2ByteInts = arrayOf2ByteInts;
        int[] ints = arrayOf2ByteInts.getAsIntArray();
        int sz = arrayOf2ByteInts.getNumInts();
        for (int i = 0; i < sz; i++) {
            super.add(ints[i]);
            // System.out.println("adding: " + ints[i]);
        }
    }

    public boolean add(Object o) {

        if (o != null) {
            Integer typeNum = null;
            if (o instanceof Integer) {
                typeNum = (Integer) o;
            } else if (o instanceof Class) {
                Class clz = (Class) o;
                typeNum = DBObjectAccessorFactory.i().getTypeIdForClass(clz.getName());
            } else if (o instanceof String) {
                String className = (String) o;
                typeNum = DBObjectAccessorFactory.i().getTypeIdForClass(className);
            }

            if (typeNum != null && !contains(typeNum)) {
                //System.out.println("adding: " + typeNum);
                arrayOf2ByteInts.addInt(typeNum);
                return super.add(typeNum);
            }
        }
        return false;
    }
    
    public boolean addAll(Collection collection) {
        boolean result = false;
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            result = add(iter.next()) || result;
        }
        return result;
    }
    
    public boolean remove(Object o) {
        if (o != null) {
            Integer typeNum = null;
            if (o instanceof Integer) {
                typeNum = (Integer) o;
            } else if (o instanceof Class) {
                Class clz = (Class) o;
                typeNum = DBObjectAccessorFactory.i().getTypeIdForClass(clz.getName());
            } else if (o instanceof String) {
                String className = (String) o;
                typeNum = DBObjectAccessorFactory.i().getTypeIdForClass(className);
            }

            if (typeNum != null && !contains(typeNum)) {
                //System.out.println("removing: " + typeNum);
                arrayOf2ByteInts.removeInt(typeNum);
                return super.remove(typeNum);
            }
        }
        return false;
    }

    public boolean removeAll(Collection collection) {
        boolean result = false;
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            result = remove(iter.next()) || result;
        }
        return result;
    }

    public boolean retainAll(Collection collection) {
        
        boolean result = false;
        ArrayList elementsToRemove = new ArrayList();
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            Object o = iter.next();
            if( !contains(o) ) {
                elementsToRemove.add(o);
            }
        }
        for (Iterator iter = elementsToRemove.iterator(); iter.hasNext();) {
            result = remove(iter.next()) || result;
        }
        return result;
    }
        
    public byte[] getByteArray() {
        return this.arrayOf2ByteInts.getIntArray();
    }

    public void printItOut() {
        this.arrayOf2ByteInts.printOutInts();
    }

    public ArrayOf2ByteInts getArrayOf2ByteInts() {
        return arrayOf2ByteInts;
    }

    public void setArrayOf2ByteInts(ArrayOf2ByteInts arrayOf2ByteInts) {
        this.arrayOf2ByteInts = arrayOf2ByteInts;
    }
}
