package org.maxml.common;

import java.util.Collection;

public interface CollectionSource {

    public Collection getCollection();
    public Collection getCollection(String descriptor);
    public Collection getCollection(Object descriptor);

}
