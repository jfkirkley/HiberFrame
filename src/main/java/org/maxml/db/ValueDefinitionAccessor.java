package org.maxml.db;

public interface ValueDefinitionAccessor {
    public Object getDefinition(Object valueInfo) throws DBException;
    public Object getValue(Object definition, Object valueInfo) throws DBException;

    public Object getNewDefinition(Object valueInfo);
    public Object getNewValue(Object definition, Object valueInfo);
    public Object getNewValue(Object definition);

    public String getValuePropertyName();
}
