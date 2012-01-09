package org.maxml.util;

import java.io.File;

public class JavaClassFileBean {
    String fileName; 
    String javaPackage; 
    String className; 
    

    public JavaClassFileBean( File javaFile ) {
        fileName = javaFile.getName();
        javaPackage = Util.i().getPkgName(javaFile.getAbsolutePath());
        className = (fileName.indexOf(".") >= 0) ? fileName.substring(0,
                fileName.indexOf(".")) : fileName;
        
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getJavaPackage() {
        return javaPackage;
    }


    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }


    public String getClassName() {
        return className;
    }
    
    public Class getJavaClass() {
        try {
            return Class.forName(getFullClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getFullClassName(){
        return (javaPackage!=null&&javaPackage.length()>0)?javaPackage + "." + className: className;
    }
}
