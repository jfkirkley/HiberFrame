package org.maxml.db.types;

public class StringAndInt {
    
    private Integer id;
    private String  str;
    private Integer num;
    
    public StringAndInt() {
    
    }
    
    public StringAndInt(String str, Integer num) {
        this.str = str;
        this.num = num;
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
    
    public Integer getNum() {
        return num;
    }
    public void setNum(Integer num) {
        this.num = num;
    }

}
