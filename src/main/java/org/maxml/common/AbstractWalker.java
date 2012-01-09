package org.maxml.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractWalker implements Walker {
	protected Collection<Visitor>	visitors;

	protected Object				root;
	protected int					depth	= 0;

	public AbstractWalker(Object root) {
		this.root = root;
		this.visitors = new ArrayList<Visitor>();
	}

	public abstract void traverse() throws VisitException;

	public abstract Collection getChildren(Object obj) throws VisitException;

	public Object getContainedObject(Object object) throws VisitException {
		return object;
	}

    public Object getParentContainedObject(Object object) throws VisitException {
		return object;
	}

	public void traverseBreadthFirst() throws VisitException {
		doVisit(root);
		traverseBreadthFirst(root);
	}

	public void traverseDepthFirst() throws VisitException {
		doVisit(root);
		traverseDepthFirst(root);
	}

	public void traverseDepthFirst(Object parentContainer) throws VisitException {

		try {
			Collection children = getChildren(parentContainer);

			if (children != null && children.size() > 0) {

				Object parent = getParentContainedObject(parentContainer);
                if( parent != null ) {
                    doPreVisitParent(parent);
                }

				for (Iterator iter = children.iterator(); iter.hasNext();) {
					Object childContainer = iter.next();

					Object child = getContainedObject(childContainer);
					if (child != null) {
						doVisit(child);

						++depth;
						traverseDepthFirst(childContainer);
						--depth;
					}
				}
                
                if( parent != null ) {
                    doPostVisitParent(parent);
                }
                
			}
		} catch (Exception e) {
			throw new VisitException(e);
		}
	}

	public void traverseBreadthFirst(Object parentContainer) throws VisitException {

		++depth;
		try {
			Collection children = getChildren(parentContainer);

			if (children != null && children.size() > 0) {
				
				Object parent = getParentContainedObject(parentContainer);
                if( parent != null ) {
                    doPreVisitParent(parent);
                }
				
				for (Iterator iter = children.iterator(); iter.hasNext();) {
					Object childContainer = iter.next();

					Object child = getContainedObject(childContainer);
					if (child != null) {
						doVisit(child);
					}
				}
				for (Iterator iter = children.iterator(); iter.hasNext();) {
					Object childContainer = iter.next();

					Object child = getContainedObject(childContainer);
					if (child != null) {
						traverseBreadthFirst(childContainer);
 					}
				}

                if( parent != null ) {
                    doPostVisitParent(parent);
                }
            }
		} catch (Exception e) {
			throw new VisitException(e);
		}
		--depth;
	}

	public void addVisitor(Visitor visitor) {
		visitor.setWalker(this);
		visitors.add(visitor);
	}

	public void addVisitors(Collection<Visitor> visitors) {
		for ( Visitor visitor: visitors ) {
			visitor.setWalker(this);
			visitors.add(visitor);
		}
	}

	public void doVisit(Object parent) throws VisitException {
		for ( Visitor visitor: visitors ) {
			visitor.visit(parent);
		}
	}

    public void doPreVisitParent(Object parent) throws VisitException {
        for ( Visitor visitor: visitors ) {
            visitor.preVisitParent(parent);
        }
    }
    
    public void doPostVisitParent(Object Object) throws VisitException {
        for ( Visitor visitor: visitors ) {
            visitor.postVisitParent(Object);
        }
    }

	public static void walk(Walker walker, Object root, Visitor visitor) throws VisitException {
		walker.addVisitor(visitor);
		walker.traverse();
	}

	public static void walk(Walker walker, Object Object, Collection<Visitor> visitors) throws VisitException {
		walker.addVisitors(visitors);
		walker.traverse();
	}

	public static void walkDepthFirst(Walker walker, Object Object, Visitor visitor) throws VisitException {
		walker.addVisitor(visitor);
		walker.traverseDepthFirst();
	}

	public static void walkDepthFirst(Walker walker, Object Object, Collection<Visitor> visitors) throws VisitException {
		walker.addVisitors(visitors);
		walker.traverseDepthFirst();
	}

	public static void walkBreadthFirst(Walker walker, Object Object, Visitor visitor) throws VisitException {
		walker.addVisitor(visitor);
		walker.traverseBreadthFirst();
	}

	public static void walkBreadthFirst(Walker walker, Object Object, Collection<Visitor> visitors)
			throws VisitException {
		walker.addVisitors(visitors);
		walker.traverseBreadthFirst();
	}

	public int getDepth() {
		return depth;
	}

}
