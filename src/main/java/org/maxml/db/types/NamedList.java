package org.maxml.db.types;

import java.util.Collection;
import java.util.HashSet;

import org.maxml.db.types.links.pCollection;

public class NamedList {
    
    private transient boolean              persisting            = false;
    private transient HashSet    readPersistentObjects = new HashSet();
   
    private Integer id;
    private String name;
    private pCollection list;
    
    public NamedList() {
    }
    public NamedList(String name) {
        this.name = name;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Collection getList() {
        return list;
    }
    public void setList(Collection l) {
        if (l != null && l instanceof pCollection) {
            list = (pCollection) l;
        } else {
            if (list == null) {
                list = pCollection.build();
            }
            list.addAll(l);
            readPersistentObjects.add(list);
        }
    }

    public String toString() { return name; }
    public HashSet getReadPersistentObjects() {
        return readPersistentObjects;
    }
    public boolean isPersisting() {
        return persisting;
    }
    public void setPersisting(boolean persisting) {
        this.persisting = persisting;
    }
    
    public boolean equals(Object otherObj) {
        if(otherObj == this) return true;
        if(otherObj == null) return false;
        
        if (otherObj instanceof NamedList) {
            NamedList otherNamedList = (NamedList) otherObj;
            if(id!=null && id.equals(otherNamedList.getId())) return true;
            if(otherNamedList.getId() == null && name!=null && name.equals(otherNamedList.getName())) return true;
        }
        
        return false;
    }

}
