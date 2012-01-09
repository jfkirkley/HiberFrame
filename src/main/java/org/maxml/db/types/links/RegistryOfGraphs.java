package org.maxml.db.types.links;

import java.util.Collection;
import java.util.Iterator;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessorFactory;
import org.maxml.util.ClassUtils;

public class RegistryOfGraphs extends GraphGroup {

    protected DBObjectAccessorFactory objectAccessorFactory = DBObjectAccessorFactory.i();
    private static Object             instance              = null;

    public static RegistryOfGraphs getInstance() {
        return (RegistryOfGraphs) (instance = ClassUtils.i().getSingletonInstance(instance, RegistryOfGraphs.class));
    }

    public RegistryOfGraphs() throws DBException {
        super(null);
        readKey2LinkGraphMap();
    }

    public void readKey2LinkGraphMap() throws DBException {
        try {
            Collection<Link> keys = linkHandler.getLinksByReferentType(Link.getTypeId());
            for ( Link key: keys ) {

                if (!key2LinkGraphMap.containsKey(key)) {
                    Link linkHierachyRootLink = (Link) linkHandler.getReferredObject(key.getReferentId(), Link.class);
                    LinkGraph linkGraph = GraphReader.read(linkHierachyRootLink);

                    key2LinkGraphMap.put(key, linkGraph );
                }
            }
        } catch (OldVisitException e) {
            throw new DBException(e);
        }
    }

    public void add(Link refLink, LinkGraph linkGraph) {
        key2LinkGraphMap.put(refLink, linkGraph);
    }
    
    public void remove(Link refLink ) {
        removeHelper(refLink);
    }
    
}
