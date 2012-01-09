package org.maxml.db.types;

import java.util.HashSet;

import org.maxml.db.types.links.Link;

public class NamedItem {
    private transient boolean persisting            = false;
    private transient HashSet readPersistentObjects = new HashSet();

    private transient Object  item;

    private Link              itemLink;
    private String            name;
    private Integer           id;

    public NamedItem() {
    }

    public NamedItem(Object item, String name) {
        this.name = name;
        setItem(item);
    }

    public Link getItemLink() {
        return itemLink;
    }

    public void setItemLink(Link itemLink) {
        this.itemLink = itemLink;
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

    public Object getItem() {
        if (!persisting && item == null) {
            Link link = getItemLink();
            if (link != null) {
                item = link.getReferentObj();
                readPersistentObjects.add(item);
            }
        }
        return item;
    }

    public void setItem(Object value) {
        Link link = getItemLink();
        if (link == null) {
            link = Link.makeNewLink();
            link.setObjectAsReferent(value);
            setItemLink(link);
        }
        link.saveNewReferent(value);
        readPersistentObjects.add(value);
        this.item = value;
    }

    public String toString() {
        return name;
    }

    public HashSet getReadPersistentObjects() {
        return readPersistentObjects;
    }

    public boolean isPersisting() {
        return persisting;
    }

    public void setPersisting(boolean persisting) {
        this.persisting = persisting;
    }

}
