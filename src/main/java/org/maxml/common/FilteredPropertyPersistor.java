package org.maxml.common;


public interface FilteredPropertyPersistor extends PropertyPersistor {

	public void addFilterPropertyType(Class type);
	public void addFilterPropertyNameRegex(String regex);
}
