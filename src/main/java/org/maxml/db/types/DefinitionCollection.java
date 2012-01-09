package org.maxml.db.types;

import java.util.Collection;

public interface DefinitionCollection extends Definition {
    public Collection<Definition> getDefinitionCollection();
    public void setDefinitionCollection(Collection<Definition> definitionCollection);
}
