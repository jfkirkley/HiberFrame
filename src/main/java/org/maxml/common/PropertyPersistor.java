package org.maxml.common;

public interface PropertyPersistor <T>{
	
	public T read();
	public T write(T t);
	

}
