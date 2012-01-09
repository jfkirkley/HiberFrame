package org.maxml.db.types;


public interface ValueObject {
    
    public void setDefinition( Definition definition );
    public Definition getDefinition();
    
    public Object getValue();
    public void setValue( Object value );

}
