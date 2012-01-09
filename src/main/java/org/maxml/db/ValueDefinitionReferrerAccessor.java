package org.maxml.db;

public interface ValueDefinitionReferrerAccessor {
    
    public Object getDefinition(Object valueInfo) throws DBException;
    public Object getValue(Object definition, Object valueInfo) throws DBException;

    public Object getNewDefinition(Object valueInfo);
    public Object getNewValue(Object definition, Object valueInfo);
    public Object getNewReferrer(Object value, Object referrerInfo);

    public boolean referrerExists(Object value, Object referrerInfo);
    
    public String getValuePropertyName();
    public String getReferrerIdPropertyName();
}
