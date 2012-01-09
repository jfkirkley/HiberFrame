package org.maxml.db.types.links;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.maxml.db.DBException;
import org.maxml.util.Util;

public class eCollection implements Collection {

	private Integer id;
	
	private pCollection	readOnlyCollection;
	private pCollection	writableCollection;

	public eCollection(Object linkedToMe) throws DBException {
		writableCollection = new pCollection(linkedToMe);
	}

	public eCollection(Object linkedToMe, boolean lazy) throws DBException {
		writableCollection = new pCollection(linkedToMe, lazy);
	}

	public eCollection(pCollection collection) {
		this.writableCollection = collection;
	}
	
	public eCollection() {
		writableCollection = new pCollection();
	}

	public eCollection(LinkGraph linkGraph) {
		writableCollection = new pCollection(linkGraph);
	}

	public eCollection(LinkGraph parent, boolean lazy) throws DBException {
		writableCollection = new pCollection(parent, lazy);
	}

	public boolean contains(Object o) {
		return writableCollection.contains(o) || (readOnlyCollection != null ? readOnlyCollection.contains(o) : false);
	}

	public Iterator iterator() {
		if (readOnlyCollection != null) {
			ArrayList al = new ArrayList();
			al.addAll(readOnlyCollection);
			al.addAll(writableCollection);
			return al.iterator();
		}
		return writableCollection.iterator();
	}
	
	public static eCollection build() {
		return new eCollection(pCollection.build());
	}

	// public boolean containsAll(Collection c) {
	// return false;
	// }

	public pCollection getReadOnlyCollection() {
		return readOnlyCollection;
	}

	public void setReadOnlyCollection(pCollection readOnlyCollection) {
		this.readOnlyCollection = readOnlyCollection;
	}

	public boolean add(Object arg0) {
		return writableCollection.add(arg0);
	}

	public boolean addAll(Collection arg0) {
		return writableCollection.addAll(arg0);
	}

	public void clear() {
		writableCollection.clear();
	}

	public boolean containsAll(Collection arg0) {
		return writableCollection.containsAll(arg0);
	}


	public boolean isEmpty() {
		return writableCollection.isEmpty() && (readOnlyCollection != null ? readOnlyCollection.isEmpty() : true);
	}

	public boolean remove(Object arg0) {
		return writableCollection.remove(arg0);
	}

	public boolean removeAll(Collection arg0) {
		return writableCollection.removeAll(arg0);
	}

	public boolean retainAll(Collection arg0) {
		return writableCollection.retainAll(arg0);
	}

	public int size() {
		return writableCollection.size() + (readOnlyCollection != null ? readOnlyCollection.size() : 0);
	}
	

	public Object[] toArray() {
		return writableCollection.toArray();
	}

	public Object[] toArray(Object[] arg0) {
		return writableCollection.toArray(arg0);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public pCollection getWritableCollection() {
		return writableCollection;
	}

	public void setWritableCollection(pCollection writableCollection) {
		this.writableCollection = writableCollection;
	}
	
	public Collection getReadObjects() {
		return writableCollection.getReadObjects();
	}

    public boolean equals(Object otherObj) {
        if (otherObj == null)
            return false;
        if (otherObj == this)
            return true;

        if (otherObj instanceof eCollection) {
            eCollection othereCollection = (eCollection) otherObj;
            if (this.id != null && this.id.equals(othereCollection.getId())) {
                return true;
            }
        }
        return false;
    }

}
