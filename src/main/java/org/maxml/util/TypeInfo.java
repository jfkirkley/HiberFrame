package org.maxml.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeInfo {
    String                       javaPackage;
    String                       type;
    boolean                      isPrimitive;
    boolean                      isJavaLang;

    public static final String[] primTypes = { "int", "long", "short",
            "boolean", "char", "byte", "float", "double", "void" };

    public TypeInfo(Class c ) {
        String className = c.getName();
        if( className.lastIndexOf('.') >= 0) {
            javaPackage = className.substring(0,className.lastIndexOf('.') );
            type = className.substring(className.lastIndexOf('.') + 1);
        }
    }
    
    public TypeInfo(String rep) {
        Pattern p = Pattern.compile("(.*) - (.*)");
        Matcher matcher = p.matcher(rep);
        Pattern p2 = Pattern.compile("(.*)\\.([^\\.]+)");
        Matcher matcher2 = p2.matcher(rep);
        if (matcher.matches()) {
            type = matcher.group(1);
            javaPackage = matcher.group(2);
        } else if (matcher2.matches()) {
            javaPackage = matcher2.group(1);
            type = matcher2.group(2);
        } else {
            type = rep;
            isPrimitive = Util.i().isInArray(rep, primTypes);
            isJavaLang = !isPrimitive;
        }
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getFullClassName() { return javaPackage + "." + type; }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    public void setPrimitive(boolean isPrimitive) {
        this.isPrimitive = isPrimitive;
    }

    public boolean isJavaLang() {
        return (javaPackage == null)? isJavaLang: javaPackage.startsWith("java.") || javaPackage.startsWith("javax.");
    }

    public void setJavaLang(boolean isJavaLang) {
        this.isJavaLang = isJavaLang;
    }
    
    public String toString() {
        return getType() + " - " + getJavaPackage();        
    }
}
