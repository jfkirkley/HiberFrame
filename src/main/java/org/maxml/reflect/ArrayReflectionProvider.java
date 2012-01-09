package org.maxml.reflect;


import org.maxml.util.ClassUtils;

public class ArrayReflectionProvider implements ReflectionProvider {
    
    private Object [] array;
    private boolean doCreate;
    public ArrayReflectionProvider(Object [] array, boolean doCreate) {
        this.array = array;
        this.doCreate = doCreate;
    }
    public Object get(String property) {
        Integer index = 0;
        if( doCreate ) {
            NameAndType nameAndType = new NameAndType((String)property);
            index = Integer.parseInt(nameAndType.getName());
            if( index < array.length && array[index] == null && nameAndType.getType()!=null) {
                array[index] = ClassUtils.i().createNewObjectOfType(nameAndType.getType());
            }
        } else {
            index = Integer.parseInt(property);
        }
        if( index < array.length ) {
            return array[index];
        }
        return null;
    }
    public void set(String property, Object value) {
        Integer index = Integer.parseInt(property);
        if( index < array.length ) {
            array[index] =  value;
        }
    }
    public void remove(String id) {
        Integer index = Integer.parseInt(id);
        if( index < array.length ) {
            array[index] =  null;
        }
    }
    public void merge(Object thisObject, boolean overwrite) {
        Object [] otherArray = (Object [])thisObject;
        for (int i = 0; i < array.length && i < otherArray.length; i++) {
            if(array[i] == null || overwrite) {
                array[i]=otherArray[i];
            }
        }
    }
}



