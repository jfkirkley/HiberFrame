package org.maxml.db.types;


public interface Definition {
        
    public String getDescription();
    public void setDescription(String description);
    public String getCode();
    public void setCode(String code);
    public String getName();
    public void setName(String name);
    public ValueObject getDefault();
    public void setDefault(ValueObject valueObject);

}
