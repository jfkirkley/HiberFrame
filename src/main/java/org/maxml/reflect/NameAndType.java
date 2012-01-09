package org.maxml.reflect;

import org.maxml.util.ClassUtils;

public class NameAndType {
    public static final String TYPE_TOKEN = "$";
    private String name;
    private String type;
    public NameAndType(String propertyName) {
        if(propertyName.indexOf(TYPE_TOKEN)!=-1) {
            String[] arr = propertyName.split(("\\$"));
            name = arr[0];
            type = arr[1].replace('_','.');
        } else {
            name = propertyName;
        }
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }

    public Object makeObject() {
        return ClassUtils.i().createNewObjectOfType(type, name);
    }
}

