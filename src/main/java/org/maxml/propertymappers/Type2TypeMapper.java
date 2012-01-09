package org.maxml.propertymappers;

import java.lang.Class;
import org.maxml.reflect.ReflectCache;
import org.maxml.util.ClassUtils;



public class Type2TypeMapper extends PropertyMapper {

    protected ReflectCache reflectCache;

    private String sourceClassName;
    private String targetClassName;
    private String name;

    public Type2TypeMapper() {
		super();
    }

    public void setSource(String source) { sourceClassName = source; }
    public void setTarget(String target) { targetClassName = target; }
    public void setName(String name) { this.name = name; }

	public Object getKeyObject() { 
		try {
			return Class.forName(sourceClassName);
		} catch( ClassNotFoundException cnfe ) {
			cnfe.printStackTrace();
		} 
		return null;
	}

	public Object mapObject(Object sourceObj) {
		Object targetObj = ClassUtils.i().createNewObjectOfType( targetClassName );
		map( sourceObj, targetObj);
		return targetObj;
	}
}
