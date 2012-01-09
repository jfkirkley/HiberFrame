package org.maxml.db.types.links;

import java.util.Map;

import org.maxml.common.VisitException;
import org.maxml.db.DBException;
import org.maxml.reflect.CachedClass;

public class LinkTreeNameMapBuilderWalker extends LinkTreeWalker {

    public LinkTreeNameMapBuilderWalker(Object root) {
        super(root);
    }

    public static Map walk(Object root, String[] nameProperties) throws VisitException {
        LinkTreeNameMapBuilderWalker linkTreeNameMapBuilderWalker = new LinkTreeNameMapBuilderWalker(root);
        return walk(nameProperties, linkTreeNameMapBuilderWalker);

    }

    public static Map walk(String[] nameProperties, LinkTreeNameMapBuilderWalker linkTreeNameMapBuilderWalker)
            throws VisitException {

        LinkTreeNameMapBuilderVisitor linkTreeNameMapBuilderVisitor = new LinkTreeNameMapBuilderVisitor(nameProperties,
                linkTreeNameMapBuilderWalker);
        linkTreeNameMapBuilderWalker.addVisitor(linkTreeNameMapBuilderVisitor);
        linkTreeNameMapBuilderWalker.traverse();

        return linkTreeNameMapBuilderVisitor.getMap();
    }

    public static Map walk(String[] nameProperties, LinkTreeNameMapBuilderWalker linkTreeNameMapBuilderWalker,
            String namePrefix) throws VisitException {

        LinkTreeNameMapBuilderVisitor linkTreeNameMapBuilderVisitor = new LinkTreeNameMapBuilderVisitor(nameProperties,
                linkTreeNameMapBuilderWalker, namePrefix);
        linkTreeNameMapBuilderWalker.addVisitor(linkTreeNameMapBuilderVisitor);
        linkTreeNameMapBuilderWalker.traverse();

        return linkTreeNameMapBuilderVisitor.getMap();
    }

	public Object getContainedObject(Object object) throws VisitException {
	    try {
	        Object target = LinkHandler.getInstance().getReferredObject((Link) object);
	        CachedClass.deepRead(target);
	        return target;
	    } catch (DBException e) {
	        throw new VisitException(e);
	    } catch (ClassNotFoundException e) {
	        throw new VisitException(e);
	    }
	}

	public Object getParentContainedObject(Object object) throws VisitException {
	    return getContainedObject(object);
	}

}
