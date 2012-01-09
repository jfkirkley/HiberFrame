package org.maxml.db.types;

public class StringValue {
    
    private Integer id;
    private String str;
    
    public StringValue() {
    }
    public StringValue(String str) {
        this.str=str;
    }
    public StringValue(String str, int id) {
    	this(str);
    	this.id=id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
    
    public String toString() { return str; }
    
    public boolean equals(Object otherObj) {
        if(otherObj == this) return true;
        if(otherObj == null) return false;
        
        if (otherObj instanceof StringValue) {
            StringValue otherSV = (StringValue) otherObj;
            if(id!=null && id.equals(otherSV.getId())) return true;
            if(otherSV.getId() == null && str!=null && str.equals(otherSV.getStr())) return true;
        }
        
        return false;
    }

}
