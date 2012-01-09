package org.maxml.common;

import java.util.Collection;

public interface Walker {

	public Collection getChildren(Object obj) throws VisitException;
	
    public void addVisitor(Visitor visitor);
    public void addVisitors(Collection<Visitor> visitors);

    public void traverse() throws VisitException;
    public void traverseBreadthFirst() throws VisitException;
    public void traverseDepthFirst() throws VisitException;

    public void doVisit(Object target) throws VisitException;
    public void doPreVisitParent(Object target) throws VisitException;
    public void doPostVisitParent(Object target) throws VisitException;

    public Object getContainedObject(Object object) throws VisitException;
    public Object getParentContainedObject(Object object) throws VisitException;

	public int getDepth();
    
}
