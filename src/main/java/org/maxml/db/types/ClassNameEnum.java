package org.maxml.db.types;

import java.lang.Integer;
import java.lang.String;

public class ClassNameEnum {

    private Integer id;

    private String className;
    
    
    public ClassNameEnum() {}
    public ClassNameEnum(String className) {
        this.className = className;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer value ) {
        this.id = value;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String value ) {
        this.className = value;
    }

}
