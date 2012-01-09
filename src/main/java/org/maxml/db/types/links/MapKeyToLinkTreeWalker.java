package org.maxml.db.types.links;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.maxml.common.AbstractWalker;
import org.maxml.common.VisitException;
import org.maxml.util.Util;

public class MapKeyToLinkTreeWalker extends AbstractWalker {
    private String currName;
    private Map<String,?>    data;

    public MapKeyToLinkTreeWalker(Object root) {
        super(root);
        this.currName = root.toString();
    }

    public MapKeyToLinkTreeWalker(Object root, Map data) {
        super(root);
        this.currName = root.toString();
        this.data = data;
    }

    @Override
    public Collection getChildren(Object obj) throws VisitException {
        ArrayList keys = new ArrayList();
        currName = obj.toString();

        for ( String key: data.keySet() ) {
            int searchIndex = currName.length() + LinkTreeNameMapBuilderVisitor.PARENT_CHILD_SEP.length();

            if (key.startsWith(currName) && key.length() > searchIndex
                    && key.substring(searchIndex).indexOf(LinkTreeNameMapBuilderVisitor.PARENT_CHILD_SEP) == -1) {
                keys.add(key);
            }
        }
        return Util.i().sortCollection(keys);
    }

    @Override
    public void traverse() throws VisitException {
        super.traverseBreadthFirst();
    }

    public Object getContainedObject(Object object) throws VisitException {
        return Util.i().getMapEntry(object, data);
    }

    public Object getParentContainedObject(Object object) throws VisitException {
        return getContainedObject(object);
    }
}
