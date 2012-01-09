package org.maxml.common;

public interface Visitor {

    public void visit(Object target) throws VisitException;
    public void preVisitParent(Object parent) throws VisitException;
    public void postVisitParent(Object parent) throws VisitException;
    public void setWalker(Walker walker);
    public Walker getWalker();
}
