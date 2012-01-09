package org.maxml.propertymappers;

import java.util.HashMap;
import java.lang.reflect.Method;



public class StaticValuePropertyMapper extends PropertyMapper {

    private Object value;
    private String valueType;


    public Object getValue() { return value; }
    public void setValue( Object v ) {  value = v; }
    public String getValueType() { return valueType; }
    public void setValueType( String v ) {  valueType = v; }

    public StaticValuePropertyMapper() {
		super();
    }

	public Object map(Object source) {
		return value;
    }


	public boolean writeInLine() { return true; }

	public String getValueRep() { 
		if( valueType.equals( "java.lang.String" ) ) {
			return "\"" + value + "\"";
		} 
		return value.toString();
	}
		
    public Object map( HashMap sourceMap, HashMap targetMap ) {
		return value;
	}

	public Object writeSourceCode( String propertyName, Method sourceMethod, Method targetMethod, String indent, HashMap methodMap ) {

		String targetMethodName = targetMethod.getName();

		StringBuffer codeBuf = new StringBuffer();

		codeBuf.append( indent + "    " + 
						TARGETPARAMNAME + "." + targetMethodName + "( " + getValueRep() + " );\n" );


		return codeBuf;
	}
	
}
