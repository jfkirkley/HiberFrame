package org.maxml.db.types.links;

import java.util.ArrayList;
import java.util.Collection;

import org.maxml.db.DBException;
import org.maxml.db.DBObjectAccessor;
import org.maxml.db.DBObjectAccessorFactory;

public class GraphWriter implements OldVisitor {
    
    private Link currentParentLink = null;

    protected DBObjectAccessorFactory objectAccessorFactory = DBObjectAccessorFactory.i();

    public void visit(Link link) throws OldVisitException {
        try {
            if( currentParentLink != null ) {
                link.setObjectAsReferrer(currentParentLink);
            }
            //link.setId(null);
            getAccessor(Link.class).save(link);
        } catch (DBException e) {
            throw new OldVisitException(e);
        }
    }

    public void visit(LinkGraph linkGraph) throws OldVisitException {
        try {
            Link tempLink = linkGraph.getRootLink();
            if( currentParentLink != null ) {
                tempLink.setObjectAsReferrer(currentParentLink);
            }
            //tempLink.setId(null);
            getAccessor(Link.class).save(tempLink);
            getAccessor(LinkGraph.class).save(linkGraph);

            currentParentLink = tempLink;
            
            objectAccessorFactory.flush();
            
        } catch (DBException e) {
            throw new OldVisitException(e);
        }
    }

    public void visitParent(Link parentLink) throws OldVisitException {
        
    }
    
    public DBObjectAccessor getAccessor(Class objectClass) {
        return objectAccessorFactory.getAccessor(objectClass);
    }
    
    public static void write(LinkGraph linkGraph) throws OldVisitException {
        GraphWriter graphWriter = new GraphWriter();
        MemoryWalker.walk(linkGraph, graphWriter);
    }

    public static void write(LinkGraph linkGraph, OldVisitor visitor) throws OldVisitException {
        GraphWriter graphWriter = new GraphWriter();
        ArrayList visitors = new ArrayList();
        visitors.add(graphWriter);
        visitors.add(visitor);
        MemoryWalker.walk(linkGraph, visitors); 
    }

    public static void write(LinkGraph linkGraph, Collection visitorList) throws OldVisitException {
        GraphWriter graphWriter = new GraphWriter();
        ArrayList visitors = new ArrayList();
        visitors.add(graphWriter);
        visitors.addAll(visitorList);
        MemoryWalker.walk(linkGraph, visitors); 
    }
}
