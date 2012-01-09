package org.maxml.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;


public class DirWalker {
    protected String basePath;
    protected FileFilter fileFilter;
    
    List<FileVisitor> fileVisitors;
    
    public DirWalker(String basePath){
        this.basePath = basePath;
        this.fileVisitors = new ArrayList();
    }
    
    public DirWalker(String basePath, FileFilter fileFilter){
        this(basePath);
        this.fileFilter = fileFilter;
    }
    
    public void addFileVisitor(FileVisitor fileVisitor) {
        this.fileVisitors.add(fileVisitor);
    }

    public void walk(){
        walk(new File(basePath));
    }
    
    public void walk(File f){
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            for (int i = 0; i < fl.length; ++i) {
                walk(fl[i]);
            }
        } else if(fileFilter==null || fileFilter.accept(f)){
            for ( FileVisitor fileVisitor: fileVisitors ) {
                fileVisitor.visit(f);
            }
        }
    }
}
