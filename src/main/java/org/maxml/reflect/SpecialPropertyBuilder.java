package org.maxml.reflect;

public interface SpecialPropertyBuilder {
	
    public Object readProperty(Object target, Object otherObj);
    public Object updateProperty(Object target, Object otherObj);

}
