package org.maxml.db.types;

import java.util.Collection;


public interface ValueCollectionObject extends ValueObject {

    public Collection getValueCollection();
    public void setValueCollection( Collection valueCollection );
    
}
