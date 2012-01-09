package org.maxml.db.types.links;

public interface OldVisitor {

    public void visit(Link link) throws OldVisitException;
    public void visitParent(Link parentLink) throws OldVisitException;
    public void visit(LinkGraph linkGraph) throws OldVisitException;
    
}
