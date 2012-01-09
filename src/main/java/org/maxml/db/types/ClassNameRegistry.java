package org.maxml.db.types;

import java.util.Collection;

import org.maxml.db.DBException;

public interface ClassNameRegistry {
    
    public void init(Collection<String> classNameList) throws DBException;
    public Integer getTypeIdForClassName(String className);
    public String getClassNameForTypeId(Integer typeId);
    public Class getClassForTypeId(Integer typeId);

}
