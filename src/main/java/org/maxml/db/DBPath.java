package org.maxml.db;

import org.maxml.util.Regex;


public class DBPath {
    
    public final static String ID_SEP= "@";
    public final static String PATH_SEP = "|";
    
    public static class DBPathPart{
        private String name;
        private Integer id;
        
        public DBPathPart(String spec) {
            String [] parts = spec.split(ID_SEP);
            
            this.name = parts[0];
            this.id = new Integer(parts[1]);
        }

        public String getName() {
            return name;
        }

        public Integer getId() {
            return id;
        }
    }
    
    private DBPathPart[] pathParts;
    
    public DBPath(String spec) {
        String [] parts = spec.split("\\" + PATH_SEP);
        this.pathParts = new DBPathPart[parts.length];
        
        for (int i = 0; i < parts.length; i++) {
            this.pathParts[i] = new DBPathPart(parts[i]);
        }            
    }

    public DBPathPart getPathPart(int i) {
        if( i >= 0 && i < pathParts.length) {
            return pathParts[i];
        }
        return null;
    }
    
    public DBPathPart getPathPart(String name) {
        for (int i = 0; i < pathParts.length; i++) {
            if( pathParts[i].getName().equals(name)) {
                return pathParts[i];
            }
        }
        return null;
    }
    

    public DBPathPart getPathPartMatching(String regexStr) {
        Regex regex = new Regex(regexStr);
        
        return getPathPartMatching(regex);
    }
    
    public DBPathPart getPathPartMatching(Regex regex) {
        for (int i = 0; i < pathParts.length; i++) {
            if( regex.doMatch(pathParts[i].getName()) ) {
                return pathParts[i];
            }
        }
        return null;
    }
    
    public DBPathPart getPathPartMatchingPrefix(String prefix) {
        
        for (int i = 0; i < pathParts.length; i++) {
            if( pathParts[i].getName().startsWith(prefix) ) {
                return pathParts[i];
            }
        }
        return null;
    }
    
    public DBPathPart getPathPartMatchingSuffix(String suffix) {
        
        for (int i = 0; i < pathParts.length; i++) {
            if( pathParts[i].getName().endsWith(suffix) ) {
                return pathParts[i];
            }
        }
        return null;
    }
    
    public Integer getPathPartId(int i) {
        DBPathPart pathPart = getPathPart(i);
        return (pathPart!=null) ? pathPart.getId(): null;
    }
        
    public Integer getPathPartId(String name) {
        DBPathPart pathPart = getPathPart(name);
        return (pathPart!=null) ? pathPart.getId(): null;
    }
            
    public Integer getPathPartIdMatching(String regexStr) {
        DBPathPart pathPart = getPathPartMatching(regexStr);
        return (pathPart!=null) ? pathPart.getId(): null;
    }

    public Integer getPathPartIdMatching(Regex regex) {
        DBPathPart pathPart = getPathPartMatching(regex);
        return (pathPart!=null) ? pathPart.getId(): null;
    }

    public Integer getPathPartIdMatchingPrefix(String prefix) {
        DBPathPart pathPart = getPathPartMatchingPrefix(prefix);
        return (pathPart!=null) ? pathPart.getId(): null;
    }
    
    public Integer getPathPartIdMatchingSuffix(String suffix) {
        DBPathPart pathPart = getPathPartMatchingSuffix(suffix);
        return (pathPart!=null) ? pathPart.getId(): null;
    }
        
}
